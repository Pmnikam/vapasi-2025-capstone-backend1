package com.tw.Repository;

import com.tw.entity.CustomerProfile;
import com.tw.entity.LoanAppDocument;
import com.tw.entity.LoanApplication;
import com.tw.entity.UserAccount;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.LoanAppDocumentRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.UserAccountRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoanAppDocumentRepositoryTest {

    @Autowired
    private LoanAppDocumentRepository documentRepository;

    @Autowired
    private LoanApplicationRepository applicationRepository;

    @Autowired
    private CustomerProfileRepository profileRepository;

    @Autowired
    private UserAccountRepository userRepository;

    private LoanApplication createLoanApplication() {
        UserAccount user = new UserAccount("Rohit", "rohit@example.com", "pass123", "Customer");
        user = userRepository.save(user);

        CustomerProfile profile = CustomerProfile.builder().dob(LocalDate.of(1995, 2, 10)).mobileNo("9876543210").address("Lucknow").aadharNo("111122223333").panNo("XYZAB1234P").loginAccount(user).build();
        profile = profileRepository.save(profile);

        LoanApplication application = LoanApplication.builder().loanAmount(300000.0).loanStatus("Submitted").monthlyIncome(45000.0).emi(12000.0).interestRate(8.0).tenure(15.0).documentType("Aadhar").isActive(true).propertyName("Green Villa").location("Kanpur").estimatedCost(350000.0).customerProfile(profile).build();
        return applicationRepository.save(application);
    }

    @Test
    void shouldSaveLoanDocument() {
        LoanApplication application = createLoanApplication();

        byte[] sampleFile = "dummy file content".getBytes();

        LoanAppDocument document = LoanAppDocument.builder().documentType("Aadhar").fileData(sampleFile).loanApplication(application).build();

        LoanAppDocument saved = documentRepository.save(document);

        assertNotNull(saved.getDocumentId());
        assertEquals("Aadhar", saved.getDocumentType());
        assertArrayEquals(sampleFile, saved.getFileData());
    }

    @Test
    void shouldFindLoanDocumentById() {
        LoanApplication application = createLoanApplication();
        byte[] fileData = "proof".getBytes();

        LoanAppDocument document = LoanAppDocument.builder().documentType("PAN").fileData(fileData).loanApplication(application).build();
        LoanAppDocument saved = documentRepository.save(document);

        Optional<LoanAppDocument> fetched = documentRepository.findById(saved.getDocumentId());

        assertTrue(fetched.isPresent());
        assertEquals("PAN", fetched.get().getDocumentType());
        assertArrayEquals(fileData, fetched.get().getFileData());
    }


}
