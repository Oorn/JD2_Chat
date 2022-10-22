package com.andrey.security;

import com.andrey.db_entities.PasswordEncryptionConfiguration;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.security.jwt.JWTFilter;
import com.andrey.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final JWTUtils jwtUtils;

    private final UserDetailsService userProvider;
    private final ChatUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    //configure method replacement after deprecation
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //ant matchers
                .antMatchers("/swagger-resources/**","/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .anyRequest()
                .authenticated();

        //JWT filter for authentication
        http
                .addFilterBefore(new JWTFilter(jwtUtils, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userProvider)
                .passwordEncoder(passwordEncoder);
    }
    @Bean
    public JWTFilter authenticationTokenFilterBean(AuthenticationManager authenticationManager) throws Exception {
        JWTFilter authenticationTokenFilter = new JWTFilter(jwtUtils, userRepository);
        authenticationTokenFilter.setAuthenticationManager(authenticationManager);
        return authenticationTokenFilter;
    }
}
