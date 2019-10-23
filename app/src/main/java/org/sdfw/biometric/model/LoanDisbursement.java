package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "loan_disbursements", indices = {@Index(value = {"day_opening", "branch_id", "center_id", "member_id"}, unique = true)})
public class LoanDisbursement {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @ColumnInfo(name = "day_opening")
    @SerializedName("day_opening")
    @Expose
    private String dayOpening;

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

    @ColumnInfo(name = "loan_code")
    @SerializedName("loan_code")
    @Expose
    private String loanCode;

    @ColumnInfo(name = "term_no")
    @SerializedName("term_no")
    @Expose
    private String termNo;

    @ColumnInfo(name = "member_name")
    @SerializedName("member_name")
    @Expose
    private String memberName;

    @ColumnInfo(name = "spouse_name")
    @SerializedName("spouse_name")
    @Expose
    private String spouseName;

    @ColumnInfo(name = "voter_id")
    @SerializedName("voter_id")
    @Expose
    private String voterId;

    @ColumnInfo(name = "mobile_no")
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;

    @ColumnInfo(name = "disbursement_amount")
    @SerializedName("disbursement_amount")
    @Expose
    private double disbursementAmount;

    @ColumnInfo(name = "is_synced")
    @SerializedName("is_synced")
    @Expose
    private String isSynced;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    @Expose
    private String image;

    @ColumnInfo(name = "passbook_n")
    @SerializedName("passbook_no")
    @Expose
    private String passbookNo;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getDayOpening() {
        return dayOpening;
    }

    public void setDayOpening(String dayOpening) {
        this.dayOpening = dayOpening;
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

    public String getLoanCode() {
        return loanCode;
    }

    public void setLoanCode(String loanCode) {
        this.loanCode = loanCode;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
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

    public double getDisbursementAmount() {
        return disbursementAmount;
    }

    public void setDisbursementAmount(double disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public String getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(String isSynced) {
        this.isSynced = isSynced;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassbookNo() {
        return passbookNo;
    }

    public void setPassbookNo(String passbookNo) {
        this.passbookNo = passbookNo;
    }
}
