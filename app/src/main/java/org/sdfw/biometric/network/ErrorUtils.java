package org.sdfw.biometric.network;
import com.google.gson.JsonSyntaxException;

import org.sdfw.biometric.network.response.MessageResponse;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by jtanveer on 9/7/16.
 */

public class ErrorUtils {

    public static MessageResponse parseError(Converter<ResponseBody, MessageResponse> converter, Response<?> response) {

        MessageResponse error;

        try {
            error = converter.convert(Objects.requireNonNull(response.errorBody()));
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            error = new MessageResponse("কার্যক্রমটি সফল হয়নি");
        }

        return error;
    }
}
