package com.tw.service.impl;

import com.tw.dto.CustomerLoanRequestDto;
import com.tw.dto.CustomerLoanResponseDto;
import com.tw.entity.LoanAppDocument;
import com.tw.entity.LoanApplication;
import com.tw.entity.CustomerProfile;
import com.tw.entity.UserAccount;
import com.tw.exception.*;
import com.tw.repository.LoanAppDocumentRepository;
import com.tw.repository.LoanApplicationRepository;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserAccountRepository;
import com.tw.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    LoanApplicationRepository loanApplicationRepository;
    @Autowired
    LoanAppDocumentRepository loanAppDocumentRepository;
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
    public String submitApplication(Long userId, CustomerLoanRequestDto requestDto) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        double eligibleLoanAmount = 60 * (0.6 * requestDto.getMonthlyIncome());
        if(requestDto.getLoanAmount() > eligibleLoanAmount){
            throw new LoanInEligibilityException("Requested amount exceeds. Max "+eligibleLoanAmount);
        }

        List<LoanApplication> existingApps = loanApplicationRepository
                .findByCustomerProfile_LoginAccount_LoginId(userId);
        for(LoanApplication loanApp : existingApps){
            String status = loanApp.getLoanStatus();
            if(status.equals("") ||
                    status.equals("")) //todo
            {
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
                .customerProfile(profile)
                .build();

        MultipartFile customerFile = requestDto.getFileData();
        LoanAppDocument loanAppDocument;
        try{
            loanAppDocument = LoanAppDocument.builder()
                    .documentType(requestDto.getDocumentType())
                    .fileData(customerFile.getBytes())
                    .loanApplication(loanApp).build();
        }
        catch(Exception ex){
            throw new FailedToReadDocument(ex.getMessage());
        }

        loanApp.setBinaryDocument(loanAppDocument);
        if(profile.getLoanApplications() == null)
        {
            profile.setLoanApplications(new ArrayList<>());
        }
        profile.getLoanApplications().add(loanApp);

        customerProfileRepository.save(profile);

        return loanApp.getApplicationId();
    }

    @Override
    public CustomerLoanResponseDto getApplicationById(Long userId, Long applicationId){
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);

        CustomerProfile profile = loanApp.getCustomerProfile();
        LoanAppDocument document = loanApp.getBinaryDocument();

        return CustomerLoanResponseDto.builder()
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
                .documentSubmitted(document != null ? document.getDocumentType() : "N/A")
                .status(loanApp.getLoanStatus())
                .build();
    }

    @Override
    public String getApplicationStatusById(Long userId, Long applicationId){
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        return loanApp.getLoanStatus();
    }

    @Override
    public Boolean changeApplicationStatusById(Long userId, Long applicationId, String status){
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        loanApp.setLoanStatus("approved"); //todo remove hardcoding
        loanApplicationRepository.save(loanApp);
        return true;

    }

    public Boolean deleteApplicationById(Long userId, Long applicationId){
        LoanApplication loanApp = getVerifiedLoanApplication(userId, applicationId);
        loanApp.setIsActive(false);
        loanApplicationRepository.save(loanApp);
        return true;
    }


    private LoanApplication getVerifiedLoanApplication(Long userId, Long applicationId){
        LoanApplication loanApp = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(applicationId));
        if(!loanApp.getIsActive()){
            throw new LoanApplicationNotFoundException(applicationId); //soft delete done already
        }
        if (!loanApp.getCustomerProfile().getLoginAccount().getLoginId().equals(userId)) {
            throw new UnauthorizedException("Application does not belong to this user");
        }

        return loanApp;
    }

}
