package org.sdfw.biometric.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.sdfw.biometric.model.Member;

import java.util.List;

public class FetchMemberMatrixResponse extends BaseResponse {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("data")
    @Expose
    private List<Member> members;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
