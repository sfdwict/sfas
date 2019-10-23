package org.sdfw.biometric.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.sdfw.biometric.model.SavingWithdrawn;

import java.util.List;

public class GetSavingWithdrawnListResponse extends BaseResponse {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("day_opening")
    @Expose
    private String dayOpening;
    @SerializedName("data")
    @Expose
    private List<SavingWithdrawn> savingWithdrawns;

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

    public List<SavingWithdrawn> getSavingWithdrawns() {
        return savingWithdrawns;
    }

    public void setSavingWithdrawns(List<SavingWithdrawn> savingWithdrawns) {
        this.savingWithdrawns = savingWithdrawns;
    }
}
