package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.FingerprintAuthForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.model.User;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.FingerprintAuthResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class FingerprintAuthViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private FingerprintAuthForm fingerprintAuth;

    private SingleLiveEvent<ResponseWrapper> authenticateLiveData;

    @Inject
    public FingerprintAuthViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        fingerprintAuth = new FingerprintAuthForm();
        authenticateLiveData = new SingleLiveEvent<>();
        // set last logged in user id
        fingerprintAuth.getFields().setUserId(sessionManager.getUserId());
    }

    public FingerprintAuthForm getFingerprintAuthForm() {
        return fingerprintAuth;
    }

    public Maybe<User> findUser(String ein) {
        return userRepository.findUser(ein);
    }

    public void onVerificationStart() {
        fingerprintAuth.onVerificationStart();
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return fingerprintAuth.getValidationStatus();
    }

    public void authenticate() {
        Log.d(TAG, "authenticate: called");
        compositeDisposable.clear();
        /*compositeDisposable.add(findUser(fingerprintAuth.getFields().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                            sessionManager.setUserId(user.getUserId());
                            FingerprintAuthResponse response = new FingerprintAuthResponse();
                            response.setSuccess(true);
                            response.setUser(user);
                            authenticateLiveData.postValue(new ResponseWrapper(response));
                        },
                        throwable -> {
                            MessageResponse response = new MessageResponse();
                            response.setSuccess(false);
                            response.setMessage("Authentication was not successful");
                            authenticateLiveData.postValue(new ResponseWrapper(response));
                        },
                        this::remoteAuthenticate)
        );*/

        remoteAuthenticate();
    }

    public SingleLiveEvent<ResponseWrapper> getAuthenticationStatus() {
        return authenticateLiveData;
    }

    private void remoteAuthenticate() {
        compositeDisposable.add(webservice.fingerprintAuth(fingerprintAuth.getFields().getUserId(),
                getFingerprintAuthForm().getFields().getTemplate1())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<FingerprintAuthResponse>(this, converter, this.hashCode()) {

                    @Override
                    protected void onSuccess(FingerprintAuthResponse response) {
                        sessionManager.setUserIdAndAccessToken(response.getUser().getUserId(), response.getAccessToken());
                        userRepository.saveUser(response.getUser());
                        authenticateLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        authenticateLiveData.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        authenticateLiveData.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
