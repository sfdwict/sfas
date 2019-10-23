package org.sdfw.biometric.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.sdfw.biometric.model.LoanDisbursement;

import java.util.List;

public class GetLoanDisbursementResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private LoanDisbursement loanDisbursement;

    public LoanDisbursement getLoanDisbursement() {
        return loanDisbursement;
    }

    public void setLoanDisbursement(LoanDisbursement loanDisbursement) {
        this.loanDisbursement = loanDisbursement;
    }
}
