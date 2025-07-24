package com.tw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.dto.LoanAppStatusChangeRequestDto;
import com.tw.dto.LoanAppStatusChangeResponseDto;
import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.exception.UnauthorizedException;
import com.tw.exception.UserNotFoundException;
import com.tw.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanApplicationController.class)
@Import(LoanApplicationControllerTest.TestConfig.class)
class LoanApplicationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoanApplicationService loanApplicationService;

    private static LoanApplicationRequestDto getLoanApplicationRequestDto() {
        LoanApplicationRequestDto requestDto = new LoanApplicationRequestDto();
        requestDto.setDob("1995-06-05");
        requestDto.setMobileNo("9876543210");
        requestDto.setAddress("Mumbai");
        requestDto.setAadharNo("123456789012");
        requestDto.setPanNo("ABCDE1234F");
        requestDto.setLoanAmount(500000.0);
        requestDto.setMonthlyIncome(40000.0);
        requestDto.setPropertyName("Dream House");
        requestDto.setLocation("Mumbai");
        requestDto.setEstimatedCost(600000.0);
        requestDto.setDocumentType("Aadhar");
        requestDto.setTenure(10.0);
        requestDto.setEmi(15000.0);
        return requestDto;
    }

    @Test
    void shouldSubmitLoanApplicationSuccessfully() throws Exception {
        Long userId = 1L;
        Long expectedAppId = 101L;
        LoanApplicationRequestDto requestDto = getLoanApplicationRequestDto();
        when(loanApplicationService.submitApplication(eq(userId), any())).thenReturn(expectedAppId);

        mockMvc.perform(post("/users/{userId}/loan", userId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isCreated()).andExpect(content().string(expectedAppId.toString()));
    }

    @Test
    void shouldReturnLoanApplicationDetailsById() throws Exception {
        Long userId = 1L, appId = 101L;
        LoanApplicationResponseDto responseDto = new LoanApplicationResponseDto();

        when(loanApplicationService.getApplicationById(userId, appId)).thenReturn(responseDto);

        mockMvc.perform(get("/users/{userId}/loan/{applicationId}", userId, appId)).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void shouldReturnAllLoanApplicationsForUser() throws Exception {
        Long userId = 1L;
        List<LoanApplicationResponseDto> responseList = List.of(new LoanApplicationResponseDto());

        when(loanApplicationService.getAllApplicationsByUserId(userId)).thenReturn(responseList);

        mockMvc.perform(get("/users/{userId}/loan", userId)).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(responseList)));
    }

    @Test
    void shouldChangeApplicationStatus() throws Exception {
        Long userId = 1L, appId = 101L;

        LoanAppStatusChangeRequestDto requestDto = new LoanAppStatusChangeRequestDto();
        requestDto.setStatus("APPROVED");

        LoanAppStatusChangeResponseDto responseDto = LoanAppStatusChangeResponseDto.builder().accountId(appId).loanAmount(500000.0).build();
        when(loanApplicationService.changeApplicationStatusById(eq(userId), eq(appId), any())).thenReturn(responseDto);
        mockMvc.perform(put("/users/{userId}/loan/{applicationId}", userId, appId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void shouldDeleteLoanApplicationById() throws Exception {
        Long userId = 1L, appId = 101L;
        when(loanApplicationService.deleteApplicationById(userId, appId)).thenReturn(true);
        mockMvc.perform(delete("/users/{userId}/loan/{applicationId}", userId, appId)).andExpect(status().isOk()).andExpect(content().string("true"));
    }

    @Test
    void shouldReturnNotFoundIfUserNotFound() throws Exception {
        Long userId = 100L;

        LoanApplicationRequestDto requestDto = new LoanApplicationRequestDto();
        requestDto.setDob("1990-01-01");
        requestDto.setMonthlyIncome(40000.0);
        requestDto.setLoanAmount(200000.0);
        requestDto.setAadharNo("123456789012");
        requestDto.setPanNo("ABCDE1234F");
        requestDto.setMobileNo("9876543210");
        requestDto.setAddress("Chennai");
        requestDto.setPropertyName("Dream House");
        requestDto.setLocation("Chennai");
        requestDto.setEstimatedCost(250000.0);
        requestDto.setDocumentType("Sale Deed");
        requestDto.setTenure(15.0);
        requestDto.setEmi(15000.0);

        when(loanApplicationService.submitApplication(eq(userId), any())).thenThrow(new UserNotFoundException("User with id 100 not found"));

        mockMvc.perform(post("/users/{userId}/loan", userId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isNotFound()).andExpect(content().string("User with id 100 not found"));
    }


    @Test
    void shouldReturnUnauthorizedIfAadharLinkedToAnotherUser() throws Exception {
        Long userId = 1L;
        LoanApplicationRequestDto requestDto = new LoanApplicationRequestDto();
        requestDto.setDob("1990-01-01");
        requestDto.setMonthlyIncome(40000.0);
        requestDto.setLoanAmount(200000.0);
        requestDto.setAadharNo("123456789012");
        requestDto.setPanNo("ABCDE1234F");
        requestDto.setMobileNo("9876543210");
        requestDto.setAddress("Chennai");
        requestDto.setPropertyName("Dream House");
        requestDto.setLocation("Chennai");
        requestDto.setEstimatedCost(250000.0);
        requestDto.setDocumentType("Sale Deed");
        requestDto.setTenure(15.0);
        requestDto.setEmi(15000.0);

        when(loanApplicationService.submitApplication(eq(userId), any())).thenThrow(new UnauthorizedException("Aadhar number already linked to another user"));

        mockMvc.perform(post("/users/{userId}/loan", userId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto))).andExpect(status().isUnauthorized()).andExpect(content().string("Aadhar number already linked to another user"));
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public LoanApplicationService loanApplicationService() {
            return mock(LoanApplicationService.class);
        }
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }


}
