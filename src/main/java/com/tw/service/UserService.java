package com.tw.service;


import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserResponseDto;


public interface UserService {
    UserResponseDto registerUser(RegisterUserDto userDto);

    UserResponseDto authenticate(AuthenticateUserDto loginDto);


}
