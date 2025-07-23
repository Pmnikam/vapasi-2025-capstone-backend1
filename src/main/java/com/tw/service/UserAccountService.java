package com.tw.service;


import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;


public interface UserAccountService {
    UserAccountResponseDto registerUser(RegisterUserDto userDto);

    UserAccountResponseDto authenticate(AuthenticateUserDto loginDto);


}
