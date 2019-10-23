package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.LoanDisbursementFields;
import org.sdfw.biometric.form.field.LoanDisbursementStatusFields;


public class LoanDisbursementForm extends BaseForm {

    private LoanDisbursementFields fields = new LoanDisbursementFields();
    private LoanDisbursementStatusFields errors = new LoanDisbursementStatusFields();

    public boolean isValid(boolean setMessage) {
//        boolean memberIdValid = isMemberIdValid(setMessage);
//        boolean centerIdValid = isCenterIdValid(setMessage);
//        boolean memberNameValid = isMemberNameValid(setMessage);
//        boolean parentValid = isParentValid(setMessage);
//        boolean nidNumberValid = isNidNumberValid(setMessage);
//        boolean mobileNumberValid = isMobileNumberValid(setMessage);
//        boolean disbursementAmountValid = isDisbursementAmountValid(setMessage);

//        return memberIdValid && centerIdValid && memberNameValid && parentValid
//                && nidNumberValid && mobileNumberValid && disbursementAmountValid;
        return isPassbookNoValid(setMessage);
    }

    public boolean isMemberIdValid(boolean setMessage) {
        if (!isEmptyField(fields.getMemberId())) {
            errors.setMemberId(null);
            notifyPropertyChanged(BR.memberIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setMemberId("মেম্বার আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.memberIdError);
            }
            return false;
        }
    }

    public boolean isCenterIdValid(boolean setMessage) {
        if (!isEmptyField(fields.getCenterId())) {
            errors.setCenterId(null);
            notifyPropertyChanged(BR.centerIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setCenterId("সেন্টার আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.centerIdError);
            }
            return false;
        }
    }

    public boolean isMemberNameValid(boolean setMessage) {
        if (!isEmptyField(fields.getMemberName())) {
            errors.setMemberName(null);
            notifyPropertyChanged(BR.memberNameError);
            return true;
        } else {
            if (setMessage) {
                errors.setMemberName("মেম্বারের নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.memberNameError);
            }
            return false;
        }
    }

    public boolean isParentValid(boolean setMessage) {
        if (!isEmptyField(fields.getSpouseName())) {
            errors.setSpouseName(null);
            notifyPropertyChanged(BR.parentError);
            return true;
        } else {
            if (setMessage) {
                errors.setSpouseName("স্বামী/অভিভাবকের নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.parentError);
            }
            return false;
        }
    }

    public boolean isNidNumberValid(boolean setMessage) {
        if (!isEmptyField(fields.getVoterId())) {
            errors.setVoterId(null);
            notifyPropertyChanged(BR.nidNumberError);
            return true;
        } else {
            if (setMessage) {
                errors.setVoterId("এন আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.nidNumberError);
            }
            return false;
        }
    }

    public boolean isMobileNumberValid(boolean setMessage) {
        if (!isEmptyField(fields.getMobileNo())) {
            errors.setMobileNo(null);
            notifyPropertyChanged(BR.mobileNumberError);
            return true;
        } else {
            if (setMessage) {
                errors.setMobileNo("মোবাইল নাম্বার খালি হতে পারবেনা");
                notifyPropertyChanged(BR.mobileNumberError);
            }
            return false;
        }
    }

    public boolean isDisbursementAmountValid(boolean setMessage) {
        if (fields.getDisbursementAmount() > 0 ) {
            errors.setDisbursementAmount(null);
            notifyPropertyChanged(BR.disbursementAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setDisbursementAmount("বিতরণের পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.disbursementAmountError);
            }
            return false;
        }
    }

    public boolean isPassbookNoValid(boolean setMessage) {
        if (!isEmptyField(fields.getPassbookNo())) {
            errors.setPassbookNo(null);
            notifyPropertyChanged(BR.passbookNoError);
            return true;
        } else {
            if (setMessage) {
                errors.setPassbookNo("পাসবুক নাম্বার খালি হতে পারবেনা");
                notifyPropertyChanged(BR.passbookNoError);
            }
            return false;
        }
    }

    public void onClick() {
        if (!isValid(true)) {
            validationStatus.setSuccess(false);
            validationStatus.setMessage("ফর্মটি যথাযথভাবে পূরণ করা হয়নি");
        } else {
            validationStatus.setSuccess(true);
            validationStatus.setMessage(null);
        }
        validation.postValue(validationStatus);
    }

    public LoanDisbursementFields getFields() {
        return fields;
    }

    @Bindable
    public String getDisbursementAmount() {
        return String.valueOf(fields.getDisbursementAmount());
    }

    public void setDisbursementAmount(String disbursementAmountStr) {
        fields.setDisbursementAmount(Double.parseDouble(disbursementAmountStr));
        notifyPropertyChanged(BR.disbursementAmount);
    }

    @Bindable
    public String getMemberIdError() {
        return errors.getMemberId();
    }

    @Bindable
    public String getCenterIdError() {
        return errors.getCenterId();
    }

    @Bindable
    public String getMemberNameError() {
        return errors.getMemberName();
    }

    @Bindable
    public String getParentError() {
        return errors.getSpouseName();
    }

    @Bindable
    public String getNidNumberError() {
        return errors.getVoterId();
    }

    @Bindable
    public String getMobileNumberError() {
        return errors.getMobileNo();
    }

    @Bindable
    public String getDisbursementAmountError() {
        return errors.getDisbursementAmount();
    }

    @Bindable
    public String getPassbookNoError() {
        return errors.getPassbookNo();
    }

}
