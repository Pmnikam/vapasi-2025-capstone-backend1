package com.tw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.dto.AuthenticateUserDto;
import com.tw.dto.RegisterUserDto;
import com.tw.dto.UserAccountResponseDto;
import com.tw.exception.InvalidUserCredentialsException;
import com.tw.exception.UserAlreadyExistsException;
import com.tw.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountService userService;

    @BeforeEach
    void setup() {
        Mockito.reset(userService);
    }

    @Test
    void shouldRegisterUserAndReturn200Ok() throws Exception {
        RegisterUserDto dto = new RegisterUserDto("john", "john@example.com", "Customer");
        UserAccountResponseDto response = new UserAccountResponseDto(1L, "John", "john@example.com", "Customer");

        Mockito.when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Signup successful")).andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void shouldReturn400BadRequestForInvalidInputOnSignup() throws Exception {
        RegisterUserDto invalidDto = new RegisterUserDto("", "invalidEmail", "");

        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidDto))).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409ConflictWhenEmailAlreadyExistsOnSignup() throws Exception {
        RegisterUserDto dto = new RegisterUserDto("John", "john@example.com", "Customer");
        Mockito.when(userService.registerUser(any())).thenThrow(new UserAlreadyExistsException("Email already exists"));
        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isConflict()) // or .is(HttpStatus.CONFLICT.value())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void shouldReturn400BadRequestWhenRequestBodyIsNullOnSignup() throws Exception {
        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400BadRequestWhenPasswordIsTooShortOnSignup() throws Exception {
        RegisterUserDto dto = new RegisterUserDto("John", "john@example.com", "123");
        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.password").value("Password is required and must be at least 6 characters long"));
    }

    @Test
    void shouldReturn400BadRequestWhenNameIsMissingOnSignup() throws Exception {
        RegisterUserDto dto = new RegisterUserDto("", "john@example.com", "password123");
        mockMvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.name").value("Name is required"));
    }


    @Test
    void shouldAuthenticateUserAndReturnUserDetailsOnLogin() throws Exception {
        AuthenticateUserDto dto = new AuthenticateUserDto("john@example.com", "password123");
        UserAccountResponseDto response = new UserAccountResponseDto(1L, "John", "john@example.com", "Customer");
        Mockito.when(userService.authenticate(any())).thenReturn(response);
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk()).andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void shouldReturn401UnauthorizedForInvalidCredentialsOnLogin() throws Exception {
        AuthenticateUserDto dto = new AuthenticateUserDto("wrong@example.com", "wrongpassword");
        Mockito.when(userService.authenticate(any())).thenThrow(new InvalidUserCredentialsException("Invalid email or password"));
        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid email or password"));
    }

    @Test
    void shouldReturn400BadRequestForInvalidEmailFormatOnLogin() throws Exception {
        AuthenticateUserDto dto = new AuthenticateUserDto("not-an-email", "password123");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400BadRequestWhenEmailAndPasswordAreBlankOnLogin() throws Exception {
        AuthenticateUserDto dto = new AuthenticateUserDto("", "");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.email").value("Email is required")).andExpect(jsonPath("$.password").value("Password is required and must be at least 6 characters long"));
    }

    @Test
    void shouldReturn400BadRequestForInvalidEmailAndShortPasswordOnLogin() throws Exception {
        AuthenticateUserDto dto = new AuthenticateUserDto("invalid", "123");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.email").value("Invalid email format")).andExpect(jsonPath("$.password").value("Password is required and must be at least 6 characters long"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserAccountService userAccountService() {
            return Mockito.mock(UserAccountService.class);
        }
    }
}
