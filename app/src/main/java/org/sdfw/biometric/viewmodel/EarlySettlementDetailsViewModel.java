package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.EarlySettlementForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.form.field.EarlySettlementFields;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.BaseResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.EarlySettlementRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class EarlySettlementDetailsViewModel extends ViewModel implements OnResponseErrorListener {

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

    private EarlySettlementForm earlySettlementForm;

    private SingleLiveEvent<ResponseWrapper> sendEarlySettlementLiveData;
    private SingleLiveEvent<ResponseWrapper> fetchEarlySettlementLiveData;
    private SingleLiveEvent<ResponseWrapper> verifyFingerPrintLiveData;

    @Inject
    public EarlySettlementDetailsViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        earlySettlementForm = new EarlySettlementForm();
        sendEarlySettlementLiveData = new SingleLiveEvent<>();
        fetchEarlySettlementLiveData = new SingleLiveEvent<>();
        verifyFingerPrintLiveData = new SingleLiveEvent<>();
    }

    public EarlySettlementForm getEarlySettlementForm() {
        return earlySettlementForm;
    }

    public void onSendServerClick() {
        earlySettlementForm.onClick();
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return earlySettlementForm.getValidationStatus();
    }

    public void verifyFingerPrintData(String branchId, String centerId, String memberId, String template) {
        compositeDisposable.add(webservice.verifyFingerprint(sessionManager.getUserId(), branchId,
                centerId, memberId, template, sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter, verifyFingerPrintLiveData.hashCode()) {
                    @Override
                    protected void onSuccess(MessageResponse response) {
                        verifyFingerPrintLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    public void sendEarlySettlementData() {
        EarlySettlementFields fields = earlySettlementForm.getFields();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sessionManager.getUserId());
        params.put("day_opening", fields.getDayOpening());
        params.put("branch_id", fields.getBranchId());
        params.put("center_id", fields.getCenterId());
        params.put("member_id", fields.getMemberId());
        params.put("member_name", fields.getMemberName());
        params.put("spouse_name", fields.getSpouseName());
        params.put("voter_id", fields.getVoterId() != null ? fields.getVoterId() : "");
        params.put("mobile_no", fields.getMobileNo() != null ? fields.getMobileNo() : "");
        params.put("received_amount", String.valueOf(fields.getReceivedAmount()));
        params.put("access_token", sessionManager.getAccessToken());
        compositeDisposable.add(webservice.postEarlySettlement(params)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter, sendEarlySettlementLiveData.hashCode()) {
                    @Override
                    protected void onSuccess(MessageResponse response) {
                        earlySettlementRepository.updateSyncStatus(fields.getDayOpening(),
                                fields.getBranchId(), fields.getCenterId(), fields.getMemberId(), "Y");
                        sendEarlySettlementLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }


    public void fetchEarlySettlement(String branchId, String centerId, String memberId) {
        Log.d(TAG, "fetchEarlySettlement: called");
        compositeDisposable.add(earlySettlementRepository.findEarlySettlement(sessionManager.getDayOpening(), branchId, centerId, memberId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(earlySettlement -> {
                            EarlySettlementFields fields = earlySettlementForm.getFields();
                            fields.setDayOpening(earlySettlement.getDayOpening());
                            fields.setBranchId(earlySettlement.getBranchId());
                            fields.setCenterId(earlySettlement.getCenterId());
                            fields.setMemberId(earlySettlement.getMemberId());
                            fields.setMemberName(earlySettlement.getMemberName());
                            fields.setSpouseName(earlySettlement.getSpouseName());
                            fields.setVoterId(earlySettlement.getVoterId());
                            fields.setMobileNo(earlySettlement.getMobileNo());
                            fields.setReceivedAmount(earlySettlement.getReceivedAmount());
                            fields.setIsSynced(earlySettlement.getIsSynced());
                            fields.setImage(earlySettlement.getImage());

                            BaseResponse response = new BaseResponse();
                            response.setSuccess(true);
                            fetchEarlySettlementLiveData.postValue(new ResponseWrapper(response));

                        },
                        throwable -> {
                            MessageResponse response = new MessageResponse();
                            response.setSuccess(false);
                            response.setMessage("Could not fetch fetchEarlySettlement");
                            fetchEarlySettlementLiveData.postValue(new ResponseWrapper(response));
                        })
        );
    }

    public SingleLiveEvent<ResponseWrapper> getEarlySettlementStatus() {
        return sendEarlySettlementLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> fetchEarlySettlementStatus() {
        return fetchEarlySettlementLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> getVerifyFingerPrintStatus() {
        return verifyFingerPrintLiveData;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(responseCode, response));
        } else {
            sendEarlySettlementLiveData.postValue(new ResponseWrapper(responseCode, response));
        }
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(response));
        } else {
            sendEarlySettlementLiveData.postValue(new ResponseWrapper(response));
        }
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
