package org.sdfw.biometric.network;

import android.util.Log;

import org.sdfw.biometric.network.response.MessageResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;

public abstract class ObserverWithErrorWrapper<T> extends DisposableObserver<T> {

    private static final String TAG = "RxTest";

    private OnResponseErrorListener listener;
    private Converter<ResponseBody, MessageResponse> converter;
    private int hashCode;

    public ObserverWithErrorWrapper(OnResponseErrorListener listener, Converter<ResponseBody, MessageResponse> converter, int hashCode) {
        this.listener = listener;
        this.converter = converter;
        this.hashCode = hashCode;
    }

    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            Log.d(TAG, "onError: HttpException");
            listener.onHttpError(hashCode, ((HttpException) e).code(), ErrorUtils.parseError(converter, ((HttpException) e).response()));
        } else if (e instanceof SocketTimeoutException) {
            Log.d(TAG, "onError: SocketTimeoutException");
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("রিকোয়েস্ট টাইম আউট হয়ে গিয়েছে");
            listener.onError(hashCode, response);
        } else if (e instanceof IOException) {
            Log.d(TAG, "onError: IOException");
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("নেটওয়ার্কের ত্রূটির কারণে সার্ভারে যোগাযোগ করা সম্ভব হয়নি");
            listener.onError(hashCode, response);
        } else {
            Log.d(TAG, "onError: UnknownException");
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("অজ্ঞাত কারণে রিকোয়েস্টটি সম্পন্ন হয়নি");
            listener.onError(hashCode, response);
        }
    }

    @Override
    public void onComplete() {
        // nothing to do
        Log.d(TAG, "onComplete: invoked");
    }
}
