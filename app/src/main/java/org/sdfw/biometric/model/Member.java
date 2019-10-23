package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "members", indices = {@Index(value = {"branch_id", "center_id", "member_id"}, unique = true)})
public class Member {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    @ColumnInfo(name = "branch_id")
    @SerializedName("branch_id")
    @Expose
    private String branchId;
    @ColumnInfo(name = "center_id")
    @SerializedName("center_id")
    @Expose
    private String centerId;
    @ColumnInfo(name = "member_id")
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @ColumnInfo(name = "member_name")
    @SerializedName("member_name")
    @Expose
    private String memberName;
    @ColumnInfo(name = "savers_only_flag")
    @SerializedName("savers_only_flag")
    @Expose
    private String saversOnlyFlag;
    @ColumnInfo(name = "biometric_flag")
    @SerializedName("biometric_flag")
    @Expose
    private String biometricFlag;
    @ColumnInfo(name = "is_blank")
    @SerializedName("is_blank")
    @Expose
    private String isBlank;
    @ColumnInfo(name = "father_name")
    @SerializedName("father_name")
    @Expose
    private String fatherName;
    @ColumnInfo(name = "mother_name")
    @SerializedName("mother_name")
    @Expose
    private String motherName;
    @ColumnInfo(name = "spouse_name")
    @SerializedName("spous_name")
    @Expose
    private String spouseName;
    @ColumnInfo(name = "sex")
    @SerializedName("sex")
    @Expose
    private String sex;
    @ColumnInfo(name = "marital_status")
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;
    @ColumnInfo(name = "address")
    @SerializedName("address")
    @Expose
    private String address;
    @ColumnInfo(name = "successor")
    @SerializedName("successor")
    @Expose
    private String successor;
    @ColumnInfo(name = "date_of_birth")
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @ColumnInfo(name = "voter_id")
    @SerializedName("voter_id")
    @Expose
    private String voterId;
    @ColumnInfo(name = "mobile_no")
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @ColumnInfo(name = "verification_status")
    @SerializedName("verification_status")
    @Expose
    private String verificationStatus;
    @ColumnInfo(name = "image")
    @SerializedName("image")
    @Expose
    private String image;
    @ColumnInfo(name = "template1")
    @SerializedName("template1")
    @Expose
    private String template1;
    @ColumnInfo(name = "template2")
    @SerializedName("template2")
    @Expose
    private String template2;
    @ColumnInfo(name = "template3")
    @SerializedName("template3")
    @Expose
    private String template3;
    @ColumnInfo(name = "template4")
    @SerializedName("template4")
    @Expose
    private String template4;

    // extra computed field
    @ColumnInfo(name = "is_new_member")
    private boolean isNewMember;
    @ColumnInfo(name = "has_fingerprint")
    private boolean hasFingerprint;
    @ColumnInfo(name = "has_image")
    private boolean hasImage;
    @ColumnInfo(name = "has_voter_id")
    private boolean hasVoterId;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getSaversOnlyFlag() {
        return saversOnlyFlag;
    }

    public void setSaversOnlyFlag(String saversOnlyFlag) {
        this.saversOnlyFlag = saversOnlyFlag;
    }

    public String getBiometricFlag() {
        return biometricFlag;
    }

    public void setBiometricFlag(String biometricFlag) {
        this.biometricFlag = biometricFlag;
    }

    public String getIsBlank() {
        return isBlank;
    }

    public void setIsBlank(String isBlank) {
        this.isBlank = isBlank;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuccessor() {
        return successor;
    }

    public void setSuccessor(String successor) {
        this.successor = successor;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTemplate1() {
        return template1;
    }

    public void setTemplate1(String template1) {
        this.template1 = template1;
    }

    public String getTemplate2() {
        return template2;
    }

    public void setTemplate2(String template2) {
        this.template2 = template2;
    }

    public String getTemplate3() {
        return template3;
    }

    public void setTemplate3(String template3) {
        this.template3 = template3;
    }

    public String getTemplate4() {
        return template4;
    }

    public void setTemplate4(String template4) {
        this.template4 = template4;
    }

    public boolean isNewMember() {
        return isNewMember;
    }

    public void setNewMember(boolean newMember) {
        isNewMember = newMember;
    }

    public boolean isHasFingerprint() {
        return hasFingerprint;
    }

    public void setHasFingerprint(boolean hasFingerprint) {
        this.hasFingerprint = hasFingerprint;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasVoterId() {
        return hasVoterId;
    }

    public void setHasVoterId(boolean hasVoterId) {
        this.hasVoterId = hasVoterId;
    }
}
