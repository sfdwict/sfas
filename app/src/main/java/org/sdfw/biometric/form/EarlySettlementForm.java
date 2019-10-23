package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.EarlySettlementFields;
import org.sdfw.biometric.form.field.EarlySettlementStatusFields;


public class EarlySettlementForm extends BaseForm {

    private EarlySettlementFields fields = new EarlySettlementFields();
    private EarlySettlementStatusFields errors = new EarlySettlementStatusFields();

    public boolean isValid(boolean setMessage) {
//        boolean memberIdValid = isMemberIdValid(setMessage);
//        boolean centerIdValid = isCenterIdValid(setMessage);
//        boolean memberNameValid = isMemberNameValid(setMessage);
//        boolean parentValid = isParentValid(setMessage);
//        boolean voterIdValid = isVoterIdValid(setMessage);
//        boolean mobileNumberValid = isMobileNumberValid(setMessage);
//        boolean receivedAmountValid = isReceivedAmountValid(setMessage);

//        return memberIdValid && centerIdValid && memberNameValid && parentValid && voterIdValid
//                && mobileNumberValid && receivedAmountValid;
        return true;
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

    public boolean isVoterIdValid(boolean setMessage) {
        if (!isEmptyField(fields.getVoterId())) {
            errors.setVoterId(null);
            notifyPropertyChanged(BR.voterIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setVoterId("এন আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.voterIdError);
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

    public boolean isReceivedAmountValid(boolean setMessage) {
        if (fields.getReceivedAmount() > 0 ) {
            errors.setReceivedAmount(null);
            notifyPropertyChanged(BR.disbursementAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setReceivedAmount("লোন পূর্ণ পরিশোধের পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.receivedAmountError);
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

    public EarlySettlementFields getFields() {
        return fields;
    }

    @Bindable
    public String getReceivedAmount() {
        return String.valueOf(fields.getReceivedAmount());
    }

    public void setReceivedAmount(String receivedAmountStr) {
        fields.setReceivedAmount(Double.parseDouble(receivedAmountStr));
        notifyPropertyChanged(BR.receivedAmount);
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
    public String getVoterIdError() {
        return errors.getVoterId();
    }

    @Bindable
    public String getMobileNumberError() {
        return errors.getMobileNo();
    }

    @Bindable
    public String getReceivedAmountError() {
        return errors.getReceivedAmount();
    }

}
