package org.sdfw.biometric.form.field;

import org.sdfw.biometric.model.Center;
import org.sdfw.biometric.model.LoanDisbursement;
import org.sdfw.biometric.model.Member;

import java.util.ArrayList;
import java.util.List;

public class LoanDisbursementListFields {

    private String userId;
    private String branchId;
    private String openingDate;
    private List<LoanDisbursement> loanDisbursements = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public List<LoanDisbursement> getLoanDisbursements() {
        return loanDisbursements;
    }

    public void setLoanDisbursements(List<LoanDisbursement> loanDisbursements) {
        this.loanDisbursements = loanDisbursements;
    }

    public void clearLoanDisbursements() {
        this.loanDisbursements.clear();
    }
}
