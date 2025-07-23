package com.tw.service.impl;

import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.entity.LoanApplication;
import com.tw.entity.CustomerProfile;
import com.tw.entity.UserAccount;
import com.tw.exception.*;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserAccountRepository;
import com.tw.service.LoanApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanApplicationServiceImpl.class);

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    /*
    1. Check whether user exists.
    2. Check whether he is eligible for loan
    3. Check whether he has already applied for loan and it is not in either "Pending" or "awaiting" state
    4. Check whether customer profile already exists. If so overwrite it.
        To do this search by aadhar.
    5. Overwrite or create customerProfile.
    6. Create Loan Application
    7. Return application Number
    * */

    @Override
    public Long submitApplication(Long userId, LoanApplicationRequestDto requestDto) {
        LOGGER.info("Submitting loan application for userId: {}", userId);
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User with ID {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        double eligibleLoanAmount = 60 * (0.6 * requestDto.getMonthlyIncome());
        if(requestDto.getLoanAmount() > eligibleLoanAmount){
            LOGGER.warn("Loan amount {} exceeds eligibility limit {}", requestDto.getLoanAmount(), eligibleLoanAmount);
            throw new LoanInEligibilityException("Requested amount exceeds. Max "+eligibleLoanAmount);
        }

        List<LoanApplication> existingApps = loanApplicationRepository
                .findByCustomerProfile_LoginAccount_LoginId(userId);
        for(LoanApplication loanApp : existingApps){
            String status = loanApp.getLoanStatus();
            if(status.equals("") ||
                    status.equals("")) //todo
            {
                LOGGER.warn("Ongoing loan application found for userId {}: applicationId {}", userId, loanApp.getApplicationId());
                throw new LoanInEligibilityException("Ongoing loan application exists. Application Id: "+loanApp.getApplicationId());

            }
        }

        CustomerProfile existingProfile = customerProfileRepository
                .findByAadharNo(requestDto.getAadharNo()).orElse(null);

        DateTimeFormatter fromatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob;
        try{
            dob = LocalDate.parse(requestDto.getDob(), fromatter);
        }
        catch (DateTimeParseException ex){
            LOGGER.error("Failed to parse DOB: {}. Expected format dd-MM-yyyy", requestDto.getDob());
            throw new IllegalArgumentException("Invalid date format for DOB");
        }

        CustomerProfile profile = CustomerProfile.builder()
                .dob(dob)
                .mobileNo(requestDto.getMobileNo())
                .address(requestDto.getAddress())
                .aadharNo(requestDto.getAadharNo())
                .panNo(requestDto.getPanNo())
                .loginAccount(userAccount)
                .build();

        if(existingProfile != null){
            LOGGER.info("Overwriting existing customer profile for Aadhar: {}", requestDto.getAadharNo());
            profile.setPId(existingProfile.getPId());
        }

        LoanApplication loanApp = LoanApplication.builder()
                .loanAmount(requestDto.getLoanAmount())
                .monthlyIncome(requestDto.getMonthlyIncome())
                .propertyName(requestDto.getPropertyName())
                .location(requestDto.getLocation())
                .estimatedCost(requestDto.getEstimatedCost())
                .loanStatus("Pending Verification")
                .isActive(true)
                .documentType(requestDto.getDocumentType())
                .customerProfile(profile)
                .build();

//        MultipartFile customerFile = requestDto.getFileData();
//        LoanAppDocument loanAppDocument;
//        try{
//            loanAppDocument = LoanAppDocument.builder()
//                    .documentType(requestDto.getDocumentType())
//                    .fileData(customerFile.getBytes())
//                    .loanApplication(loanApp).build();
//        }
//        catch(Exception ex){
//            throw new FailedToReadDocument(ex.getMessage());
//        }

//        loanApp.setBinaryDocument(loanAppDocument);
        if(profile.getLoanApplications() == null)
        {
            profile.setLoanApplications(new ArrayList<>());
        }
        profile.getLoanApplications().add(loanApp);

        customerProfileRepository.save(profile);
        LOGGER.info("Loan application submitted successfully. Application ID: {}", loanApp.getApplicationId());

        return loanApp.getApplicationId();
    }

    @Override
    public LoanApplicationResponseDto getApplicationById(Long userId, Long applicationId){
        LOGGER.info("Fetching loan application details for applicationId: {}, userId: {}", applicationId, userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);

        CustomerProfile profile = loanApp.getCustomerProfile();
//        LoanAppDocument document = loanApp.getBinaryDocument();

        return LoanApplicationResponseDto.builder()
                .applicationNo(loanApp.getApplicationId())
                .dob(profile.getDob().toString())
                .mobileNo(profile.getMobileNo())
                .address(profile.getAddress())
                .aadharNo(profile.getAadharNo())
                .panNo(profile.getPanNo())
                .loanAmount(loanApp.getLoanAmount())
                .monthlyIncome(loanApp.getMonthlyIncome())
                .propertyName(loanApp.getPropertyName())
                .location(loanApp.getLocation())
                .estimatedCost(loanApp.getEstimatedCost())
                .documentSubmitted(loanApp.getDocumentType())
                .status(loanApp.getLoanStatus())
                .build();
    }

    @Override
    public String getApplicationStatusById(Long userId, Long applicationId){
        LOGGER.info("Fetching status for applicationId: {}, userId: {}", applicationId, userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        return loanApp.getLoanStatus();
    }

    @Override
    public Boolean changeApplicationStatusById(Long userId, Long applicationId, String status){
        LOGGER.info("Changing application status for applicationId: {} by userId: {}", applicationId, userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        loanApp.setLoanStatus("approved"); //todo remove hardcoding
        loanApplicationRepository.save(loanApp);
        LOGGER.info("Loan application {} status changed to {}", applicationId, status);
        return true;

    }

    public Boolean deleteApplicationById(Long userId, Long applicationId){
        LOGGER.info("Deleting (soft) applicationId: {} by userId: {}", applicationId, userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        loanApp.setIsActive(false);
        loanApplicationRepository.save(loanApp);
        LOGGER.info("ApplicationId: {} marked as inactive", applicationId);
        return true;
    }


    private LoanApplication getVerifiedLoanApplication(Long userId, Long applicationId){
        LOGGER.info("Verifying ownership and existence of applicationId: {} for userId: {}", applicationId, userId);
        LoanApplication loanApp = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    LOGGER.warn("Loan application not found: {}", applicationId);
                    return new LoanApplicationNotFoundException(applicationId);
                });
        if(!loanApp.getIsActive()){
            LOGGER.warn("Loan application {} is already inactive (soft deleted)", applicationId);
            throw new LoanApplicationNotFoundException(applicationId); //soft delete done already
        }
        if (!loanApp.getCustomerProfile().getLoginAccount().getLoginId().equals(userId)) {
            LOGGER.warn("Unauthorized access attempt by userId: {} for applicationId: {}", userId, applicationId);
            throw new UnauthorizedException("Application does not belong to this user");
        }

        return loanApp;
    }

}
