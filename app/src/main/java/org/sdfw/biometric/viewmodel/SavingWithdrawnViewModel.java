package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.SavingWithdrawnListForm;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.GetSavingWithdrawnListResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.SavingWithdrawnRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class SavingWithdrawnViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    SavingWithdrawnRepository savingWithdrawnRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private SavingWithdrawnListForm savingWithdrawnListForm;

    private SingleLiveEvent<Boolean> findUserLiveEvent;
    private SingleLiveEvent<ResponseWrapper> savingWithdrawnLiveEvent;


    @Inject
    public SavingWithdrawnViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        savingWithdrawnListForm = new SavingWithdrawnListForm();
        findUserLiveEvent = new SingleLiveEvent<>();
        savingWithdrawnLiveEvent = new SingleLiveEvent<>();
    }

    public SavingWithdrawnListForm getSavingWithdrawnListForm() {
        return savingWithdrawnListForm;
    }

    public void findUser() {
        compositeDisposable.clear();
        compositeDisposable.add(userRepository.findUser(sessionManager.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                    Log.d(TAG, "findUser: received");
                    savingWithdrawnListForm.setBasicFields(user);
                    findUserLiveEvent.postValue(true);
                })
        );
    }

    public void fetchSavingWithdrawnList() {
        Log.d(TAG, "fetchSavingWithdrawnList: called");
        if (sessionManager.getAccessToken(null) != null) {
            compositeDisposable.clear();
            compositeDisposable.add(savingWithdrawnRepository.getAll(savingWithdrawnListForm.getFields().getBranchId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(savingWithdrawns -> {
                                Log.d(TAG, "fetch Saving Withdrawn: " + savingWithdrawns.size());
                                if (savingWithdrawns.size() > 0) {
                                    String openingDate = sessionManager.getDayOpening();
                                    savingWithdrawnListForm.setSavingWithdrawns(savingWithdrawns);
                                    savingWithdrawnListForm.setOpeningDate(openingDate);
                                    GetSavingWithdrawnListResponse response = new GetSavingWithdrawnListResponse();
                                    response.setSuccess(true);
                                    response.setSavingWithdrawns(savingWithdrawns);
                                    response.setDayOpening(openingDate);
                                    savingWithdrawnLiveEvent.postValue(new ResponseWrapper(response));
                                }
                            },
                            throwable -> {
                                MessageResponse response = new MessageResponse();
                                response.setSuccess(false);
                                response.setMessage("Could not fetch Saving Withdrawn");
                                savingWithdrawnLiveEvent.postValue(new ResponseWrapper(response));
                            })
            );
            remoteFetchSavingWithdrawnList();
        } else {
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("Invalid access token");
            savingWithdrawnLiveEvent.postValue(new ResponseWrapper(401, response));
        }
    }

    private void remoteFetchSavingWithdrawnList() {
        Log.d(TAG, "remoteFetchSavingWithdrawnList: called");
        compositeDisposable.add(webservice.getSavingWithdrawnList(savingWithdrawnListForm.getFields().getUserId(),
                savingWithdrawnListForm.getFields().getBranchId(), sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .delaySubscription(200, TimeUnit.MILLISECONDS)
                .subscribeWith(new ObserverWithErrorWrapper<GetSavingWithdrawnListResponse>(this, converter,
                        savingWithdrawnRepository.hashCode()) {

                    @Override
                    protected void onSuccess(GetSavingWithdrawnListResponse response) {
                        sessionManager.setDayOpening(response.getDayOpening());
                        savingWithdrawnRepository.deleteAll(savingWithdrawnListForm.getFields().getBranchId());
                        savingWithdrawnRepository.saveAll(response.getSavingWithdrawns());
                    }
                })
        );
    }

    public void reloadSavingWithdrawnListAdapter() {
        GetSavingWithdrawnListResponse response = new GetSavingWithdrawnListResponse();
        response.setSuccess(true);
        response.setSavingWithdrawns(savingWithdrawnListForm.getFields().getSavingWithdrawns());
        savingWithdrawnLiveEvent.postValue(new ResponseWrapper(response));
    }

    public void clearSavingWithdrawns() {
        savingWithdrawnListForm.clearSavingWithdrawns();
        savingWithdrawnListForm.setOpeningDate(null);
    }

    public SingleLiveEvent<Boolean> getFindUserStatus() {
        return findUserLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getSavingWithdrawnListStatus() {
        return savingWithdrawnLiveEvent;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        Log.d(TAG, "onHttpError: " + responseCode);
        if (responseCode == 400) {
            savingWithdrawnRepository.deleteAll(savingWithdrawnListForm.getFields().getBranchId());
        }
        savingWithdrawnLiveEvent.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        savingWithdrawnLiveEvent.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
