package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.EarlySettlementListForm;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.GetEarlySettlementListResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.EarlySettlementRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class EarlySettlementViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    EarlySettlementRepository earlySettlementRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private EarlySettlementListForm earlySettlementListForm;

    private SingleLiveEvent<Boolean> findUserLiveEvent;
    private SingleLiveEvent<ResponseWrapper> earlySettlementLiveEvent;


    @Inject
    public EarlySettlementViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        earlySettlementListForm = new EarlySettlementListForm();
        findUserLiveEvent = new SingleLiveEvent<>();
        earlySettlementLiveEvent = new SingleLiveEvent<>();
    }

    public EarlySettlementListForm getEarlySettlementListForm() {
        return earlySettlementListForm;
    }

    public void findUser() {
        compositeDisposable.clear();
        compositeDisposable.add(userRepository.findUser(sessionManager.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                    Log.d(TAG, "findUser: received");
                    earlySettlementListForm.setBasicFields(user);
                    findUserLiveEvent.postValue(true);
                })
        );
    }

    public void fetchEarlySettlementList() {
        Log.d(TAG, "fetchEarlySettlementList: called");
        if (sessionManager.getAccessToken(null) != null) {
            compositeDisposable.clear();
            compositeDisposable.add(earlySettlementRepository.getAll(earlySettlementListForm.getFields().getBranchId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(earlySettlements -> {
                                Log.d(TAG, "fetchEarlySettlementList: " + earlySettlements.size());
                                if (earlySettlements.size() > 0) {
                                    String openingDate = sessionManager.getDayOpening();
                                    earlySettlementListForm.setEarlySettlements(earlySettlements);
                                    earlySettlementListForm.setOpeningDate(openingDate);
                                    GetEarlySettlementListResponse response = new GetEarlySettlementListResponse();
                                    response.setSuccess(true);
                                    response.setEarlySettlements(earlySettlements);
                                    response.setDayOpening(openingDate);
                                    earlySettlementLiveEvent.postValue(new ResponseWrapper(response));
                                }
                            },
                            throwable -> {
                                MessageResponse response = new MessageResponse();
                                response.setSuccess(false);
                                response.setMessage("Could not fetch Loan Disbursements");
                                earlySettlementLiveEvent.postValue(new ResponseWrapper(response));
                            })
            );
            remoteFetchEarlySettlementList();
        } else {
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("Invalid access token");
            earlySettlementLiveEvent.postValue(new ResponseWrapper(401, response));
        }
    }

    private void remoteFetchEarlySettlementList() {
        Log.d(TAG, "remoteFetchEarlySettlementList: called");
        compositeDisposable.add(webservice.getEarlySettlementList(earlySettlementListForm.getFields().getUserId(),
                earlySettlementListForm.getFields().getBranchId(),
                sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .delaySubscription(200, TimeUnit.MILLISECONDS)
                .subscribeWith(new ObserverWithErrorWrapper<GetEarlySettlementListResponse>(this, converter,
                        earlySettlementRepository.hashCode()) {

                    @Override
                    protected void onSuccess(GetEarlySettlementListResponse response) {
                        Log.d(TAG, "onSuccess: ");
                        sessionManager.setDayOpening(response.getDayOpening());
                        earlySettlementRepository.deleteAll(earlySettlementListForm.getFields().getBranchId());
                        earlySettlementRepository.saveAll(response.getEarlySettlements());
                    }
                })
        );
    }

    public void reloadEarlySettlementsAdapter() {
        GetEarlySettlementListResponse response = new GetEarlySettlementListResponse();
        response.setSuccess(true);
        response.setEarlySettlements(earlySettlementListForm.getFields().getEarlySettlements());
        earlySettlementLiveEvent.postValue(new ResponseWrapper(response));
    }

    public void clearEarlySettlements() {
        earlySettlementListForm.clearEarlySettlements();
        earlySettlementListForm.setOpeningDate(null);
    }

    public SingleLiveEvent<Boolean> getFindUserStatus() {
        return findUserLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getLoanDisbursementsStatus() {
        return earlySettlementLiveEvent;
    }


    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        Log.d(TAG, "onHttpError: " + responseCode);
        if (responseCode == 400) {
            earlySettlementRepository.deleteAll(earlySettlementListForm.getFields().getBranchId());
        }
        earlySettlementLiveEvent.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        earlySettlementLiveEvent.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
