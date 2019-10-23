package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.UserFields;
import org.sdfw.biometric.form.field.UserStatusFields;


public class FingerprintRegistrationForm extends BaseForm {

    private UserFields fields = new UserFields();
    private UserStatusFields statuses = new UserStatusFields();

    public boolean isValid(boolean setMessage) {
        boolean template1Valid = isTemplate1Valid(setMessage);
        boolean template2Valid = isTemplate2Valid(setMessage);
        boolean template3Valid = isTemplate3Valid(setMessage);
        boolean template4Valid = isTemplate4Valid(setMessage);
        boolean deviceIdValid = isFieldValid(setMessage, fields.getDeviceId());
        return template1Valid && template2Valid && template3Valid && template4Valid && deviceIdValid;
    }

    public boolean isTemplate1Valid(boolean setMessage) {
        if (!isEmptyField(fields.getTemplate1())) {
            if (setMessage) {
                statuses.setTemplate1("✓");
                notifyPropertyChanged(BR.template1);
                notifyPropertyChanged(BR.enabled);
            }
            return true;
        } else {
            if (setMessage) {
                statuses.setTemplate1("–");
                notifyPropertyChanged(BR.template1);
                notifyPropertyChanged(BR.enabled);
            }
            return false;
        }
    }

    public boolean isTemplate2Valid(boolean setMessage) {
        if (!isEmptyField(fields.getTemplate2())) {
            if (setMessage) {
                statuses.setTemplate2("✓");
                notifyPropertyChanged(BR.template2);
                notifyPropertyChanged(BR.enabled);
            }
            return true;
        } else {
            if (setMessage) {
                statuses.setTemplate2("–");
                notifyPropertyChanged(BR.template2);
                notifyPropertyChanged(BR.enabled);
            }
            return false;
        }
    }

    public boolean isTemplate3Valid(boolean setMessage) {
        if (!isEmptyField(fields.getTemplate3())) {
            if (setMessage) {
                statuses.setTemplate3("✓");
                notifyPropertyChanged(BR.template3);
                notifyPropertyChanged(BR.enabled);
            }
            return true;
        } else {
            if (setMessage) {
                statuses.setTemplate3("–");
                notifyPropertyChanged(BR.template3);
                notifyPropertyChanged(BR.enabled);
            }
            return false;
        }
    }

    public boolean isTemplate4Valid(boolean setMessage) {
        if (!isEmptyField(fields.getTemplate4())) {
            if (setMessage) {
                statuses.setTemplate4("✓");
                notifyPropertyChanged(BR.template4);
                notifyPropertyChanged(BR.enabled);
            }
            return true;
        } else {
            if (setMessage) {
                statuses.setTemplate4("–");
                notifyPropertyChanged(BR.template4);
                notifyPropertyChanged(BR.enabled);
            }
            return false;
        }
    }

    public boolean isFieldValid(boolean setMessage, String field) {
        if (!isEmptyField(field)) {
            if (setMessage) {
                notifyPropertyChanged(BR.enabled);
            }
            return true;
        } else {
            if (setMessage) {
                notifyPropertyChanged(BR.enabled);
            }
            return false;
        }
    }

    public void onValidate() {
        isValid(true);
    }

    public UserFields getFields() {
        return fields;
    }

    @Bindable
    public String getTemplate1() {
        return statuses.getTemplate1();
    }

    @Bindable
    public String getTemplate2() {
        return statuses.getTemplate2();
    }

    @Bindable
    public String getTemplate3() {
        return statuses.getTemplate3();
    }

    @Bindable
    public String getTemplate4() {
        return statuses.getTemplate4();
    }

    @Bindable
    public boolean isEnabled() {
        return isValid(false);
    }
}
