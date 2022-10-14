package com.andrey.controller;

import com.andrey.repository.user.UserRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestRestController {

    private final UserRepositoryInterface userRepositoryInterface;

    @GetMapping("/ping")
    public ResponseEntity<Object> ping(){
        return new ResponseEntity<>(userRepositoryInterface.findById(1L), HttpStatus.OK);
    }
}
