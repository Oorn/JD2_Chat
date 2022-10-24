package com.andrey.service.auth;

import com.andrey.controller.requests.AuthenticationRequest;

import java.util.Optional;

public interface AuthenticationService {
    Optional<String> generateAuthToken(AuthenticationRequest request);
    Optional<String> refreshAuthToken(String token);
}
