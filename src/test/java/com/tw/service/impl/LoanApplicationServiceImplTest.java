
package com.tw.service.impl;

import com.tw.dto.LoanAppStatusChangeRequestDto;
import com.tw.dto.LoanAppStatusChangeResponseDto;
import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.entity.UserAccount;
import com.tw.exception.*;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        requestDto = LoanApplicationRequestDto.builder()
                .aadharNo("123456789012")
                .panNo("ABCDE1234F")
                .dob("1990-01-01")
                .mobileNo("9876543210")
                .address("Some Address")
                .monthlyIncome(50000.0)
                .loanAmount(1000000.0)
                .propertyName("Dream Home")
                .location("City A")
                .estimatedCost(1200000.0)
                .documentType("ID Proof")
                .build();

        user = UserAccount.builder()
                .loginId(1L)
                .build();

        profile = CustomerProfile.builder()
                .pId(1L)
                .aadharNo("123456789012")
                .loginAccount(user)
                .build();

        loanApplication = LoanApplication.builder()
                .applicationId(100L)
                .loanAmount(1000000.0)
                .loanStatus("Pending Verification")
                .isActive(true)
                .customerProfile(profile)
                .build();
    }

    @Test
    void shouldSubmitApplication_Success() {
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(1L))
                .thenReturn(Collections.emptyList());
        when(customerProfileRepository.findByAadharNo("123456789012"))
                .thenReturn(Optional.of(profile));
        when(customerProfileRepository.save(any(CustomerProfile.class)))
                .thenAnswer(i -> {
                    CustomerProfile savedProfile = i.getArgument(0);
                    savedProfile.getLoanApplications().get(0).setApplicationId(100L);
                    return savedProfile;
                });

        Long appId = loanApplicationService.submitApplication(1L, requestDto);

        assertNotNull(appId);
        assertEquals(100L, appId);
    }

    @Test
    void shouldSubmitApplication_UserNotFound() {
        when(userAccountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> loanApplicationService.submitApplication(1L, requestDto));
    }

    @Test
    void shouldSubmitApplication_ExceedsEligibility() {
        requestDto.setLoanAmount(99999999.0);
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(LoanInEligibilityException.class,
                () -> loanApplicationService.submitApplication(1L, requestDto));
    }

    @Test
    void shouldGetApplicationById_Success() {
        loanApplication.setCustomerProfile(profile);
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(loanApplication));
        String dobStr = LocalDate.now().minusYears(30).toString();
        profile.setDob(LocalDate.parse(dobStr));
        profile.setLoginAccount(user);

        LoanApplicationResponseDto response =
                loanApplicationService.getApplicationById(1L, 100L);

        assertEquals("Pending Verification", response.getStatus());
    }

    @Test
    void shouldGetApplicationStatusById_Success() {
        loanApplication.setCustomerProfile(profile);
        profile.setLoginAccount(user);

        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(loanApplication));
        String status = loanApplicationService.getApplicationStatusById(1L, 100L);
        assertEquals("Pending Verification", status);
    }

    @Test
    void shouldChangeApplicationStatusById_Success() {
        loanApplication.setCustomerProfile(profile);
        profile.setLoginAccount(user);

        when(loanApplicationRepository.findById(10L)).thenReturn(Optional.of(loanApplication));

        LoanAppStatusChangeRequestDto dto = new LoanAppStatusChangeRequestDto("Approved");

        LoanAppStatusChangeResponseDto response =
                loanApplicationService.changeApplicationStatusById(1L, 10L, dto);

        assertEquals("Approved", loanApplication.getLoanStatus());
    }

    @Test
    void shouldDeleteApplicationById_Success() {
        loanApplication.setCustomerProfile(profile);
        profile.setLoginAccount(user);

        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(loanApplication));

        Boolean result = loanApplicationService.deleteApplicationById(1L, 100L);
        assertTrue(result);
        assertFalse(loanApplication.getIsActive());
    }
    @Test
    public void shouldSubmitApplication_InvalidDate_ThrowsException() {
        Long userId = 1L;
        LoanApplicationRequestDto requestDto = new LoanApplicationRequestDto();
        requestDto.setDob("31-31-2020"); // Invalid date
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
}



