package com.tw.service;

import com.tw.dto.UserDto;
import com.tw.entity.Users;

public interface UserService {
    Users createUser(UserDto userDto);
}
