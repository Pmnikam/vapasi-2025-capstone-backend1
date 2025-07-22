package com.tw.service;


import com.tw.dto.LoginDto;
import com.tw.dto.UserDto;
import com.tw.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;


public interface UserService {
    ResponseEntity<String> signup(UserDto userDto);
    ResponseEntity<String> login(LoginDto loginDto);


}
