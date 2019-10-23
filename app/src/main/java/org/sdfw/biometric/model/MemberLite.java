package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;

public class MemberLite {

    @ColumnInfo(name = "branch_id")
    private String branchId;
    @ColumnInfo(name = "center_id")
    private String centerId;
    @ColumnInfo(name = "member_id")
    private String memberId;
    @ColumnInfo(name = "member_name")
    private String memberName;
    @ColumnInfo(name = "is_new_member")
    private boolean isNewMember;
    @ColumnInfo(name = "has_fingerprint")
    private boolean hasFingerprint;
    @ColumnInfo(name = "has_image")
    private boolean hasImage;
    @ColumnInfo(name = "has_voter_id")
    private boolean hasVoterId;

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
