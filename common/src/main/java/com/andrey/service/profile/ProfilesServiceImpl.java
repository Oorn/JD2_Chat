package com.andrey.service.profile;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.exceptions.BadRequestException;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.exceptions.TooManyEntitiesException;
import com.andrey.repository.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfilesServiceImpl implements ProfilesService{

    private final ChatUserRepository userRepository;

    private final ChatProfileRepository profileRepository;

    private final ChatUserUtilsService userUtils;

    private final EntityManager entityManager;

    private static final int RANDOM_SAMPLE_SIZE = 100;

    @Override
    public Optional<ChatProfile> createNewProfile(ChatProfile newProfile, ChatUser user) {

        if (newProfile.getProfileVisibilityMatchmaking() == ProfileVisibilityMatchmaking.DEFAULT_PROFILE_SEARCH_VISIBILITY)
            throw new BadRequestException("invalid profileVisibilityMatchmaking");
        if (newProfile.getProfileVisibilityUserInfo() == ProfileVisibilityUserInfo.DEFAULT_PROFILE_USER_INFO_VISIBILITY)
            throw new BadRequestException("invalid ProfileVisibilityUserInfo");

        ChatUser persistUser = userRepository.findChatUserById(user.getId());
        if (userUtils.getActiveProfileNumber(persistUser) >= Constants.MAX_PROFILES_PER_USER)
            throw new TooManyEntitiesException("exceeding maximum allowed number if profiles per user of " + Constants.MAX_PROFILES_PER_USER);

        newProfile.setOwner(user);

        newProfile = profileRepository.saveAndFlush(newProfile);
        return Optional.of(newProfile);
    }

    @Override
    public Optional<ChatProfile> updateProfile(ChatProfile newProfile, ChatUser user) {
        if (newProfile == null)
            throw new BadRequestException("new Profile cannot be null");
        if (!newProfile.isInteractable())
            throw new RemovedEntityException("Profile with id " + newProfile.getId() + " has been removed");
        if (newProfile.getProfileVisibilityMatchmaking() == ProfileVisibilityMatchmaking.DEFAULT_PROFILE_SEARCH_VISIBILITY)
            throw new BadRequestException("invalid profileVisibilityMatchmaking");
        if (newProfile.getProfileVisibilityUserInfo() == ProfileVisibilityUserInfo.DEFAULT_PROFILE_USER_INFO_VISIBILITY)
            throw new BadRequestException("invalid ProfileVisibilityUserInfo");

        if (!newProfile.getOwner().getId().equals(user.getId()))
            throw new NoPermissionException("user with id " + user.getId() + " doesn't own profile with id " + newProfile.getId());

        //Optional<ChatProfile> oldProfile = profileRepository.findChatProfileByIdWithOwner(newProfile.getId());
        //if (oldProfile.isEmpty())
        //    return Optional.empty();
        //if (!oldProfile.get().getOwner().getId().equals(user.getId()))
        //    return Optional.empty();

        //newProfile.setOwner(user);

        newProfile = profileRepository.saveAndFlush(newProfile);
        return Optional.of(newProfile);

    }

    @Override
    public Optional<ChatProfile> deleteProfile(Long deleteId, ChatUser user) {
        Optional<ChatProfile> optionalOldProfile = profileRepository.findChatProfileByIdWithOwnerWithMembershipsWithChannelWithProfile(deleteId);
        if (optionalOldProfile.isEmpty())
            throw new NoSuchEntityException("Profile with id " + deleteId + " does not exist");
        ChatProfile oldProfile = optionalOldProfile.get();
        if (!oldProfile.isInteractable())
            throw new RemovedEntityException("Profile with id " + deleteId + " has been deleted");
        if (!oldProfile.getOwner().getId().equals(user.getId()))
            throw new NoPermissionException("user with id " + user.getId() + " doesn't own profile with id " + deleteId);

        oldProfile.setStatus(ProfileStatus.REMOVED);
        removeProfileChats(oldProfile);
        profileRepository.saveAndFlush(oldProfile);
        return Optional.of(oldProfile);
    }

    //requires loaded user owner with loaded channel memberships with channels and profiles
    private void removeProfileChats(ChatProfile profile) {

        profile.getOwner().getChannelMemberships().values().stream()
                .filter(cm -> cm.getChannel().getChannelType() == ChannelType.PRIVATE_CHAT_FROM_PROFILE)
                .filter(cm -> cm.getUserProfile().getId().equals(profile.getId()))
                .map(ChatChannelMembership::getChannel)
                .forEach(c -> {
                    c.setStatus(ChannelStatus.REMOVED);
                    c.setStatusReason("removed by chain remove of profile " + profile.getId());
                });
    }

    @Override
    public boolean checkProfileVisibility(ChatProfile profile, ChatUser viewingUser) {
        switch (profile.getProfileVisibilityUserInfo()) {
            case PUBLIC:
                return true;
            case FRIENDS:
                return userUtils.checkFriendship(viewingUser, profile.getOwner());
            case HIDDEN:
                return viewingUser.getId().equals(
                        profile.getOwner().getId()
                );
            case DEFAULT_PROFILE_USER_INFO_VISIBILITY:
                return false;
        }
        return false;
    }

    @Override
    public List<ChatProfile> getOwnedProfiles(ChatUser authUser) {
        Optional<ChatUser> optionalUser = userRepository.findChatUserByIdWithProfiles(authUser.getId());

        if (optionalUser.isEmpty()) //should never be true
            throw new IllegalStateException("authUser not found");
        ChatUser user = optionalUser.get();

        entityManager.detach(user);

        return user.getOwnedProfiles().stream()
                .filter(ChatProfile::isInteractable)
                .collect(Collectors.toList());

    }

    @Override
    public List<ChatProfile> getRandomMatchmakingProfiles(ChatUser authUser, int amount) {
        if (amount > Constants.MAX_MATCHMAKING_PROFILES_PER_RESPONSE)
            amount = Constants.MAX_MATCHMAKING_PROFILES_PER_RESPONSE;

        return profileRepository.getRandomMatchmakingProfilesForUserWithSample(amount, authUser.getId(), RANDOM_SAMPLE_SIZE);
    }
}
