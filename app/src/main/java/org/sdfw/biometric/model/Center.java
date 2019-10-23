package org.sdfw.biometric.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "centers", indices = {@Index(value = {"branch_id", "center_id"}, unique = true)})
public class Center {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    @SerializedName("branch_id")
    @Expose
    @ColumnInfo(name = "branch_id")
    private String branchId;
    @SerializedName("center_id")
    @Expose
    @ColumnInfo(name = "center_id")
    private String centerId;
    @SerializedName("center_name")
    @Expose
    @ColumnInfo(name = "center_name")
    private String centerName;

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

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }
}
