package com.tw.controller;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;
import com.tw.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserAccountController {
    @Autowired
    private UserAccountService userService;

    @PostMapping(value = "/signup" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserAccountResponseDto> signup(@Valid @RequestBody RegisterUserDto registerUserDto) {
        UserAccountResponseDto user= userService.registerUser(registerUserDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/login" , consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserAccountResponseDto> login(@Valid @RequestBody AuthenticateUserDto authenticateUserDto) {
        UserAccountResponseDto user= userService.authenticate(authenticateUserDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
