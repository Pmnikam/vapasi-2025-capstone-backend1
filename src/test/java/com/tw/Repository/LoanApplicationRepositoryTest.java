package com.tw.Repository;

import com.tw.dto.CustomerLoanInfoDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanApplication;
import com.tw.entity.UserAccount;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Unit tests for LoanApplicationRepository")
public class LoanApplicationRepositoryTest {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccount createUser() {
        UserAccount user = UserAccount.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .role("CUSTOMER")
                .build();
        return userAccountRepository.save(user);
    }

    private CustomerProfile createCustomerProfile(UserAccount user) {
        CustomerProfile profile = CustomerProfile.builder()
                .dob(LocalDate.of(1990, 5, 15))
                .mobileNo("9876543210")
                .address("Mumbai")
                .aadharNo("123456789012")
                .panNo("ABCDE1234F")
                .loginAccount(user)
                .build();
        return customerProfileRepository.save(profile);
    }
    private LoanApplication createLoanApplication(CustomerProfile profile) {
        LoanApplication loan = new LoanApplication();
        loan.setCustomerProfile(profile);
        loan.setDocumentType("Aadhar"); // or whatever valid type
        loan.setEmi(12000.0); // ✅ MUST NOT be null
        loan.setEstimatedCost(150000.0);
        loan.setInterestRate(8.5);
        loan.setIsActive(true);
        loan.setLoanAmount(100000.0);
        loan.setLoanStatus("PENDING");
        loan.setLocation("Chennai");
        loan.setMonthlyIncome(45000.0);
        loan.setPropertyName("Sunshine Residency");
        loan.setTenure(15.0);
        return loan;
    }

    @Test
    @DisplayName("should return all loan applications for a given login ID")
    void shouldReturnLoanApplicationsForGivenLoginId() {

        UserAccount user = UserAccount.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password123")
                .role("CUSTOMER")
                .build();

        CustomerProfile profile = CustomerProfile.builder()
                .aadharNo("123412341234")
                .panNo("ABCDE1234F")
                .mobileNo("9876543210")
                .dob(LocalDate.of(1995, 6, 15))
                .address("Mumbai")
                .loginAccount(user)
                .build();

        user.setCustomerProfile(profile);
        userAccountRepository.save(user);

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setCustomerProfile(profile);
        loanApplication.setLoanAmount(500000.0);
        loanApplication.setLoanStatus("PENDING");
        loanApplication.setMonthlyIncome(40000.0);
        loanApplication.setEstimatedCost(600000.0);
        loanApplication.setLocation("Test City");
        loanApplication.setPropertyName("Test Property");
        loanApplication.setEmi(15000.0);
        loanApplication.setInterestRate(7.5);
        loanApplication.setTenure(10.0);
        loanApplication.setDocumentType("Aadhar");
        loanApplication.setIsActive(true);

        loanApplicationRepository.saveAndFlush(loanApplication);

        List<LoanApplication> result =
                loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(user.getLoginId());

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Should save a new loan application")
    void shouldSaveLoanApplication() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        LoanApplication loan = createLoanApplication(profile);

        LoanApplication savedLoan = loanApplicationRepository.save(loan);
        assertThat(savedLoan.getApplicationId()).isNotNull();
        assertThat(savedLoan.getLoanStatus()).isEqualTo("PENDING");
    }
    @Test
    @DisplayName("Should find loan application by ID")
    void shouldFindLoanApplicationById() {
        UserAccount user = createUser();
        user = userAccountRepository.save(user);

        CustomerProfile profile = createCustomerProfile(user);
        profile = customerProfileRepository.save(profile);

        LoanApplication loan = createLoanApplication(profile);
        loan.setLoanAmount(250000.0);
        LoanApplication savedLoan = loanApplicationRepository.save(loan);

        Optional<LoanApplication> result = loanApplicationRepository.findById(savedLoan.getApplicationId());

        assertThat(result).isPresent();
        assertThat(result.get().getLoanAmount()).isEqualTo(250000.0);
    }

    @Test
    @DisplayName("Should fetch all customer loan applications as DTOs")
    void shouldFetchAllCustomerLoanApplicationsAsDto() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        LoanApplication loan = createLoanApplication(profile);
        loanApplicationRepository.save(loan);

        List<CustomerLoanInfoDto> result = loanApplicationRepository.fetchAllCustomerLoanApplications();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Should find loan by application ID and profile ID")
    void shouldFindByApplicationIdAndCustomerProfileId() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        LoanApplication loan = createLoanApplication(profile);
        loan = loanApplicationRepository.save(loan);

        Optional<LoanApplication> result = loanApplicationRepository
                .findByApplicationIdAndCustomerProfile_pId(loan.getApplicationId(), profile.getPId());

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException when EMI is null")
    void shouldThrowWhenEmiIsNull() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);

        LoanApplication loan = createLoanApplication(profile);
        loan.setEmi(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            loanApplicationRepository.saveAndFlush(loan);
        });
    }

    @Test
    @DisplayName("Should find all loan applications")
    void shouldFindAllLoanApplications() {
        UserAccount user = userAccountRepository.save(createUser());
        CustomerProfile profile = customerProfileRepository.save(createCustomerProfile(user));

        loanApplicationRepository.save(createLoanApplication(profile));
        loanApplicationRepository.save(createLoanApplication(profile));

        List<LoanApplication> result = loanApplicationRepository.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2); // Now this will pass
    }

    @Test
    @DisplayName("Should find by customer login ID")
    void shouldFindByCustomerProfile_LoginAccount_LoginId() {
        UserAccount user = userAccountRepository.save(createUser());
        CustomerProfile profile = createCustomerProfile(user);
        profile = customerProfileRepository.save(profile);
        LoanApplication loan = createLoanApplication(profile);
        loanApplicationRepository.save(loan);
        List<LoanApplication> result =
                loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(user.getLoginId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCustomerProfile().getLoginAccount().getLoginId())
                .isEqualTo(user.getLoginId());
    }


}
