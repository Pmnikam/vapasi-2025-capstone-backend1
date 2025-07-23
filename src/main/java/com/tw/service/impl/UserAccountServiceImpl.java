package com.tw.service.impl;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;
import com.tw.entity.UserAccount;
import com.tw.exception.InvalidUserCredentialsException;
import com.tw.exception.UserAlreadyExistsException;
import com.tw.repository.UserAccountRepository;
import com.tw.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final String ROLE = "Customer";
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAccountResponseDto registerUser(RegisterUserDto registerUserDto) {
        if (userAccountRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists"); // or custom exception
        }

        String hashedPwd = passwordEncoder.encode(registerUserDto.getPassword());

        UserAccount user = new UserAccount(registerUserDto.getName(), registerUserDto.getEmail(), hashedPwd, ROLE);

        userAccountRepository.save(user);

        return new UserAccountResponseDto(user.getLoginId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public UserAccountResponseDto authenticate(AuthenticateUserDto authenticateUserDto) {
        UserAccount user = userAccountRepository.findByEmail(authenticateUserDto.getEmail()).orElseThrow(() -> new InvalidUserCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(authenticateUserDto.getPassword(), user.getPassword())) {
            throw new InvalidUserCredentialsException("Invalid email or password");
        }
        return new UserAccountResponseDto(user.getLoginId(), user.getName(), user.getEmail(), user.getRole());
    }
}
