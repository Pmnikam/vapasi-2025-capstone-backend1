package com.tw.service.impl;

import com.tw.dto.LoanAppStatusChangeRequestDto;
import com.tw.dto.LoanAppStatusChangeResponseDto;
import com.tw.dto.LoanApplicationRequestDto;
import com.tw.dto.LoanApplicationResponseDto;
import com.tw.entity.LoanAccount;
import com.tw.entity.LoanApplication;
import com.tw.entity.CustomerProfile;
import com.tw.entity.UserAccount;
import com.tw.exception.*;
import com.tw.repository.*;
import com.tw.service.LoanApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tw.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.tw.util.AppConstant.*;


@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoanApplicationServiceImpl.class);

    @Autowired
    LoanApplicationRepository loanApplicationRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Override
    public Long submitApplication(Long userId, LoanApplicationRequestDto requestDto) {
        LOGGER.info("Submitting loan application for userId: {}", userId);
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.warn("User with ID {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        CustomerProfile existingProfile = customerProfileRepository
                .findByAadharNo(requestDto.getAadharNo()).orElse(null);
        if (existingProfile != null && !existingProfile.getLoginAccount().getLoginId().equals(userId)) {
            throw new UnauthorizedException("Aadhar number already linked to another user");
        }

        List<LoanApplication> existingApps = loanApplicationRepository
                .findByCustomerProfile_LoginAccount_LoginId(userId);
        boolean hasPendingStatus = existingApps.stream()
                .anyMatch(app -> PENDING_CUSTOMER.equals(app.getLoanStatus())
                        || PENDING_ADMIN.equals(app.getLoanStatus()));
        if (hasPendingStatus) {
            throw new LoanInEligibilityException("Loan application is already in progress");
        }

        double eligibleLoanAmount = 60 * (0.6 * requestDto.getMonthlyIncome());
        if(requestDto.getLoanAmount() > eligibleLoanAmount){
            LOGGER.warn("Loan amount {} exceeds eligibility limit {}", requestDto.getLoanAmount(), eligibleLoanAmount);
            throw new LoanInEligibilityException("Requested amount exceeds. Max "+eligibleLoanAmount);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dob;
        try{
            dob = LocalDate.parse(requestDto.getDob(), formatter);
        }
        catch (DateTimeParseException ex){
            LOGGER.error("Failed to parse DOB: {}. Expected format yyyy-MM-dd", requestDto.getDob());
            throw new IllegalArgumentException("Invalid date format for DOB");
        }

        CustomerProfile customerProfile;
        if (existingProfile != null) {
            LOGGER.info("Updating existing profile for Aadhar: {}", requestDto.getAadharNo());
            customerProfile = existingProfile;
            customerProfile.setDob(dob);
            customerProfile.setMobileNo(requestDto.getMobileNo());
            customerProfile.setAddress(requestDto.getAddress());
            customerProfile.setPanNo(requestDto.getPanNo());
            // Aadhar number should not change; skip or validate immutability
        } else {
            customerProfile = new CustomerProfile();
            customerProfile = CustomerProfile.builder()
                    .dob(dob)
                    .mobileNo(requestDto.getMobileNo())
                    .address(requestDto.getAddress())
                    .aadharNo(requestDto.getAadharNo())
                    .panNo(requestDto.getPanNo())
                    .loginAccount(userAccount)
                    .loanApplications(new ArrayList<>())
                    .build();
        }

        LoanApplication loanApp = LoanApplication.builder()
                .loanAmount(requestDto.getLoanAmount())
                .monthlyIncome(requestDto.getMonthlyIncome())
                .propertyName(requestDto.getPropertyName())
                .location(requestDto.getLocation())
                .estimatedCost(requestDto.getEstimatedCost())
                .loanStatus(PENDING_ADMIN)
                .isActive(true)
                .documentType(requestDto.getDocumentType())
                .tenure(requestDto.getTenure())
                .emi(requestDto.getEmi())
                .interestRate(AppConstant.INTEREST_RATE)
                .customerProfile(customerProfile)
                .build();

        if(customerProfile.getLoanApplications() == null)
        {
            customerProfile.setLoanApplications(new ArrayList<>());
        }
        customerProfile.getLoanApplications().add(loanApp);
        customerProfileRepository.save(customerProfile);
        loanApplicationRepository.save(loanApp);

        LOGGER.info("Loan application submitted successfully. Application ID: {}", loanApp.getApplicationId());

        return loanApp.getApplicationId();
    }

    @Override
    public LoanApplicationResponseDto getApplicationById(Long userId, Long applicationId){
        LOGGER.info("Fetching loan application details for applicationId: {}, userId: {}", applicationId, userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);

        CustomerProfile profile = loanApp.getCustomerProfile();

        return LoanApplicationResponseDto.builder()
                .applicationNo(Long.valueOf(loanApp.getApplicationId()))
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
                .tenure(loanApp.getTenure())
                .interestRate(AppConstant.INTEREST_RATE)
                .emi(loanApp.getEmi())
                .build();
    }

    @Override
    public List<LoanApplicationResponseDto> getAllApplicationsByUserId(Long userId) {
        LOGGER.info("Fetching all existing applications for userId: {}", userId);
        //todo check whether user id is valid
        List<LoanApplication> loanApplicationList
                = loanApplicationRepository.findByCustomerProfile_LoginAccount_LoginId(userId);
        List<LoanApplicationResponseDto> responseDtoList = new ArrayList<>();

        for (LoanApplication loanApp : loanApplicationList) {
            CustomerProfile profile = loanApp.getCustomerProfile();

            LoanApplicationResponseDto responseDto = LoanApplicationResponseDto.builder()
                    .applicationNo(Long.valueOf(loanApp.getApplicationId()))
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
                    .tenure(loanApp.getTenure())
                    .interestRate(AppConstant.INTEREST_RATE)
                    .emi(loanApp.getEmi())
                    .build();

            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }


    @Override
    public LoanAppStatusChangeResponseDto changeApplicationStatusById(Long userId, Long applicationId, LoanAppStatusChangeRequestDto loanAppStatusChangeRequestDto){
        String status = loanAppStatusChangeRequestDto.getStatus();
        LOGGER.info("Changing application status for applicationId: {} by userId: {}", applicationId,
                userId);
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        String newStatus = loanAppStatusChangeRequestDto.getStatus();
        String oldStatus = loanApp.getLoanStatus();
        //todo check whether it is valid state
        if(oldStatus.equals(APPROVED)){
            throw new InvalidLoanStatusException("Loan is in approved state. Cannot change it.");
        }
        if(newStatus.equals(PENDING_CUSTOMER)){
            throw new InvalidLoanStatusException("Not authorised to change to this state.Only possible state are Approved and Rejected");
        }
        if(oldStatus.equals(PENDING_ADMIN) && newStatus.equals(APPROVED)){
            throw new InvalidLoanStatusException("Not authorized for this change");
        }
        loanApp.setLoanStatus(loanAppStatusChangeRequestDto.getStatus());

        LoanAccount loanAccount = new LoanAccount();
        loanAccount.setAmountDispersed(loanApp.getLoanAmount());
        loanAccount.setLoanApplication(loanApp);
        loanApp.setLoanAccount(loanAccount);

        loanApplicationRepository.save(loanApp);

        LOGGER.info("Loan application {} status changed to {}", applicationId, status);
        LOGGER.info("Loan account created successfully. AccountId ID: {}", loanApp.getLoanAccount().getAccountId());

        LoanAppStatusChangeResponseDto  loanAppStatusChangeResponseDto = new LoanAppStatusChangeResponseDto();
        loanAppStatusChangeResponseDto.setLoanAmount(loanApp.getLoanAccount().getAmountDispersed());
        loanAppStatusChangeResponseDto.setAccountId(loanApp.getLoanAccount().getAccountId());

        return loanAppStatusChangeResponseDto;
    }

    @Override
    public Boolean checkApplicationStatus(Long userId) {
        List<LoanApplication> existingApps = loanApplicationRepository
                .findByCustomerProfile_LoginAccount_LoginId(userId);
        boolean hasPendingStatus = existingApps.stream()
                .anyMatch(app -> PENDING_CUSTOMER.equals(app.getLoanStatus())
                        || PENDING_ADMIN.equals(app.getLoanStatus()));
        if (hasPendingStatus) {
            return true;
        }
        return false;
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
                    LOGGER.warn("Loan application not found: {}", applicationId.toString());
                    return new LoanApplicationNotFoundException("Loan application not found exception: " + applicationId);
                });
        if(!loanApp.getIsActive()){
            LOGGER.warn("Loan application {} is already inactive (soft deleted)", applicationId);
            throw new UnauthorizedException("Loan application is already deleted. Application Id:  "+applicationId); //soft delete done already
        }
        if (!loanApp.getCustomerProfile().getLoginAccount().getLoginId().equals(userId)) {
            LOGGER.warn("Unauthorized access attempt by userId: {} for applicationId: {}", userId, applicationId);
            throw new UnauthorizedException("Application does not belong to this user");
        }

        return loanApp;
    }

}