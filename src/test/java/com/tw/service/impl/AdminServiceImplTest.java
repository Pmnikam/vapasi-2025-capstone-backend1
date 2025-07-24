package com.tw.service.impl;

import com.tw.dto.CustomerLoanInfoDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.exception.InvalidLoanStatusException;
import com.tw.exception.LoanApplicationNotFoundException;
import com.tw.exception.LoanInactiveException;
import com.tw.exception.LoanNotFoundException;
import com.tw.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private LoanApplication loanApp;

    @BeforeEach
    void setup() {
        CustomerProfile profile = CustomerProfile.builder().pId(1L).build();
        loanApp = LoanApplication.builder().applicationId(100L).loanStatus("Pending Admin Approval").isActive(true).customerProfile(profile).build();
    }

    @Test
    void shouldReturnAllCustomerLoanInfoList() {
        List<CustomerLoanInfoDto> list = List.of(mock(CustomerLoanInfoDto.class));
        when(loanApplicationRepository.fetchAllCustomerLoanApplications()).thenReturn(list);

        List<CustomerLoanInfoDto> result = adminService.getAllCustomerLoanInfo();

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldThrowExceptionWhenNoLoanInfoFound() {
        when(loanApplicationRepository.fetchAllCustomerLoanApplications()).thenReturn(Collections.emptyList());

        assertThrows(LoanNotFoundException.class, () -> adminService.getAllCustomerLoanInfo());
    }

    @Test
    void shouldUpdateLoanStatusWhenApproved() {
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.of(loanApp));

        String message = adminService.processLoanDecision(1L, 100L, "approve");

        assertThat(message).isEqualTo("Loan with ID 100 has been Approved successfully for customer ID 1");
        assertThat(loanApp.getLoanStatus()).isEqualTo("Pending Customer Approval");
        verify(loanApplicationRepository).save(loanApp);
    }

    @Test
    void shouldUpdateLoanStatusWhenRejected() {
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.of(loanApp));

        String message = adminService.processLoanDecision(1L, 100L, "reject");

        assertThat(message).isEqualTo("Loan with ID 100 has been Rejected successfully for customer ID 1");
        assertThat(loanApp.getLoanStatus()).isEqualTo("Rejected");
    }

    @Test
    void shouldThrowExceptionWhenLoanIsInactive() {
        loanApp.setIsActive(false);
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.of(loanApp));

        assertThrows(LoanInactiveException.class, () -> adminService.processLoanDecision(1L, 100L, "approve"));
    }

    @Test
    void shouldThrowExceptionForInvalidLoanStatus() {
        loanApp.setLoanStatus("Approved");
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.of(loanApp));

        assertThrows(InvalidLoanStatusException.class, () -> adminService.processLoanDecision(1L, 100L, "approve"));
    }

    @Test
    void shouldThrowExceptionForUnsupportedAction() {
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.of(loanApp));

        assertThrows(IllegalArgumentException.class, () -> adminService.processLoanDecision(1L, 100L, "invalid"));
    }

    @Test
    void shouldThrowExceptionWhenLoanNotFound() {
        when(loanApplicationRepository.findByApplicationIdAndCustomerProfile_pId(100L, 1L)).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class, () -> adminService.processLoanDecision(1L, 100L, "approve"));
    }


}
