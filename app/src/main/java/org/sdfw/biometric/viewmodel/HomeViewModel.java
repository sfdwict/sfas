package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.model.User;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class HomeViewModel extends ViewModel {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private SingleLiveEvent<User> findUserLiveEvent;

    @Inject
    public HomeViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        findUserLiveEvent = new SingleLiveEvent<>();
    }

    public void findUser() {
        compositeDisposable.clear();
        compositeDisposable.add(userRepository.findUser(sessionManager.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                    Log.d(TAG, "findUser: received");
                    findUserLiveEvent.postValue(user);
                })
        );
    }

    public SingleLiveEvent<User> getFindUserStatus() {
        return findUserLiveEvent;
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
