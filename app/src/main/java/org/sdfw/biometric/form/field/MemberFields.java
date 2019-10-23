package org.sdfw.biometric.form.field;

import java.util.HashMap;
import java.util.Map;

public class MemberFields {

    private long id;
    private String branchId;
    private String centerId;
    private String memberId;
    private String groupId;
    private String memberName;
    private String saversOnlyFlag;
    private String biometricFlag;
    private String isBlank;
    private String fatherName;
    private String motherName;
    private String spouseName;
    private String sex;
    private String maritalStatus;
    private String address;
    private String successor;
    private String dateOfBirth;
    private String age;
    private String voterId;
    private String mobileNo;
    private String verificationStatus;
    private String image;
    private String template1;
    private String template2;
    private String template3;
    private String template4;

    // extra computed field
    private boolean isNewMember;
    private boolean hasFingerprint;
    private boolean hasImage;
    private boolean hasVoterId;

    private Map<String, Integer> saversFlagMap = new HashMap<>();
    private Map<String, Integer> genderMap = new HashMap<>();
    private Map<String, Integer> maritalMap = new HashMap<>();

    public MemberFields() {
        saversFlagMap.put("N", 0);
        saversFlagMap.put("Y", 1);
        genderMap.put("M", 0);
        genderMap.put("F", 1);
        maritalMap.put("M", 0);
        maritalMap.put("U", 1);
        maritalMap.put("W", 2);
        maritalMap.put("D", 3);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public Map<String, Integer> getSaversFlagMap() {
        return saversFlagMap;
    }

    public void setSaversFlagMap(Map<String, Integer> saversFlagMap) {
        this.saversFlagMap = saversFlagMap;
    }

    public Map<String, Integer> getGenderMap() {
        return genderMap;
    }

    public void setGenderMap(Map<String, Integer> genderMap) {
        this.genderMap = genderMap;
    }

    public Map<String, Integer> getMaritalMap() {
        return maritalMap;
    }

    public void setMaritalMap(Map<String, Integer> maritalMap) {
        this.maritalMap = maritalMap;
    }
}
