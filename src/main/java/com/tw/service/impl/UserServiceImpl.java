package com.tw.service.impl;

import com.tw.dto.UserDto;
import com.tw.entity.Users;
import com.tw.repository.UserRepository;
import com.tw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users createUser(UserDto userDto) {
        Users user = new Users();
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        return userRepository.save(user);
    }
}
