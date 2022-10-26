package com.andrey.service.user;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.profile.ProfilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserServiceImpl implements ChatUserService{

    private final ChatUserRepository userRepository;

    private final EntityManager entityManager;

    private final ProfilesService profilesService;

    @Override
    public Optional<ChatUser> findChatUserByIdWithProfiles(Long id) {
        return userRepository.findChatUserByIdWithProfiles(id);
    }

    @Override
    public Optional<ChatUser> getUserInfoForViewer(Long userId, ChatUser viewer) {
        Optional<ChatUser> optionalUser = findChatUserByIdWithProfiles(userId);
        if (optionalUser.isEmpty())
            return optionalUser;
        if (!optionalUser.get().isInteractable())
            return Optional.empty();

        entityManager.detach(optionalUser.get());
        optionalUser.get().setOwnedProfiles(
                optionalUser.get().getOwnedProfiles().stream()
                        .filter(ChatProfile::isInteractable)
                        .filter(p -> profilesService.checkProfileVisibility(p,viewer))
                        .collect(Collectors.toSet())
        );

        return optionalUser;
    }
}
