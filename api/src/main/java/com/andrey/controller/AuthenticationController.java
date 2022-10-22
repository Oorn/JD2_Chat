package com.andrey.controller;

import com.andrey.requests.AuthenticationRequest;
import com.andrey.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/generateToken")
    public ResponseEntity<Object> getAuthToken(@RequestBody AuthenticationRequest authRequest){
        if (!authRequest.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<String> response = authenticationService.generateAuthToken(authRequest);

        if (response.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Object> refreshAuthToken(@RequestBody String token){
        Optional<String> response = authenticationService.refreshAuthToken(token);

        if (response.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }
}
