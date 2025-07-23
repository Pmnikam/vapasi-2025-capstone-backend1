package com.tw.service.impl;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.entity.UserAccount;
import com.tw.repository.UserAccountRepository;
import com.tw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ResponseEntity<String> registerUser(RegisterUserDto registerUserDto) {
        if (userAccountRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        String hashedPwd = passwordEncoder.encode(registerUserDto.getPassword());
        UserAccount user = new UserAccount(registerUserDto.getName(),registerUserDto.getEmail(),
                hashedPwd, registerUserDto.getRole());


        userAccountRepository.save(user);
        return ResponseEntity.ok("Signup successful");
    }

    @Override
    public ResponseEntity<String> authenticate(AuthenticateUserDto authenticateUserDto) {
        try {
            UserAccount user = userAccountRepository.findByEmail(authenticateUserDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));

            if (!passwordEncoder.matches(authenticateUserDto.getPassword(), user.getPassword())) {
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
