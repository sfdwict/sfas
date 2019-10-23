package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.UserStatusFields;
import org.sdfw.biometric.form.field.UserFields;


public class SigninForm extends BaseForm {

    private UserFields fields = new UserFields();
    private UserStatusFields errors = new UserStatusFields();

    public boolean isValid(boolean setMessage) {
        boolean einValid = isEinValid(setMessage);
        boolean passwordValid = isPasswordValid(setMessage);
        return einValid && passwordValid;
    }

    public boolean isEinValid(boolean setMessage) {
        if (!isEmptyField(fields.getUserId())) {
            errors.setUserId(null);
            notifyPropertyChanged(BR.userIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setUserId("ই আই এন খালি হতে পারবেনা");
                notifyPropertyChanged(BR.userIdError);
            }
            return false;
        }
    }

    public boolean isPasswordValid(boolean setMessage) {
        if (!isEmptyField(fields.getPassword())) {
            errors.setPassword(null);
            notifyPropertyChanged(BR.passwordError);
            return true;
        } else {
            if (setMessage) {
                errors.setPassword("পাসওয়ার্ড খালি হতে পারবেনা");
                notifyPropertyChanged(BR.passwordError);
            }
            return false;
        }
    }

    public void onClick() {
        if (!isValid(true)) {
            validationStatus.setSuccess(false);
            validationStatus.setMessage(null);
        } else {
            validationStatus.setSuccess(true);
            validationStatus.setMessage(null);
        }
        validation.postValue(validationStatus);
    }

    public UserFields getFields() {
        return fields;
    }

    @Bindable
    public String getUserIdError() {
        return errors.getUserId();
    }

    @Bindable
    public String getPasswordError() {
        return errors.getPassword();
    }
}
