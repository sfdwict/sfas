package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.LoanDisbursementListFields;
import org.sdfw.biometric.model.LoanDisbursement;
import org.sdfw.biometric.model.User;

import java.util.List;


public class LoanDisbursementListForm extends BaseForm {

    private LoanDisbursementListFields fields = new LoanDisbursementListFields();

    public LoanDisbursementListFields getFields() {
        return fields;
    }

    public void setBasicFields(User user) {
        fields.setUserId(user.getUserId());
        fields.setBranchId(user.getBranchId());
    }

    public void setLoanDisbursements(List<LoanDisbursement> loanDisbursements) {
        fields.setLoanDisbursements(loanDisbursements);
        notifyPropertyChanged(BR.loanDisbursements);
    }

    public void clearLoanDisbursements() {
        fields.clearLoanDisbursements();
        notifyPropertyChanged(BR.loanDisbursements);
    }

    public void setOpeningDate(String openingDate) {
        fields.setOpeningDate(openingDate);
        notifyPropertyChanged(BR.openingDate);
    }

    @Bindable
    public String getOpeningDate() {
        return fields.getOpeningDate();
    }

    @Bindable
    public List<LoanDisbursement> getLoanDisbursements() {
        return fields.getLoanDisbursements();
    }
}
