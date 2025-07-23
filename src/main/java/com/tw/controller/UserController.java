package com.tw.controller;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserResponseDto;
import com.tw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/signup" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserResponseDto user= userService.registerUser(registerUserDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/login" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponseDto> login(@Valid @RequestBody AuthenticateUserDto authenticateUserDto) {
        UserResponseDto user= userService.authenticate(authenticateUserDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
