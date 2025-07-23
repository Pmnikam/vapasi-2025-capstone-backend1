package com.tw.service;


import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import org.springframework.http.ResponseEntity;


public interface UserService {
    ResponseEntity<String> registerUser(RegisterUserDto userDto);
    ResponseEntity<String> authenticate(AuthenticateUserDto loginDto);


}
