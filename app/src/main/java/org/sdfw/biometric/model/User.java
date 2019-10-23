package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users", indices = {@Index(value = "user_id", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    @SerializedName("use_id")
    @Expose
    @ColumnInfo(name = "user_id")
    private String userId;
    @SerializedName("user_name")
    @Expose
    @ColumnInfo(name = "user_name")
    private String userName;
    @SerializedName("branch_id")
    @Expose
    @ColumnInfo(name = "branch_id")
    private String branchId;
    @SerializedName("branch_name")
    @Expose
    @ColumnInfo(name = "branch_name")
    private String branchName;
    @SerializedName("app_role")
    @Expose
    @ColumnInfo(name = "app_role")
    private String appRole;
    @SerializedName("app_roles_name")
    @Expose
    @ColumnInfo(name = "app_roles_name")
    private String appRolesName;
    @SerializedName("registration_status")
    @Expose
    @ColumnInfo(name = "registration_status")
    private String registrationStatus;
    @SerializedName("template1")
    @Expose
    private String template1;
    @SerializedName("template2")
    @Expose
    private String template2;
    @SerializedName("template3")
    @Expose
    private String template3;
    @SerializedName("template4")
    @Expose
    private String template4;
    @SerializedName("device_id")
    @Expose
    @ColumnInfo(name = "device_id")
    private String deviceId;
    @SerializedName("device_token")
    @Expose
    @ColumnInfo(name = "device_token")
    private String deviceToken;
    @SerializedName("opened_date")
    @Expose
    @ColumnInfo(name = "opened_date")
    private String openedDate;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAppRole() {
        return appRole;
    }

    public void setAppRole(String appRole) {
        this.appRole = appRole;
    }

    public String getAppRolesName() {
        return appRolesName;
    }

    public void setAppRolesName(String appRolesName) {
        this.appRolesName = appRolesName;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(String openedDate) {
        this.openedDate = openedDate;
    }
}
