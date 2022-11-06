package com.andrey.service.cached_user_details;

import com.andrey.Constants;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatUserRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Configuration
public class CachedUserDetailsServiceImpl implements CachedUserDetailsService {
    private final ChatUserRepository userRepository;

    @Override
    @Cacheable(value = Constants.AUTH_USER_CACHE_NAME, key = "#email")
    public ChatUser cachedFindChatUserByEmailWithFriendshipsAndChatMembershipsAndBlocks(String email) {
        return userRepository.findChatUserByEmailWithFriendshipsAndChatMembershipsAndBlocks(email);
    }

    @Override
    @CacheEvict(value = Constants.AUTH_USER_CACHE_NAME, key = "#user.email")
    public void evictUserFromCache(ChatUser user) {

    }
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(Constants.AUTH_USER_CACHE_NAME);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(Constants.AUTH_USER_CACHE_START_SIZE)
                .maximumSize(Constants.AUTH_USER_CACHE_MAX_SIZE)
                .expireAfterAccess(Constants.AUTH_USER_CACHE_LIFETIME, TimeUnit.MILLISECONDS)
                .weakKeys()
                .recordStats());
        return cacheManager;
    }
}
