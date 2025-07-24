package com.tw.controller;

import com.tw.dto.CustomerLoanInfoDto;
import com.tw.service.AdminService;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import({AdminControllerTest.TestAdminConfig.class, AdminControllerTest.TestSecurityConfig.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Test
    void shouldReturnSuccessMessageWhenLoanIsApprovedByAdmin() throws Exception {
        Long customerId = 1L;
        Long loanId = 100L;
        String action = "approve";
        String message = "Loan with ID 100 has been approved successfully for customer ID 1";

        when(adminService.processLoanDecision(customerId, loanId, action)).thenReturn(message);

        mockMvc.perform(put("/admin/users/{customerId}/loan/{loanId}", customerId, loanId).param("action", action).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(message));
    }

    @Test
    void shouldReturnSuccessMessageWhenLoanIsRejectedByAdmin() throws Exception {
        Long customerId = 2L;
        Long loanId = 200L;
        String action = "reject";
        String message = "Loan with ID 200 has been rejected successfully for customer ID 2";

        when(adminService.processLoanDecision(customerId, loanId, action)).thenReturn(message);

        mockMvc.perform(put("/admin/users/{customerId}/loan/{loanId}", customerId, loanId).param("action", action).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(message));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidLoanActionProvidedByAdmin() throws Exception {
        Long customerId = 1L;
        Long loanId = 100L;
        String action = "invalid";

        when(adminService.processLoanDecision(customerId, loanId, action)).thenThrow(new IllegalArgumentException("Invalid action"));

        mockMvc.perform(put("/admin/users/{customerId}/loan/{loanId}", customerId, loanId).param("action", action).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(content().string("Invalid action"));
    }

    @Test
    void shouldReturnListOfCustomerLoanInfoWhenRequestedByAdmin() throws Exception {
        List<CustomerLoanInfoDto> mockList = List.of(new CustomerLoanInfoDto(1L, "John", "john@example.com", 1L, 100L, LocalDate.of(1990, 1, 1), "9876543210", "Mumbai", "123456789012", "ABCDE1234F", 300000.0, 40000.0, "Dream House", "Mumbai", 350000.0, "Aadhar", "PENDING", 10.0, 12000.0));

        when(adminService.getAllCustomerLoanInfo()).thenReturn(mockList);

        mockMvc.perform(get("/admin/users")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].email").value("john@example.com")).andExpect(jsonPath("$[0].loanAmount").value(300000.0));
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
        when(adminService.getAllCustomerLoanInfo()).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/admin/users")).andExpect(status().isInternalServerError());
    }


    @TestConfiguration
    static class TestAdminConfig {
        @Bean
        public AdminService adminService() {
            return mock(AdminService.class);
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
