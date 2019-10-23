package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.LoanDisbursementForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.form.field.LoanDisbursementFields;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.BaseResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.LoanDisbursementRepository;
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

public class LoanDisbursementDetailsViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    LoanDisbursementRepository loanDisbursementRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private LoanDisbursementForm loanDisbursementForm;

    private SingleLiveEvent<ResponseWrapper> sendLoanDisbursementLiveData;
    private SingleLiveEvent<ResponseWrapper> fetchLoanDisbursementLiveData;
    private SingleLiveEvent<ResponseWrapper> verifyFingerPrintLiveData;

    @Inject
    public LoanDisbursementDetailsViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        loanDisbursementForm = new LoanDisbursementForm();
        sendLoanDisbursementLiveData = new SingleLiveEvent<>();
        fetchLoanDisbursementLiveData = new SingleLiveEvent<>();
        verifyFingerPrintLiveData = new SingleLiveEvent<>();
    }

    public LoanDisbursementForm getLoanDisbursementForm() {
        return loanDisbursementForm;
    }

    public void onSendServerClick() {
        loanDisbursementForm.onClick();
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return loanDisbursementForm.getValidationStatus();
    }

    public void verifyFingerPrintData(String branchId, String centerId, String memberId, String template) {
        compositeDisposable.add(webservice.verifyFingerprint(sessionManager.getUserId(), branchId, centerId,
                memberId, template, sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter,
                        verifyFingerPrintLiveData.hashCode()) {
                    @Override
                    protected void onSuccess(MessageResponse response) {
                        verifyFingerPrintLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    public void sendLoanDisbursementData() {
        LoanDisbursementFields fields = loanDisbursementForm.getFields();
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
        params.put("disbursement_amount", String.valueOf(fields.getDisbursementAmount()));
        params.put("passbook_no", fields.getPassbookNo());
        params.put("access_token", sessionManager.getAccessToken());
        compositeDisposable.add(webservice.postLoanDisbursement(params)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter,
                        sendLoanDisbursementLiveData.hashCode()) {
                    @Override
                    protected void onSuccess(MessageResponse response) {
                        loanDisbursementRepository.updateSyncStatus(fields.getDayOpening(),
                                fields.getBranchId(), fields.getCenterId(), fields.getMemberId(), "Y");
                        sendLoanDisbursementLiveData.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    public void fetchLoanDisbursement(String branchId, String centerId, String memberId) {
        Log.d(TAG, "fetchLoanDisbursement: called");
        compositeDisposable.add(loanDisbursementRepository.findLoanDisbursement(sessionManager.getDayOpening(),
                branchId, centerId, memberId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(loanDisbursement -> {
                            LoanDisbursementFields fields = loanDisbursementForm.getFields();
                            fields.setDayOpening(loanDisbursement.getDayOpening());
                            fields.setBranchId(loanDisbursement.getBranchId());
                            fields.setCenterId(loanDisbursement.getCenterId());
                            fields.setMemberId(loanDisbursement.getMemberId());
                            fields.setMemberName(loanDisbursement.getMemberName());
                            fields.setSpouseName(loanDisbursement.getSpouseName());
                            fields.setVoterId(loanDisbursement.getVoterId());
                            fields.setMobileNo(loanDisbursement.getMobileNo());
                            fields.setDisbursementAmount(loanDisbursement.getDisbursementAmount());
                            fields.setIsSynced(loanDisbursement.getIsSynced());
                            fields.setImage(loanDisbursement.getImage());
                            fields.setPassbookNo(loanDisbursement.getPassbookNo());

                            BaseResponse response = new BaseResponse();
                            response.setSuccess(true);
                            fetchLoanDisbursementLiveData.postValue(new ResponseWrapper(response));

                        },
                        throwable -> {
                            MessageResponse response = new MessageResponse();
                            response.setSuccess(false);
                            response.setMessage("Could not fetch Loan Disbursements");
                            fetchLoanDisbursementLiveData.postValue(new ResponseWrapper(response));
                        })
        );
    }


    public SingleLiveEvent<ResponseWrapper> getLoanDisbursementStatus() {
        return sendLoanDisbursementLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> fetchLoanDisbursementStatus() {
        return fetchLoanDisbursementLiveData;
    }

    public SingleLiveEvent<ResponseWrapper> getVerifyFingerPrintStatus() {
        return verifyFingerPrintLiveData;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(responseCode, response));
        } else {
            sendLoanDisbursementLiveData.postValue(new ResponseWrapper(responseCode, response));
        }
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        if (hashCode == verifyFingerPrintLiveData.hashCode()) {
            verifyFingerPrintLiveData.postValue(new ResponseWrapper(response));
        } else {
            sendLoanDisbursementLiveData.postValue(new ResponseWrapper(response));
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
