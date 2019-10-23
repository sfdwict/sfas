package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.MatrixFields;
import org.sdfw.biometric.model.MemberLite;
import org.sdfw.biometric.model.User;

import java.util.List;


public class MatrixForm extends BaseForm {

    private MatrixFields fields = new MatrixFields();

    public MatrixFields getFields() {
        return fields;
    }

    public void setBasicFields(User user) {
        fields.setUserId(user.getUserId());
        fields.setBranchId(user.getBranchId());
        fields.setBranchName(user.getBranchName());
        notifyPropertyChanged(BR.branchName);
    }

    public void setMembers(List<MemberLite> members) {
        fields.setMembers(members);
        notifyPropertyChanged(BR.members);
    }

    public void clearMembers() {
        fields.clearMembers();
        notifyPropertyChanged(BR.members);
    }

    @Bindable
    public String getBranchId() {
        return fields.getBranchId();
    }

    @Bindable
    public String getBranchName() {
        return fields.getBranchName();
    }

    @Bindable
    public List<MemberLite> getMembers() {
        return fields.getMembers();
    }
}
