package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import org.sdfw.biometric.form.FingerprintRegistrationForm;
import org.sdfw.biometric.form.field.UserFields;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class FingerprintRegistrationViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private FingerprintRegistrationForm fingerprintRegistration;

    private SingleLiveEvent<ResponseWrapper> registrationLiveData;

    @Inject
    public FingerprintRegistrationViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        fingerprintRegistration = new FingerprintRegistrationForm();
        registrationLiveData = new SingleLiveEvent<>();
    }

    public FingerprintRegistrationForm getFingerprintRegistrationForm() {
        return fingerprintRegistration;
    }

    public void onTemplateValidate() {
        fingerprintRegistration.onValidate();
    }

    public SingleLiveEvent<ResponseWrapper> getRegistrationStatus() {
        return registrationLiveData;
    }

    public void registerFingerprints() {
        UserFields fields = getFingerprintRegistrationForm().getFields();
        compositeDisposable.clear();
        compositeDisposable.add(webservice.registerFingerprints(fields.getUserId(), fields.getTemplate1(),
                fields.getTemplate2(), fields.getTemplate3(), fields.getTemplate4(), fields.getDeviceId(),
                fields.getDeviceToken(), sessionManager.getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter, this.hashCode()) {

                    @Override
                    protected void onSuccess(MessageResponse response) {
                        userRepository.saveFingerprints(fields.getUserId(), fields.getTemplate1(),
                                fields.getTemplate2(), fields.getTemplate3(), fields.getTemplate4(),
                                fields.getDeviceId(), fields.getDeviceToken());
                        registrationLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        registrationLiveData.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        registrationLiveData.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
