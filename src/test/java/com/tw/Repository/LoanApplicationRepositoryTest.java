package com.tw.Repository;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
        LoanApplication loan = LoanApplication.builder()
                .loanAmount(250000.0)
                .loanStatus("PENDING")
                .monthlyIncome(40000.0)
                .isActive(true)
                .propertyName("Green Villa")
                .location("Pune")
                .estimatedCost(300000.0)
                .customerProfile(profile)
                .build();
        return loanApplicationRepository.save(loan);
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
        loanApplication.setIsActive(true);

        loanApplicationRepository.saveAndFlush(loanApplication);

        List<LoanApplication> result = loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(user.getLoginId());

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Should save a new loan application")
    void shouldSaveLoanApplication() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        LoanApplication loan = createLoanApplication(profile);

        assertThat(loan.getApplicationId()).isNotNull();
        assertThat(loan.getLoanStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Should find loan application by ID")
    void shouldFindLoanApplicationById() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        LoanApplication loan = createLoanApplication(profile);

        Optional<LoanApplication> result = loanApplicationRepository.findById(loan.getApplicationId());

        assertThat(result).isPresent();
        assertThat(result.get().getLoanAmount()).isEqualTo(250000.0);
    }

    @Test
    @DisplayName("Should find all loan applications")
    void shouldFindAllLoanApplications() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        createLoanApplication(profile);
        createLoanApplication(profile); // Save second

        List<LoanApplication> result = loanApplicationRepository.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }
    @Test
    @DisplayName("Should find by customer login ID")
    void shouldFindByCustomerProfile_LoginAccount_LoginId() {
        UserAccount user = createUser();
        CustomerProfile profile = createCustomerProfile(user);
        createLoanApplication(profile);

        List<LoanApplication> result = loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(user.getLoginId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCustomerProfile().getLoginAccount().getLoginId()).isEqualTo(user.getLoginId());
    }
}
