package com.tw.service.impl;


import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.entity.UserAccount;
import com.tw.exception.LoanInEligibilityException;
import com.tw.exception.UnauthorizedException;
import com.tw.exception.UserNotFoundException;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.UserAccountRepository;
import com.tw.util.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoanApplicationServiceImplTest {

    @InjectMocks
    private LoanApplicationServiceImpl loanApplicationService;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    private LoanApplicationRequestDto requestDto;
    private UserAccount user;
    private CustomerProfile profile;
    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = LoanApplicationRequestDto.builder().aadharNo("123456789012").panNo("ABCDE1234F").dob("1990-01-01").mobileNo("9876543210").address("Some Address").monthlyIncome(50000.0).loanAmount(1000000.0).propertyName("Dream Home").location("City A").estimatedCost(1200000.0).documentType("ID Proof").build();

        user = UserAccount.builder().loginId(1L).build();

        profile = CustomerProfile.builder().pId(1L).aadharNo("123456789012").loginAccount(user).build();

        loanApplication = LoanApplication.builder().applicationId(100L).loanAmount(1000000.0).loanStatus("Pending Verification").isActive(true).customerProfile(profile).build();
    }

    @Test
    void shouldSubmitApplicationSuccessfully() {
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(1L)).thenReturn(Collections.emptyList());
        when(customerProfileRepository.findByAadharNo("123456789012")).thenReturn(Optional.of(profile));
        when(customerProfileRepository.save(any(CustomerProfile.class))).thenAnswer(i -> {
            CustomerProfile savedProfile = i.getArgument(0);
            savedProfile.getLoanApplications().get(0).setApplicationId(100L);
            return savedProfile;
        });

        Long appId = loanApplicationService.submitApplication(1L, requestDto);

        assertNotNull(appId);
        assertEquals(100L, appId);
    }

    @Test
    void shouldThrowExceptionWhenSubmittingApplicationWithNonExistentUser() {
        when(userAccountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> loanApplicationService.submitApplication(1L, requestDto));
    }

    @Test
    void shouldThrowExceptionWhenLoanAmountExceedsEligibility() {
        requestDto.setLoanAmount(99999999.0);
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(LoanInEligibilityException.class, () -> loanApplicationService.submitApplication(1L, requestDto));
    }

    @Test
    void shouldReturnApplicationDetailsWhenValidIdProvided() {
        loanApplication.setCustomerProfile(profile);
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(loanApplication));
        String dobStr = LocalDate.now().minusYears(30).toString();
        profile.setDob(LocalDate.parse(dobStr));
        profile.setLoginAccount(user);

        LoanApplicationResponseDto response = loanApplicationService.getApplicationById(1L, 100L);

        assertEquals("Pending Verification", response.getStatus());
    }

    @Test
    void shouldReturnAllApplicationsForGivenUserId() {
        Long userId = 1L;
        CustomerProfile mockProfile = new CustomerProfile();
        mockProfile.setDob(LocalDate.of(1990, 1, 1));
        mockProfile.setMobileNo("9876543210");
        mockProfile.setAddress("123 Main St");
        mockProfile.setAadharNo("123412341234");
        mockProfile.setPanNo("ABCDE1234F");

        LoanApplication loanApp = new LoanApplication();
        loanApp.setApplicationId(100L);
        loanApp.setCustomerProfile(mockProfile);
        loanApp.setLoanAmount(500000.0);
        loanApp.setMonthlyIncome(50000.0);
        loanApp.setPropertyName("Dream Home");
        loanApp.setLocation("Mumbai");
        loanApp.setEstimatedCost(600000.0);
        loanApp.setDocumentType("Aadhar, PAN");
        loanApp.setLoanStatus("Pending");
        loanApp.setTenure(10.0);
        loanApp.setEmi(5400.0);

        List<LoanApplication> loanApps = List.of(loanApp);

        when(loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(userId)).thenReturn(loanApps);

        List<LoanApplicationResponseDto> result = loanApplicationService.getAllApplicationsByUserId(userId);

        assertEquals(1, result.size());

        LoanApplicationResponseDto dto = result.get(0);
        assertEquals(100L, dto.getApplicationNo());
        assertEquals("1990-01-01", dto.getDob());
        assertEquals("9876543210", dto.getMobileNo());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals("123412341234", dto.getAadharNo());
        assertEquals("ABCDE1234F", dto.getPanNo());
        assertEquals(500000.0, dto.getLoanAmount());
        assertEquals(50000.0, dto.getMonthlyIncome());
        assertEquals("Dream Home", dto.getPropertyName());
        assertEquals("Mumbai", dto.getLocation());
        assertEquals(600000.0, dto.getEstimatedCost());
        assertEquals("Aadhar, PAN", dto.getDocumentSubmitted());
        assertEquals("Pending", dto.getStatus());
        assertEquals(10, dto.getTenure());
        assertEquals(AppConstant.INTEREST_RATE, dto.getInterestRate());
        assertEquals(5400.0, dto.getEmi());
    }


    @Test
    void shouldDeleteApplicationSuccessfullyById() {
        loanApplication.setCustomerProfile(profile);
        profile.setLoginAccount(user);

        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(loanApplication));

        Boolean result = loanApplicationService.deleteApplicationById(1L, 100L);
        assertTrue(result);
        assertFalse(loanApplication.getIsActive());
    }

    @Test
    public void shouldThrowExceptionWhenSubmittingApplicationWithInvalidDate() {
        Long userId = 1L;
        LoanApplicationRequestDto requestDto = new LoanApplicationRequestDto();
        requestDto.setDob("31-31-2020");
        requestDto.setMonthlyIncome(50000.0);
        requestDto.setLoanAmount(100000.0);
        requestDto.setAadharNo("123456789012");
        requestDto.setPanNo("ABCDE1234F");
        requestDto.setMobileNo("9876543210");
        requestDto.setAddress("Test Address");

        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(new UserAccount()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loanApplicationService.submitApplication(userId, requestDto);
        });

        assertEquals("Invalid date format for DOB", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAadharIsLinkedToAnotherUser() {
        CustomerProfile anotherProfile = CustomerProfile.builder().loginAccount(UserAccount.builder().loginId(2L).build()).build();
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(customerProfileRepository.findByAadharNo("123456789012")).thenReturn(Optional.ofNullable(anotherProfile));

        assertThrows(UnauthorizedException.class, () -> loanApplicationService.submitApplication(1L, getValidDto()));
    }

    private LoanApplicationRequestDto getValidDto() {
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();
        dto.setAadharNo("123456789012");
        dto.setPanNo("ABCDE1234F");
        dto.setDob("1990-01-01");
        dto.setMobileNo("9999999999");
        dto.setAddress("Address");
        dto.setLoanAmount(100000.0);
        dto.setMonthlyIncome(50000.0);
        dto.setPropertyName("Flat");
        dto.setLocation("Bangalore");
        dto.setEstimatedCost(120000.0);
        dto.setDocumentType("PDF");
        dto.setTenure(12.0);
        dto.setEmi(8500.0);
        return dto;

    }

    @Test
    void shouldThrowExceptionWhenLoanAlreadyInProgressForUser() {
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(customerProfileRepository.findByAadharNo("123456789012")).thenReturn(Optional.ofNullable(profile));

        LoanApplication pendingApp = LoanApplication.builder().loanStatus("Pending Customer Approval").build();
        when(loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(1L)).thenReturn(List.of(pendingApp));

        assertThrows(LoanInEligibilityException.class, () -> loanApplicationService.submitApplication(1L, getValidDto()));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnApplication() {
        loanApplication.getCustomerProfile().getLoginAccount().setLoginId(2L);
        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApplication));
        assertThrows(UnauthorizedException.class, () -> loanApplicationService.getApplicationById(1L, 10L));
    }


}










