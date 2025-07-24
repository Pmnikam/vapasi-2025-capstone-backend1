package com.tw.service.impl;
import com.tw.dto.CustomerLoanInfoDto;
import com.tw.entity.LoanApplication;
import com.tw.exception.*;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.tw.util.AppConstant.*;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Override
    public List<CustomerLoanInfoDto> getAllCustomerLoanInfo() {
        List<CustomerLoanInfoDto> loanInfoList = loanApplicationRepository.fetchAllCustomerLoanApplications();
        if (loanInfoList.isEmpty()) {
            throw new LoanNotFoundException("No customer loan applications found");
        }
        return loanInfoList;
    }
    @Override
    public String processLoanDecision(Long customerId, Long loanId, String action) {
        LoanApplication loanApp = fetchLoanApplication(customerId, loanId);
        validateLoanStatusForProcessing(loanApp);

        if (!loanApp.getIsActive()) {
            throw new LoanInactiveException("Loan application is not active and cannot be processed.");
        }

        updateLoanStatusBasedOnAction(loanApp, action);
        loanApplicationRepository.save(loanApp);

        return buildSuccessMessage(loanId, customerId, action);
    }

    private LoanApplication fetchLoanApplication(Long customerId, Long loanId) {
        return loanApplicationRepository
                .findByApplicationIdAndCustomerProfile_pId(loanId, customerId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(
                        "Loan not found for customer ID " + customerId + " and loan ID " + loanId));
    }

    private void validateLoanStatusForProcessing(LoanApplication loanApp) {
        String currentStatus = loanApp.getLoanStatus().trim();
        if (!PENDING_ADMIN.equalsIgnoreCase(currentStatus)) {
            throw new InvalidLoanStatusException(
                    "Cannot process loan. Current status is '" + currentStatus + "'");
        }
    }

    private void updateLoanStatusBasedOnAction(LoanApplication loanApp, String action) {
        if (APPROVE.equalsIgnoreCase(action)) {
            loanApp.setLoanStatus(PENDING_CUSTOMER);
        } else if (REJECT.equalsIgnoreCase(action)) {
            loanApp.setLoanStatus(REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action + ". Use 'approve' or 'reject'.");
        }
    }

    private String buildSuccessMessage(Long loanId, Long customerId, String action) {
        String status = action.equalsIgnoreCase(APPROVE) ? APPROVED : REJECTED;
        return "Loan with ID " + loanId + " has been " + status + " successfully for customer ID " + customerId;
    }


}
