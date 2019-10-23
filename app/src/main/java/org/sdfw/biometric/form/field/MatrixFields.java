package org.sdfw.biometric.form.field;

import org.sdfw.biometric.model.Center;
import org.sdfw.biometric.model.MemberLite;

import java.util.ArrayList;
import java.util.List;

public class MatrixFields {

    private String userId;
    private String branchId;
    private String branchName = "";
    private int centerIdIndex = 0;
    private List<Center> centers = new ArrayList<>();
    private List<MemberLite> members = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getCenterIdIndex() {
        return centerIdIndex;
    }

    public void setCenterIdIndex(int centerIdIndex) {
        this.centerIdIndex = centerIdIndex + 1; // hack for material spinner
    }

    public String getCenterId() {
        if (centerIdIndex > 0 && centerIdIndex <= centers.size()) {
            return centers.get(centerIdIndex - 1).getCenterId();
        }
        return null;
    }

    public List<Center> getCenters() {
        return centers;
    }

    public void setCenters(List<Center> centers) {
        this.centers.clear();
        this.centers.addAll(centers);
    }

    public List<MemberLite> getMembers() {
        return members;
    }

    public void setMembers(List<MemberLite> members) {
        this.members.clear();
        this.members.addAll(members);
    }

    public void clearMembers() {
        this.members.clear();
    }
}
