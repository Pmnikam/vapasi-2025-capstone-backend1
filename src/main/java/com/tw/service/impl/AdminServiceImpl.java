package com.tw.service.impl;

import com.tw.dto.CustomerLoanInfoDto;
import com.tw.entity.LoanApplication;
import com.tw.exception.InvalidLoanStatusException;
import com.tw.exception.LoanApplicationNotFoundException;
import com.tw.exception.LoanInactiveException;
import com.tw.exception.LoanNotFoundException;
import com.tw.repository.LoanApplicationRepository;
import com.tw.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tw.util.AppConstant.*;

@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Override
    public List<CustomerLoanInfoDto> getAllCustomerLoanInfo() {
        LOGGER.info("Fetching all customer loan applications");
        List<CustomerLoanInfoDto> loanInfoList = loanApplicationRepository.fetchAllCustomerLoanApplications();
        if (loanInfoList.isEmpty()) {
            LOGGER.warn("No customer loan applications found");
            throw new LoanNotFoundException("No customer loan applications found");
        }
        LOGGER.info("Found {} customer loan applications", loanInfoList.size());
        return loanInfoList;
    }

    @Override
    public String processLoanDecision(Long customerId, Long loanId, String action) {
        LOGGER.info("Processing loan decision: action={}, customerId={}, loanId={}", action, customerId, loanId);
        LoanApplication loanApp = fetchLoanApplication(customerId, loanId);
        validateLoanStatusForProcessing(loanApp);

        if (!loanApp.getIsActive()) {
            LOGGER.warn("Loan application {} is inactive", loanId);
            throw new LoanInactiveException("Loan application is not active and cannot be processed.");
        }

        updateLoanStatusBasedOnAction(loanApp, action);
        loanApplicationRepository.save(loanApp);
        String message = buildSuccessMessage(loanId, customerId, action);
        LOGGER.info(message);
        return message;
    }

    private LoanApplication fetchLoanApplication(Long customerId, Long loanId) {
        LOGGER.debug("Fetching loan application for customerId={} and loanId={}", customerId, loanId);
        return loanApplicationRepository
                .findByApplicationIdAndCustomerProfile_pId(loanId, customerId)
                .orElseThrow(() -> new LoanApplicationNotFoundException(
                        "Loan not found for customer ID " + customerId + " and loan ID " + loanId));
    }

    private void validateLoanStatusForProcessing(LoanApplication loanApp) {
        String currentStatus = loanApp.getLoanStatus().trim();
        LOGGER.debug("Validating loan status for processing: {}", currentStatus);
        if (!PENDING_ADMIN.equalsIgnoreCase(currentStatus)) {
            LOGGER.warn("Invalid loan status for processing: {}", currentStatus);
            throw new InvalidLoanStatusException(
                    "Cannot process loan. Current status is '" + currentStatus + "'");
        }
    }

    private void updateLoanStatusBasedOnAction(LoanApplication loanApp, String action) {
        LOGGER.debug("Updating loan status based on action: {}", action);
        if (APPROVE.equalsIgnoreCase(action)) {
            loanApp.setLoanStatus(PENDING_CUSTOMER);
        } else if (REJECT.equalsIgnoreCase(action)) {
            loanApp.setLoanStatus(REJECTED);
        } else {
            LOGGER.error("Invalid action provided: {}", action);
            throw new IllegalArgumentException("Invalid action: " + action + ". Use 'approve' or 'reject'.");
        }
    }

    private String buildSuccessMessage(Long loanId, Long customerId, String action) {
        String status = action.equalsIgnoreCase(APPROVE) ? APPROVED : REJECTED;
        return "Loan with ID " + loanId + " has been " + status + " successfully for customer ID " + customerId;
    }
}
