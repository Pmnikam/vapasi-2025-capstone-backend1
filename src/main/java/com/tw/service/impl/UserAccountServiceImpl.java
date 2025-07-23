package com.tw.service.impl;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;
import com.tw.entity.UserAccount;
import com.tw.exception.InvalidUserCredentialsException;
import com.tw.exception.UserAlreadyExistsException;
import com.tw.repository.UserAccountRepository;
import com.tw.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountServiceImpl.class);
    private static final String ROLE = "Customer";
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAccountResponseDto registerUser(RegisterUserDto registerUserDto) {
        LOGGER.info("Attempting to register user with email: {}", registerUserDto.getEmail());
        if (userAccountRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            LOGGER.warn("Registration failed: Email {} already exists", registerUserDto.getEmail());
            throw new UserAlreadyExistsException("Email already exists"); // or custom exception
        }

        String hashedPwd = passwordEncoder.encode(registerUserDto.getPassword());

        UserAccount user = new UserAccount(registerUserDto.getName(), registerUserDto.getEmail(), hashedPwd, ROLE);

        userAccountRepository.save(user);

        LOGGER.info("User registered successfully with email: {}", user.getEmail());

        return new UserAccountResponseDto(user.getLoginId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public UserAccountResponseDto authenticate(AuthenticateUserDto authenticateUserDto) {
        LOGGER.info("Authenticating user with email: {}", authenticateUserDto.getEmail());

        UserAccount user = userAccountRepository.findByEmail(authenticateUserDto.getEmail()).orElseThrow(() -> {
            LOGGER.warn("Authentication failed: Email {} not found", authenticateUserDto.getEmail());
            return new InvalidUserCredentialsException("Invalid email or password");
        });
        if (!passwordEncoder.matches(authenticateUserDto.getPassword(), user.getPassword())) {
            LOGGER.warn("Authentication failed: Password mismatch for email {}", authenticateUserDto.getEmail());
            throw new InvalidUserCredentialsException("Invalid email or password");
        }
        LOGGER.info("User authenticated successfully with email: {}", user.getEmail());
        return new UserAccountResponseDto(user.getLoginId(), user.getName(), user.getEmail(), user.getRole());
    }
}
