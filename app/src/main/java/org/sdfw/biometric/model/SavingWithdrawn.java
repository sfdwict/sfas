package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "saving_withdrawns", indices = {@Index(value = {"day_opening", "branch_id", "center_id", "member_id"}, unique = true)})
public class SavingWithdrawn {

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

    @ColumnInfo(name = "withdraw_type")
    @SerializedName("withdraw_type")
    @Expose
    private String withdrawType;

    @ColumnInfo(name = "my_savings_amount")
    @SerializedName("my_savings_amount")
    @Expose
    private double mySavingsAmount;

    @ColumnInfo(name = "family_savings_amount")
    @SerializedName("family_savings_amount")
    @Expose
    private double familySavingsAmount;

    @ColumnInfo(name = "dps_amount")
    @SerializedName("dps_amount")
    @Expose
    private double dpsAmount;

    @ColumnInfo(name = "lakhpati_amount")
    @SerializedName("lakhpati_amount")
    @Expose
    private double lakhpatiAmount;

    @ColumnInfo(name = "double_savings_amount")
    @SerializedName("double_savings_amount")
    @Expose
    private double doubleSavingsAmount;

    @ColumnInfo(name = "is_synced")
    @SerializedName("is_synced")
    @Expose
    private String isSynced;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    @Expose
    private String image;

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

    public String getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    public double getMySavingsAmount() {
        return mySavingsAmount;
    }

    public void setMySavingsAmount(double mySavingsAmount) {
        this.mySavingsAmount = mySavingsAmount;
    }

    public double getFamilySavingsAmount() {
        return familySavingsAmount;
    }

    public void setFamilySavingsAmount(double familySavingsAmount) {
        this.familySavingsAmount = familySavingsAmount;
    }

    public double getDpsAmount() {
        return dpsAmount;
    }

    public void setDpsAmount(double dpsAmount) {
        this.dpsAmount = dpsAmount;
    }

    public double getLakhpatiAmount() {
        return lakhpatiAmount;
    }

    public void setLakhpatiAmount(double lakhpatiAmount) {
        this.lakhpatiAmount = lakhpatiAmount;
    }

    public double getDoubleSavingsAmount() {
        return doubleSavingsAmount;
    }

    public void setDoubleSavingsAmount(double doubleSavingsAmount) {
        this.doubleSavingsAmount = doubleSavingsAmount;
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
}
