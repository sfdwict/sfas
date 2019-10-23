package org.sdfw.biometric.network.response;

public class ResponseWrapper {

    private int responseCode;
    private BaseResponse response;

    public ResponseWrapper(int responseCode, BaseResponse response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public ResponseWrapper(BaseResponse response) {
        this(0, response);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public BaseResponse getResponse() {
        return response;
    }

    public void setResponse(BaseResponse response) {
        this.response = response;
    }
}
