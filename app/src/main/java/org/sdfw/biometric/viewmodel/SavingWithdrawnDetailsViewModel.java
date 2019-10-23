package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.SavingWithdrawnForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.form.field.SavingWithdrawnFields;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.BaseResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.SavingWithdrawnRepository;
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

public class SavingWithdrawnDetailsViewModel extends ViewModel implements OnResponseErrorListener {

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

    private SavingWithdrawnForm savingWithdrawnForm;

    private SingleLiveEvent<ResponseWrapper> sendSavingWithdrawnLiveData;
    private SingleLiveEvent<ResponseWrapper> fetchSavingWithdrawnLiveData;
    private SingleLiveEvent<ResponseWrapper> verifyFingerPrintLiveData;

    @Inject
    public SavingWithdrawnDetailsViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        savingWithdrawnForm = new SavingWithdrawnForm();
        sendSavingWithdrawnLiveData = new SingleLiveEvent<>();
        fetchSavingWithdrawnLiveData = new SingleLiveEvent<>();
        verifyFingerPrintLiveData = new SingleLiveEvent<>();
    }

    public SavingWithdrawnForm getSavingWithdrawnForm() {
        return savingWithdrawnForm;
    }

    public void onSendServerClick() {
        savingWithdrawnForm.onClick();
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return savingWithdrawnForm.getValidationStatus();
    }


    public void verifyFingerPrintData(String branchId, String centerId, String memberId, String template) {
        compositeDisposable.add(webservice.verifyFingerprint(sessionManager.getUserId(), branchId, centerId,
                memberId, template, sessionManager.getAccessToken())
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

    public void sendSavingWithdrawnData() {
        SavingWithdrawnFields fields = savingWithdrawnForm.getFields();
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
        params.put("withdraw_type", fields.getWithdrawType());
        params.put("my_savings_amount", String.valueOf(fields.getMySavingsAmount()));
        params.put("family_savings_amount", String.valueOf(fields.getFamilySavingsAmount()));
        params.put("dps_amount", String.valueOf(fields.getDpsAmount()));
        params.put("lakhpati_amount", String.valueOf(fields.getLakhpatiAmount()));
        params.put("double_savings_amount", String.valueOf(fields.getDoubleSavingsAmount()));
        params.put("access_token", sessionManager.getAccessToken());
        compositeDisposable.add(webservice.postSavingWithdrawn(params)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter,
                        sendSavingWithdrawnLiveData.hashCode()) {
                    @Override
                    protected void onSuccess(MessageResponse response) {
                        savingWithdrawnRepository.updateSyncStatus(fields.getDayOpening(),
                                fields.getBranchId(), fields.getCenterId(), fields.getMemberId(), "Y");
                        sendSavingWithdrawnLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    public void fetchSavingWithdrawn(String branchId, String centerId, String memberId) {
        Log.d(TAG, "fetchSavingWithdrawn: called");
        compositeDisposable.clear();
        compositeDisposable.add(savingWithdrawnRepository.findSavingWithdrawn(sessionManager.getDayOpening(), branchId, centerId, memberId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(savingWithdrawn -> {
                            SavingWithdrawnFields fields = savingWithdrawnForm.getFields();
                            fields.setDayOpening(savingWithdrawn.getDayOpening());
                            fields.setBranchId(savingWithdrawn.getBranchId());
                            fields.setCenterId(savingWithdrawn.getCenterId());
                            fields.setMemberId(savingWithdrawn.getMemberId());
                            fields.setMemberName(savingWithdrawn.getMemberName());
                            fields.setSpouseName(savingWithdrawn.getSpouseName());
                            fields.setVoterId(savingWithdrawn.getVoterId());
                            fields.setMobileNo(savingWithdrawn.getMobileNo());
                            fields.setWithdrawType(savingWithdrawn.getWithdrawType());
                            fields.setMySavingsAmount(savingWithdrawn.getMySavingsAmount());
                            fields.setFamilySavingsAmount(savingWithdrawn.getFamilySavingsAmount());
                            fields.setDpsAmount(savingWithdrawn.getDpsAmount());
                            fields.setLakhpatiAmount(savingWithdrawn.getLakhpatiAmount());
                            fields.setDoubleSavingsAmount(savingWithdrawn.getDoubleSavingsAmount());
                            fields.setIsSynced(savingWithdrawn.getIsSynced());
                            fields.setImage(savingWithdrawn.getImage());

                            BaseResponse response = new BaseResponse();
                            response.setSuccess(true);
                            fetchSavingWithdrawnLiveData.postValue(new ResponseWrapper(response));

                        },
                        throwable -> {
                            MessageResponse response = new MessageResponse();
                            response.setSuccess(false);
                            response.setMessage("Could not fetch fetchEarlySettlement");
                            fetchSavingWithdrawnLiveData.postValue(new ResponseWrapper(response));
                        })
        );
    }

    public SingleLiveEvent<ResponseWrapper> getSavingWithdrawnStatus() {
        return sendSavingWithdrawnLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> fetchSavingWithdrawnStatus() {
        return fetchSavingWithdrawnLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> getVerifyFingerPrintStatus() {
        return verifyFingerPrintLiveData;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(responseCode, response));
        } else {
            sendSavingWithdrawnLiveData.postValue(new ResponseWrapper(responseCode, response));
        }
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(response));
        } else {
            sendSavingWithdrawnLiveData.postValue(new ResponseWrapper(response));
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
