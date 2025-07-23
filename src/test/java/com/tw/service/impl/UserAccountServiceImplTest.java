package com.tw.service.impl;

import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;
import com.tw.entity.UserAccount;
import com.tw.exception.InvalidUserCredentialsException;
import com.tw.exception.UserAlreadyExistsException;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAccountServiceImplTest {
    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateNewUserWhenValidDetailsProvided() {
        RegisterUserDto dto = new RegisterUserDto("John", "john@example.com", "password123");
        when(userAccountRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");

        UserAccount savedUser = new UserAccount("John", "john@example.com", "hashedPassword", "Customer");
        savedUser.setLoginId(1L);

        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        UserAccountResponseDto response = userAccountService.registerUser(dto);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        RegisterUserDto dto = new RegisterUserDto("John", "john@example.com", "password123");
        when(userAccountRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new UserAccount()));

        assertThrows(UserAlreadyExistsException.class, () -> userAccountService.registerUser(dto));
    }

    @Test
    void shouldAuthenticateUserWithValidCredentials() {
        AuthenticateUserDto dto = new AuthenticateUserDto("john@example.com", "password123");
        UserAccount user = new UserAccount("John", "john@example.com", "hashedPassword", "Customer");
        user.setLoginId(1L);

        when(userAccountRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), "hashedPassword")).thenReturn(true);

        UserAccountResponseDto response = userAccountService.authenticate(dto);

        assertEquals("john@example.com", response.getEmail());
        assertEquals("Customer", response.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        AuthenticateUserDto dto = new AuthenticateUserDto("john@example.com", "password123");
        when(userAccountRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidUserCredentialsException.class, () -> userAccountService.authenticate(dto));
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        AuthenticateUserDto dto = new AuthenticateUserDto("john@example.com", "wrongPassword");
        UserAccount user = new UserAccount("John", "john@example.com", "hashedPassword", "Customer");

        when(userAccountRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidUserCredentialsException.class, () -> userAccountService.authenticate(dto));
    }

  
}