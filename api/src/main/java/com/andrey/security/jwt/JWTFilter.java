package com.andrey.security.jwt;

import com.andrey.Constants;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;

import com.andrey.security.AuthenticatedChatUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JWTFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtils tokenUtils;
    private final JWTAuthenticationService jwtAuthenticationService;

    private final ChatUserRepository userRepository;



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String authToken = httpRequest.getHeader(JWTPropertiesConfig.AUTH_TOKEN_HEADER);

        if (authToken != null) {
            if (tokenUtils.validateToken(authToken)) {
                String username = tokenUtils.getUsernameFromToken(authToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    AuthenticatedChatUserDetails authUser = (AuthenticatedChatUserDetails) jwtAuthenticationService.loadUserByUsername(username);
                    ChatUser user = authUser.getChatUser();
                    if (user.getPasswordResetDate()
                            .before(tokenUtils
                                    .getCreationDate(authToken))
                    && (user.getStatus().equals(UserStatus.OK))) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        Optional<String> newToken = tokenUtils.refreshToken(authToken);
                        newToken.ifPresent(s -> httpServletResponse.setHeader(JWTPropertiesConfig.AUTH_TOKEN_HEADER, s));
                    }
                }
            }
        }
        chain.doFilter(request, httpServletResponse);
    }

}
