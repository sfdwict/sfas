package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import org.sdfw.biometric.form.SigninForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.network.response.SigninResponse;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class SigninViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private SigninForm signin;

    private SingleLiveEvent<ResponseWrapper> loginLiveData;

    @Inject
    public SigninViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        signin = new SigninForm();
        loginLiveData = new SingleLiveEvent<>();
    }

    public SigninForm getSigninForm() {
        return signin;
    }

    public void onSigninClick() {
        signin.onClick();
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return signin.getValidationStatus();
    }

    public void signin() {
        compositeDisposable.clear();
        compositeDisposable.add(webservice.signin(signin.getFields().getUserId(), signin.getFields().getPassword())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<SigninResponse>(this, converter, this.hashCode()) {

                    @Override
                    protected void onSuccess(SigninResponse response) {
                        sessionManager.setUserIdAndAccessToken(response.getUser().getUserId(), response.getAccessToken());
                        userRepository.saveUser(response.getUser());
                        loginLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    public SingleLiveEvent<ResponseWrapper> getSigninStatus() {
        return loginLiveData;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        loginLiveData.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        loginLiveData.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared(){
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
