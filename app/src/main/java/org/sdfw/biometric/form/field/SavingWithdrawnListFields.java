package org.sdfw.biometric.form.field;

import org.sdfw.biometric.model.LoanDisbursement;
import org.sdfw.biometric.model.SavingWithdrawn;

import java.util.ArrayList;
import java.util.List;

public class SavingWithdrawnListFields {

    private String userId;
    private String branchId;
    private String openingDate;
    private List<SavingWithdrawn> savingWithdrawns = new ArrayList<>();

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

    public List<SavingWithdrawn> getSavingWithdrawns() {
        return savingWithdrawns;
    }

    public void setSavingWithdrawns(List<SavingWithdrawn> savingWithdrawns) {
        this.savingWithdrawns = savingWithdrawns;
    }

    public void clearSavingWithdrawn() {
        this.savingWithdrawns.clear();
    }
}
