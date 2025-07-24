package com.tw.Repository;

import com.tw.entity.*;
import com.tw.repository.LoanAccountRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests for LoanAccountRepository")
public class LoanAccountRepositoryTest {

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    private LoanApplication createLoanApplication() {
        // 1. Create UserAccount
        UserAccount user = new UserAccount("Test User", "testuser@example.com", "password", "ROLE_USER");
        user = userAccountRepository.save(user);

        // 2. Create CustomerProfile
        CustomerProfile profile = CustomerProfile.builder()
                .dob(LocalDate.of(1990, 1, 1))
                .mobileNo("9999999999")
                .address("Delhi")
                .aadharNo("123456789012")
                .panNo("ABCDE1234F")
                .loginAccount(user)
                .build();
        profile = customerProfileRepository.save(profile);

        // 3. Create LoanApplication
        LoanApplication application = LoanApplication.builder()
                .loanAmount(500000.0)
                .loanStatus("Pending")
                .monthlyIncome(40000.0)
                .emi(15000.0)
                .interestRate(8.5)
                .tenure(10.0)
                .documentType("Aadhar")
                .isActive(true)
                .propertyName("Dream House")
                .location("Mumbai")
                .estimatedCost(600000.0)
                .customerProfile(profile)
                .build();

        return loanApplicationRepository.save(application);
    }
    @Test
    @Order(1)
    @DisplayName("Save LoanAccount")
    void shouldsaveLoanAccount() {
        LoanApplication application = createLoanApplication();

        LoanAccount loanAccount = LoanAccount.builder()
                .amountDispersed(400000.0)
                .loanApplication(application)
                .build();

        LoanAccount saved = loanAccountRepository.save(loanAccount);
        assertNotNull(saved.getAccountId());
        assertEquals(application.getApplicationId(), saved.getLoanApplication().getApplicationId());
    }

    @Test
    @Order(2)
    @DisplayName("Find LoanAccount by ID")
    void shouldfindLoanAccountById() {
        LoanApplication application = createLoanApplication();

        LoanAccount loanAccount = LoanAccount.builder()
                .amountDispersed(350000.0)
                .loanApplication(application)
                .build();
        LoanAccount saved = loanAccountRepository.save(loanAccount);

        Optional<LoanAccount> fetched = loanAccountRepository.findById(saved.getAccountId());
        assertTrue(fetched.isPresent());
        assertEquals(350000.0, fetched.get().getAmountDispersed());
    }

    @Test
    @Order(3)
    @DisplayName("Update LoanAccount amountDispersed")
    void shouldupdateLoanAccount() {
        LoanApplication application = createLoanApplication();

        LoanAccount loanAccount = LoanAccount.builder()
                .amountDispersed(300000.0)
                .loanApplication(application)
                .build();
        LoanAccount saved = loanAccountRepository.save(loanAccount);

        saved.setAmountDispersed(325000.0);
        LoanAccount updated = loanAccountRepository.save(saved);

        assertEquals(325000.0, updated.getAmountDispersed());
    }


}
