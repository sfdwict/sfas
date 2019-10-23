package org.sdfw.biometric.network;

import org.sdfw.biometric.network.response.MessageResponse;

public interface OnResponseErrorListener {
    void onHttpError(int hashCode, int responseCode, MessageResponse response);
    void onError(int hashCode, MessageResponse response);
}
