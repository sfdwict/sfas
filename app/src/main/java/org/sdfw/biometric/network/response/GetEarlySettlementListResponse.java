package org.sdfw.biometric.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.sdfw.biometric.model.EarlySettlement;

import java.util.List;

public class GetEarlySettlementListResponse extends BaseResponse {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("day_opening")
    @Expose
    private String dayOpening;
    @SerializedName("data")
    @Expose
    private List<EarlySettlement> earlySettlements;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDayOpening() {
        return dayOpening;
    }

    public void setDayOpening(String dayOpening) {
        this.dayOpening = dayOpening;
    }

    public List<EarlySettlement> getEarlySettlements() {
        return earlySettlements;
    }

    public void setEarlySettlements(List<EarlySettlement> earlySettlements) {
        this.earlySettlements = earlySettlements;
    }
}
