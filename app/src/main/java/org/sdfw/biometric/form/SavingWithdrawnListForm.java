package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.LoanDisbursementListFields;
import org.sdfw.biometric.form.field.SavingWithdrawnListFields;
import org.sdfw.biometric.model.LoanDisbursement;
import org.sdfw.biometric.model.SavingWithdrawn;
import org.sdfw.biometric.model.User;

import java.util.List;


public class SavingWithdrawnListForm extends BaseForm {

    private SavingWithdrawnListFields fields = new SavingWithdrawnListFields();

    public SavingWithdrawnListFields getFields() {
        return fields;
    }

    public void setBasicFields(User user) {
        fields.setUserId(user.getUserId());
        fields.setBranchId(user.getBranchId());
    }

    public void setSavingWithdrawns(List<SavingWithdrawn> savingWithdrawns) {
        fields.setSavingWithdrawns(savingWithdrawns);
        notifyPropertyChanged(BR.savingWithdrawns);
    }

    public void clearSavingWithdrawns() {
        fields.clearSavingWithdrawn();
        notifyPropertyChanged(BR.savingWithdrawns);
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
    public List<SavingWithdrawn> getSavingWithdrawns() {
        return fields.getSavingWithdrawns();
    }
}
