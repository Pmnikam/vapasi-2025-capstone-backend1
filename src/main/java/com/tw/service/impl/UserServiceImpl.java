package com.tw.service.impl;

import com.tw.dto.LoginDto;
import com.tw.dto.UserDto;
import com.tw.entity.Users;
import com.tw.repository.UserRepository;
import com.tw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> signup(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        String hashedPwd = passwordEncoder.encode(userDto.getPassword());
        Users user = new Users(userDto.getName(), userDto.getEmail(), hashedPwd, userDto.getRole());
        userRepository.save(user);
        return ResponseEntity.ok("Signup successful");
    }

    @Override
    public ResponseEntity<String> login(LoginDto loginDto) {
        try {
            Users user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            return ResponseEntity.ok("Login successful!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong!");
        }
    }
}
