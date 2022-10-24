package com.andrey.controller;

import com.andrey.controller.requests.AuthenticationRequest;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/generateToken")
    @Operation(summary = "generates authentication JWT token")
    public ResponseEntity<Object> getAuthToken(@RequestBody AuthenticationRequest authRequest){

        Optional<String> response = authenticationService.generateAuthToken(authRequest);

        if (response.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTPropertiesConfig.AUTH_TOKEN_HEADER, response.get());
        return new ResponseEntity<>(response.get(),
                headers,
                HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "refreshes expiration date on JWT token. Every request requiring authentication automatically refreshes token in header")
    public ResponseEntity<Object> refreshAuthToken(@RequestBody
                                                       @NotBlank @NotNull String token){
        Optional<String> response = authenticationService.refreshAuthToken(token);

        if (response.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTPropertiesConfig.AUTH_TOKEN_HEADER, response.get());
        return new ResponseEntity<>(response.get(),
                headers,
                HttpStatus.OK);
    }
}
