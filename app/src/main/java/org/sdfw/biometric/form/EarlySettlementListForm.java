package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.EarlySettlementListFields;
import org.sdfw.biometric.model.EarlySettlement;
import org.sdfw.biometric.model.User;

import java.util.List;


public class EarlySettlementListForm extends BaseForm {

    private EarlySettlementListFields fields = new EarlySettlementListFields();

    public EarlySettlementListFields getFields() {
        return fields;
    }

    public void setBasicFields(User user) {
        fields.setUserId(user.getUserId());
        fields.setBranchId(user.getBranchId());
    }

    public void setEarlySettlements(List<EarlySettlement> earlySettlements) {
        fields.setEarlySettlements(earlySettlements);
        notifyPropertyChanged(BR.earlySettlements);
    }

    public void clearEarlySettlements() {
        fields.clearEarlySettlement();
        notifyPropertyChanged(BR.earlySettlements);
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
    public List<EarlySettlement> getEarlySettlements() {
        return fields.getEarlySettlements();
    }
}
