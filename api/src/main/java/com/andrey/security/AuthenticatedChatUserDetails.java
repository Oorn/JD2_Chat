package com.andrey.security;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.UserServiceRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class AuthenticatedChatUserDetails implements UserDetails {


    private transient ChatUser chatUser;

    String userEmail;

    String passwordHash;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (chatUser.getUserServiceRole() == null)
            return AuthorityUtils.NO_AUTHORITIES;
        switch (chatUser.getUserServiceRole()) {
            case ADMIN:
                return AuthorityUtils.createAuthorityList(UserServiceRole.ADMIN.name());
            default:
                return AuthorityUtils.NO_AUTHORITIES;
        }
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
