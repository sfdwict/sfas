package org.sdfw.biometric.form;

import android.databinding.BaseObservable;
import android.text.TextUtils;

import org.sdfw.biometric.lifecycle.SingleLiveEvent;

public class BaseForm extends BaseObservable {

    protected ValidationStatus validationStatus = new ValidationStatus();
    protected SingleLiveEvent<ValidationStatus> validation = new SingleLiveEvent<>();

    protected boolean isEmptyField(String field) {
        return field == null || TextUtils.isEmpty(field);
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return validation;
    }
}
