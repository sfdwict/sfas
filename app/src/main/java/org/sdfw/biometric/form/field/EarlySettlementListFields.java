package org.sdfw.biometric.form.field;

import org.sdfw.biometric.model.EarlySettlement;

import java.util.ArrayList;
import java.util.List;

public class EarlySettlementListFields {

    private String userId;
    private String branchId;
    private String openingDate;
    private List<EarlySettlement> earlySettlements = new ArrayList<>();

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

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public List<EarlySettlement> getEarlySettlements() {
        return earlySettlements;
    }

    public void setEarlySettlements(List<EarlySettlement> earlySettlements) {
        this.earlySettlements = earlySettlements;
    }

    public void clearEarlySettlement() {
        this.earlySettlements.clear();
    }
}
