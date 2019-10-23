package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.SavingWithdrawnFields;
import org.sdfw.biometric.form.field.SavingWithdrawnStatusFields;


public class SavingWithdrawnForm extends BaseForm {

    private SavingWithdrawnFields fields = new SavingWithdrawnFields();
    private SavingWithdrawnStatusFields errors = new SavingWithdrawnStatusFields();

    public boolean isValid(boolean setMessage) {
//        boolean memberIdValid = isMemberIdValid(setMessage);
//        boolean centerIdValid = isCenterIdValid(setMessage);
//        boolean memberNameValid = isMemberNameValid(setMessage);
//        boolean parentValid = isParentValid(setMessage);
//        boolean voterIdValid = isVoterIdValid(setMessage);
//        boolean mobileNumberValid = isMobileNumberValid(setMessage);
//        boolean withdrawnTypeValid = isWithdrawnTypeValid(setMessage);
//        boolean mySavingAmountValid = isMySavingAmountValid(setMessage);
//        boolean familySavingAmountValid = isFamilySavingAmountValid(setMessage);
//        boolean dpsAmountValid = isDpsAmountValid(setMessage);

//        return memberIdValid && centerIdValid && memberNameValid && parentValid && voterIdValid
//                && mobileNumberValid && withdrawnTypeValid && dpsAmountValid;
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

    public boolean isWithdrawnTypeValid(boolean setMessage) {
        if (!isEmptyField(fields.getWithdrawType())) {
            errors.setWithdrawType(null);
            notifyPropertyChanged(BR.withdrawnTypeError);
            return true;
        } else {
            if (setMessage) {
                errors.setWithdrawType("উত্তোলনের ধরণ খালি হতে পারবেনা");
                notifyPropertyChanged(BR.withdrawnTypeError);
            }
            return false;
        }
    }

    public boolean isMySavingAmountValid(boolean setMessage) {
        if (fields.getMySavingsAmount() > 0) {
            errors.setMySavingsAmount(null);
            notifyPropertyChanged(BR.mySavingAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setMySavingsAmount("মাই সেভিংসের পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.mySavingAmountError);
            }
            return false;
        }
    }

    public boolean isFamilySavingAmountValid(boolean setMessage) {
        if (fields.getFamilySavingsAmount() > 0) {
            errors.setFamilySavingsAmount(null);
            notifyPropertyChanged(BR.familySavingAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setFamilySavingsAmount("ফ্যামিলি সাভিংসের পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.familySavingAmountError);
            }
            return false;
        }
    }

    public boolean isDpsAmountValid(boolean setMessage) {
        if (fields.getDpsAmount() > 0) {
            errors.setDpsAmount(null);
            notifyPropertyChanged(BR.dpsAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setDpsAmount("ডিপিএস এর পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.dpsAmountError);
            }
            return false;
        }
    }

    public boolean isLakhpatiAmountValid(boolean setMessage) {
        if (fields.getLakhpatiAmount() > 0) {
            errors.setLakhpatiAmount(null);
            notifyPropertyChanged(BR.lakhpatiAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setLakhpatiAmount("লাখপতি এর পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.lakhpatiAmountError);
            }
            return false;
        }
    }

    public boolean isDoubleSavingsAmountValid(boolean setMessage) {
        if (fields.getDoubleSavingsAmount() > 0) {
            errors.setDoubleSavingsAmount(null);
            notifyPropertyChanged(BR.doubleSavingsAmountError);
            return true;
        } else {
            if (setMessage) {
                errors.setDoubleSavingsAmount("ডাবল সেভিংস এর পরিমান শূন্য হতে পারবেনা");
                notifyPropertyChanged(BR.doubleSavingsAmountError);
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

    public SavingWithdrawnFields getFields() {
        return fields;
    }

    @Bindable
    public String getMySavingAmount() {
        return String.valueOf(fields.getMySavingsAmount());
    }

    public void setMySavingAmount(String mySavingAmountStr) {
        fields.setMySavingsAmount(Double.parseDouble(mySavingAmountStr));
        notifyPropertyChanged(BR.mySavingAmount);
    }

    @Bindable
    public String getFamilySavingAmount() {
        return String.valueOf(fields.getFamilySavingsAmount());
    }

    public void setFamilySavingAmount(String familySavingAmountStr) {
        fields.setFamilySavingsAmount(Double.parseDouble(familySavingAmountStr));
        notifyPropertyChanged(BR.familySavingAmount);
    }

    @Bindable
    public String getDpsAmount() {
        return String.valueOf(fields.getDpsAmount());
    }

    public void setDpsAmount(String dpsAmountStr) {
        fields.setDpsAmount(Double.parseDouble(dpsAmountStr));
        notifyPropertyChanged(BR.dpsAmount);
    }

    @Bindable
    public String getLakhpatiAmount() {
        return String.valueOf(fields.getLakhpatiAmount());
    }

    public void setLakhpatiAmount(String lakhpatiAmountStr) {
        fields.setLakhpatiAmount(Double.parseDouble(lakhpatiAmountStr));
        notifyPropertyChanged(BR.lakhpatiAmount);
    }

    @Bindable
    public String getDoubleSavingsAmount() {
        return String.valueOf(fields.getDoubleSavingsAmount());
    }

    public void setDoubleSavingsAmount(String doubleSavingsAmountStr) {
        fields.setDoubleSavingsAmount(Double.parseDouble(doubleSavingsAmountStr));
        notifyPropertyChanged(BR.doubleSavingsAmount);
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
    public String getWithdrawnTypeError() {
        return errors.getWithdrawType();
    }

    @Bindable
    public String getMySavingAmountError() {
        return errors.getMySavingsAmount();
    }

    @Bindable
    public String getFamilySavingAmountError() {
        return errors.getFamilySavingsAmount();
    }

    @Bindable
    public String getDpsAmountError() {
        return errors.getDpsAmount();
    }

    @Bindable
    public String getLakhpatiAmountError() {
        return errors.getLakhpatiAmount();
    }

    @Bindable
    public String getDoubleSavingsAmountError() {
        return errors.getDoubleSavingsAmount();
    }

}
