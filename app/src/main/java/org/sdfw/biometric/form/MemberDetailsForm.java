package org.sdfw.biometric.form;

import android.databinding.Bindable;

import org.sdfw.biometric.BR;
import org.sdfw.biometric.form.field.MemberFields;
import org.sdfw.biometric.form.field.MemberStatusFields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;


public class MemberDetailsForm extends BaseForm {

    private MemberFields fields = new MemberFields();
    private MemberStatusFields errors = new MemberStatusFields();

    public boolean isValid(boolean setMessage) {
        boolean centerIdValid = isCenterIdValid(setMessage);
        boolean memberIdValid = isMemberIdValid(setMessage);
        boolean groupIdValid = isGroupIdValid(setMessage);
        boolean memberNameValid = isMemberNameValid(setMessage);
        boolean spouseNameValid = isSpouseNameValid(setMessage);
        boolean successorValid = isSuccessorValid(setMessage);
        boolean dateOfBirthValid = isDateOfBirthValid(setMessage);
        boolean fatherNameValid = isFatherNameValid(setMessage);
        boolean motherNameValid = isMotherNameValid(setMessage);
        boolean voterIdValid = isVoterIdValid(setMessage);
        boolean mobileNoValid = isMobileNoValid(setMessage);
        boolean addressValid = isAddressValid(setMessage);
        boolean sexValid = isSexValid(setMessage);
        boolean maritalStatusValid = isMaritalStatusValid(setMessage);
        boolean imageValid = isImageValid(setMessage);
        boolean templateValid = isTemplateValid(setMessage);
        return centerIdValid && memberIdValid && groupIdValid && memberNameValid && spouseNameValid
                && successorValid && dateOfBirthValid && fatherNameValid && motherNameValid && voterIdValid
                && mobileNoValid && addressValid && sexValid && maritalStatusValid && imageValid && templateValid;
    }

    public boolean isCenterIdValid(boolean setMessage) {
        if (!isEmptyField(fields.getCenterId())) {
            errors.setCenterId(null);
            notifyPropertyChanged(BR.centerIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setCenterId("সেন্টার আইডি খালি হতে পারবেন");
                notifyPropertyChanged(BR.centerIdError);
            }
            return false;
        }
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

    public boolean isGroupIdValid(boolean setMessage) {
        if (!isEmptyField(fields.getGroupId())) {
            errors.setGroupId(null);
            notifyPropertyChanged(BR.groupIdError);
            return true;
        } else {
            if (setMessage) {
                errors.setGroupId("গ্রূপ আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.groupIdError);
            }
            return false;
        }
    }

    public boolean isMemberNameValid(boolean setMessage) {
        if (isEmptyField(fields.getMemberName())) {
            if (setMessage) {
                errors.setMemberName("মেম্বারের নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.memberNameError);
            }
            return false;
        } else if (fields.getMemberName().length() > 50) {
            if (setMessage) {
                errors.setMemberName("মেম্বারের নাম ৫০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.memberNameError);
            }
            return false;
        } else {
            errors.setMemberName(null);
            notifyPropertyChanged(BR.memberNameError);
            return true;
        }
    }

    public boolean isSpouseNameValid(boolean setMessage) {
        if (isEmptyField(fields.getSpouseName())) {
            if (setMessage) {
                errors.setSpouseName("স্বামী/অভিভাবকের নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.spouseNameError);
            }
            return false;
        } else if (fields.getSpouseName().length() > 50) {
            if (setMessage) {
                errors.setSpouseName("স্বামী/অভিভাবকের নাম ৫০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.spouseNameError);
            }
            return false;
        } else {
            errors.setSpouseName(null);
            notifyPropertyChanged(BR.spouseNameError);
            return true;
        }
    }

    public boolean isSuccessorValid(boolean setMessage) {
        if (isEmptyField(fields.getSuccessor())) {
            if (setMessage) {
                errors.setSuccessor("নমিনীর নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.successorError);
            }
            return false;
        } else if (fields.getSuccessor().length() > 50) {
            if (setMessage) {
                errors.setSuccessor("নমিনীর নাম ৫০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.successorError);
            }
            return false;
        } else {
            errors.setSuccessor(null);
            notifyPropertyChanged(BR.successorError);
            return true;
        }
    }

    public boolean isDateOfBirthValid(boolean setMessage) {
        if (!isEmptyField(fields.getDateOfBirth())) {
            errors.setDateOfBirth(null);
            notifyPropertyChanged(BR.dateOfBirthError);
            return true;
        } else {
            if (setMessage) {
                errors.setDateOfBirth("জন্ম তারিখ খালি হতে পারবেনা");
                notifyPropertyChanged(BR.dateOfBirthError);
            }
            return false;
        }
    }

    public boolean isFatherNameValid(boolean setMessage) {
        if (isEmptyField(fields.getFatherName())) {
            if (setMessage) {
                errors.setFatherName("পিতার নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.fatherNameError);
            }
            return false;
        } else if (fields.getFatherName().length() > 50) {
            if (setMessage) {
                errors.setFatherName("পিতার নাম ৫০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.fatherNameError);
            }
            return false;
        } else {
            errors.setFatherName(null);
            notifyPropertyChanged(BR.fatherNameError);
            return true;
        }
    }

    public boolean isMotherNameValid(boolean setMessage) {
        if (isEmptyField(fields.getMotherName())) {
            if (setMessage) {
                errors.setMotherName("মাতার নাম খালি হতে পারবেনা");
                notifyPropertyChanged(BR.motherNameError);
            }
            return false;
        } else if (fields.getMotherName().length() > 50) {
            if (setMessage) {
                errors.setMotherName("মাতার নাম ৫০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.motherNameError);
            }
            return false;
        } else {
            errors.setMotherName(null);
            notifyPropertyChanged(BR.motherNameError);
            return true;
        }
    }

    public boolean isVoterIdValid(boolean setMessage) {
        if (isEmptyField(fields.getVoterId())) {
            if (setMessage) {
                errors.setVoterId("এন আইডি খালি হতে পারবেনা");
                notifyPropertyChanged(BR.voterIdError);
            }
            return false;
        } else if (!Arrays.asList(10, 13, 17).contains(fields.getVoterId().length())) {
            if (setMessage) {
                errors.setVoterId("এন আইডি ১০, ১৩ অথবা ১৭ সংখ্যার হতে হবে");
                notifyPropertyChanged(BR.voterIdError);
            }
            return false;
        } else {
            errors.setVoterId(null);
            notifyPropertyChanged(BR.voterIdError);
            return true;
        }
    }

    public boolean isMobileNoValid(boolean setMessage) {
        if (isEmptyField(fields.getMobileNo())) {
            if (setMessage) {
                errors.setMobileNo("মোবাইল নাম্বার খালি হতে পারবেনা");
                notifyPropertyChanged(BR.mobileNoError);
            }
            return false;
        } else if (fields.getMobileNo().length() != 11) {
            if (setMessage) {
                errors.setMobileNo("মোবাইল নাম্বার ১১ সংখ্যার হতে হবে");
                notifyPropertyChanged(BR.mobileNoError);
            }
            return false;
        } else {
            errors.setMobileNo(null);
            notifyPropertyChanged(BR.mobileNoError);
            return true;
        }
    }

    public boolean isAddressValid(boolean setMessage) {
        if (isEmptyField(fields.getAddress())) {
            if (setMessage) {
                errors.setAddress("ঠিকানা খালি হতে পারবেনা");
                notifyPropertyChanged(BR.addressError);
            }
            return false;
        } else if (fields.getAddress().length() > 100) {
            if (setMessage) {
                errors.setAddress("ঠিকানা ১০০ অক্ষরের চেয়ে বড় হতে পারবেনা");
                notifyPropertyChanged(BR.addressError);
            }
            return false;
        } else {
            errors.setAddress(null);
            notifyPropertyChanged(BR.addressError);
            return true;
        }
    }

    public boolean isSexValid(boolean setMessage) {
        if (!isEmptyField(fields.getSex())) {
            errors.setSex(null);
            notifyPropertyChanged(BR.sexError);
            return true;
        } else {
            if (setMessage) {
                errors.setSex("নারী/পুরুষ নির্বাচন করা হয়নি");
                notifyPropertyChanged(BR.sexError);
            }
            return false;
        }
    }

    public boolean isMaritalStatusValid(boolean setMessage) {
        if (!isEmptyField(fields.getMaritalStatus())) {
            errors.setMaritalStatus(null);
            notifyPropertyChanged(BR.maritaLStatusError);
            return true;
        } else {
            if (setMessage) {
                errors.setMaritalStatus("বৈবাহিক অবস্থা নির্বাচন করা হয়নি");
                notifyPropertyChanged(BR.maritaLStatusError);
            }
            return false;
        }
    }

    public boolean isImageValid(boolean setMessage) {
        if (!isEmptyField(fields.getImage())) {
            errors.setImage(null);
            notifyPropertyChanged(BR.imageError);
            return true;
        } else {
            if (setMessage) {
                errors.setImage("ছবি নির্বাচন করা হয়নি");
                notifyPropertyChanged(BR.imageError);
            }
            return false;
        }
    }

    public boolean isTemplateValid(boolean setMessage) {
        if (!isEmptyField(fields.getTemplate1()) && !isEmptyField(fields.getTemplate2())
                && !isEmptyField(fields.getTemplate3()) && !isEmptyField(fields.getTemplate4())) {
            errors.setTemplate(null);
            notifyPropertyChanged(BR.templateError);
            return true;
        } else {
            if (setMessage) {
                errors.setTemplate("আঙুলের ছাপ নিবন্ধন করা হয়নি");
                notifyPropertyChanged(BR.templateError);
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

    public MemberFields getFields() {
        return fields;
    }

    private String calculateAge(String dateOfBirth) {
        if (dateOfBirth == null) return "0";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Calendar dob = Calendar.getInstance();
        try {
            dob.setTime(sdf.parse(dateOfBirth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return String.valueOf(now.get(Calendar.YEAR) - dob.get(Calendar.YEAR));
    }

    public void setSex(String gender) {
        fields.setSex(gender);
        notifyPropertyChanged(BR.sexIndex);
    }

    public void setMarital(String marital) {
        fields.setMaritalStatus(marital);
        notifyPropertyChanged(BR.maritalStatusIndex);
    }

    public void setDateOfBirthForm(String dateOfBirth) {
        fields.setDateOfBirth(dateOfBirth);
        fields.setAge(calculateAge(dateOfBirth));
        notifyPropertyChanged(BR.dateOfBirth);
        notifyPropertyChanged(BR.age);
    }

    @Bindable
    public int getSaversOnlyFlagIndex() {
        if (fields.getSaversOnlyFlag() == null || fields.getSaversOnlyFlag().isEmpty()) {
            return 1;
        }
        return fields.getSaversFlagMap().get(fields.getSaversOnlyFlag()) + 1;
    }

    @Bindable
    public void setSaversOnlyFlagIndex(int index) {
        fields.setSaversOnlyFlag("");
        for (Map.Entry<String, Integer> entry : fields.getSaversFlagMap().entrySet()) {
            if (entry.getValue() == index - 1) {
                fields.setSaversOnlyFlag(entry.getKey());
                break;
            }
        }
    }

    @Bindable
    public String getDateOfBirth() {
        return fields.getDateOfBirth();
    }

    @Bindable
    public void setDateOfBirth(String dateOfBirth) {
        fields.setDateOfBirth(dateOfBirth);
    }

    @Bindable
    public String getAge() {
        return fields.getAge();
    }

    @Bindable
    public int getSexIndex() {
        if (fields.getSex() == null || fields.getSex().isEmpty()) {
            return 2;
        }
        return fields.getGenderMap().get(fields.getSex()) + 1;
    }

    @Bindable
    public void setSexIndex(int index) {
        fields.setSex("");
        for (Map.Entry<String, Integer> entry : fields.getGenderMap().entrySet()) {
            if (entry.getValue() == index - 1) {
                fields.setSex(entry.getKey());
                break;
            }
        }
    }

    @Bindable
    public int getMaritalStatusIndex() {
        if (fields.getMaritalStatus() == null || fields.getMaritalStatus().isEmpty()) {
            return 1;
        }
        return fields.getMaritalMap().get(fields.getMaritalStatus()) + 1;
    }

    @Bindable
    public void setMaritalStatusIndex(int index) {
        fields.setMaritalStatus("");
        for (Map.Entry<String, Integer> entry : fields.getMaritalMap().entrySet()) {
            if (entry.getValue() == index - 1) {
                fields.setMaritalStatus(entry.getKey());
                break;
            }
        }
    }

    @Bindable
    public String getCenterIdError() {
        return errors.getCenterId();
    }

    @Bindable
    public String getMemberIdError() {
        return errors.getMemberId();
    }

    @Bindable
    public String getGroupIdError() {
        return errors.getGroupId();
    }

    @Bindable
    public String getMemberNameError() {
        return errors.getMemberName();
    }

    @Bindable
    public String getSpouseNameError() {
        return errors.getSpouseName();
    }

    @Bindable
    public String getSuccessorError() {
        return errors.getSuccessor();
    }

    @Bindable
    public String getDateOfBirthError() {
        return errors.getDateOfBirth();
    }

    @Bindable
    public String getFatherNameError() {
        return errors.getFatherName();
    }

    @Bindable
    public String getMotherNameError() {
        return errors.getMotherName();
    }

    @Bindable
    public String getVoterIdError() {
        return errors.getVoterId();
    }

    @Bindable
    public String getAddressError() {
        return errors.getAddress();
    }

    @Bindable
    public String getMobileNoError() {
        return errors.getMobileNo();
    }

    @Bindable
    public String getSexError() {
        return errors.getSex();
    }

    @Bindable
    public String getMaritaLStatusError() {
        return errors.getMaritalStatus();
    }

    @Bindable
    public String getImageError() {
        return errors.getImage();
    }

    @Bindable
    public String getTemplateError() {
        return errors.getTemplate();
    }
}
