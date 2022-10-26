package com.andrey.security;

import com.andrey.db_entities.PasswordEncryptionConfiguration;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.security.jwt.JWTAuthenticationService;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final JWTUtils jwtUtils;

    private final UserDetailsService userProvider;
    private final ChatUserRepository userRepository;

    private final JWTAuthenticationService jwtAuthenticationService;

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
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**","/swagger-ui/**", "/v3/api-docs/**","/configuration/ui/**", "/configuration/security/**", "/webjars/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/admin/**").permitAll() //todo
                .anyRequest()
                .authenticated();

        //JWT filter for authentication
        http
                .addFilterBefore(new JWTFilter(jwtUtils, jwtAuthenticationService, userRepository), BasicAuthenticationFilter.class);

        return http.build();
    }
}
