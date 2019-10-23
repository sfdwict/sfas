package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.MemberDetailsForm;
import org.sdfw.biometric.form.ValidationStatus;
import org.sdfw.biometric.form.field.MemberFields;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.BaseResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.MemberRepository;
import org.sdfw.biometric.util.SessionManager;
import org.sdfw.biometric.worker.CreateMemberWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class MemberDetailsViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    MemberRepository memberRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    private CompositeDisposable compositeDisposable;

    private MemberDetailsForm memberDetails;

    private SingleLiveEvent<ResponseWrapper> fetchMemberLiveEvent;
    private SingleLiveEvent<ResponseWrapper> createMemberLiveEvent;

    @Inject
    public MemberDetailsViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        memberDetails = new MemberDetailsForm();
        fetchMemberLiveEvent = new SingleLiveEvent<>();
        createMemberLiveEvent = new SingleLiveEvent<>();
    }

    public MemberDetailsForm getMemberDetails() {
        return memberDetails;
    }

    public SingleLiveEvent<ValidationStatus> getValidationStatus() {
        return memberDetails.getValidationStatus();
    }

    public void fetchMember(String branchId, String centerId, String memberId) {
        if (sessionManager.getAccessToken(null) != null) {
            compositeDisposable.clear();
            compositeDisposable.add(memberRepository.findMember(branchId, centerId, memberId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(member -> {
                                Log.d(TAG, "fetchMember: " + member.getMemberId());
                                MemberFields fields = memberDetails.getFields();
                                fields.setId(member.getId());
                                fields.setBranchId(member.getBranchId());
                                fields.setCenterId(member.getCenterId());
                                fields.setMemberId(member.getMemberId());
                                fields.setGroupId(member.getMemberId().substring(0, 1));
                                fields.setMemberName(member.getMemberName());
                                fields.setSaversOnlyFlag(member.getSaversOnlyFlag());
                                fields.setBiometricFlag(member.getBiometricFlag());
                                fields.setIsBlank(member.getIsBlank());
                                fields.setFatherName(member.getFatherName());
                                fields.setMotherName(member.getMotherName());
                                fields.setSpouseName(member.getSpouseName());
                                fields.setAddress(member.getAddress());
                                fields.setSuccessor(member.getSuccessor());
                                fields.setVoterId(member.getVoterId());
                                fields.setMobileNo(member.getMobileNo());
                                fields.setVerificationStatus(member.getVerificationStatus());
                                fields.setImage(member.getImage());
                                fields.setTemplate1(member.getTemplate1());
                                fields.setTemplate2(member.getTemplate2());
                                fields.setTemplate3(member.getTemplate3());
                                fields.setTemplate4(member.getTemplate4());
//                                fields.setTemplate1("Rk1SACAyMAAAAAFEAAABLAGxAMUAxQEAAABkMYDVACEXAECMADAcAIBEAEMgAEBwAEWkAECjAEgaAICuAEoUAEBmAFogAIDKAFwRAEDqAGEMAIC1AG%2BVAIAkAHAaAEEIAHH8AEDyAHX8AECQAIIXAICrAIQVAID7AIr1AIDEAI0FAIB4AJGcAIBdAKAdAEDoAKDwAICpAKEVAICsAK4OAECFALsZAEDjAL%2FwAECBAMSkAIBXAMWjAEDYAMjyAICUANYaAID%2BANZrAEBJAOYkAECwAOr%2BAECWAOsaAIBSAPikAEDbAQTmAEBUAQUpAICXARAcAIClARUEAECjASf%2BAIBtASilAEEJAS9eAEA6ATCfAEDjATDcAICBATMnAEEKAUPVAECYAUsTAICgAVL%2BAIC4AVdwAECHAV6aAIB4AWkfAAAA");
//                                fields.setTemplate2("Rk1SACAyMAAAAAFEAAABLAGxAMUAxQEAAABkMYDVACEXAECMADAcAIBEAEMgAEBwAEWkAECjAEgaAICuAEoUAEBmAFogAIDKAFwRAEDqAGEMAIC1AG%2BVAIAkAHAaAEEIAHH8AEDyAHX8AECQAIIXAICrAIQVAID7AIr1AIDEAI0FAIB4AJGcAIBdAKAdAEDoAKDwAICpAKEVAICsAK4OAECFALsZAEDjAL%2FwAECBAMSkAIBXAMWjAEDYAMjyAICUANYaAID%2BANZrAEBJAOYkAECwAOr%2BAECWAOsaAIBSAPikAEDbAQTmAEBUAQUpAICXARAcAIClARUEAECjASf%2BAIBtASilAEEJAS9eAEA6ATCfAEDjATDcAICBATMnAEEKAUPVAECYAUsTAICgAVL%2BAIC4AVdwAECHAV6aAIB4AWkfAAAA");
//                                fields.setTemplate3("Rk1SACAyMAAAAAFEAAABLAGxAMUAxQEAAABkMYDVACEXAECMADAcAIBEAEMgAEBwAEWkAECjAEgaAICuAEoUAEBmAFogAIDKAFwRAEDqAGEMAIC1AG%2BVAIAkAHAaAEEIAHH8AEDyAHX8AECQAIIXAICrAIQVAID7AIr1AIDEAI0FAIB4AJGcAIBdAKAdAEDoAKDwAICpAKEVAICsAK4OAECFALsZAEDjAL%2FwAECBAMSkAIBXAMWjAEDYAMjyAICUANYaAID%2BANZrAEBJAOYkAECwAOr%2BAECWAOsaAIBSAPikAEDbAQTmAEBUAQUpAICXARAcAIClARUEAECjASf%2BAIBtASilAEEJAS9eAEA6ATCfAEDjATDcAICBATMnAEEKAUPVAECYAUsTAICgAVL%2BAIC4AVdwAECHAV6aAIB4AWkfAAAA");
//                                fields.setTemplate4("Rk1SACAyMAAAAAFEAAABLAGxAMUAxQEAAABkMYDVACEXAECMADAcAIBEAEMgAEBwAEWkAECjAEgaAICuAEoUAEBmAFogAIDKAFwRAEDqAGEMAIC1AG%2BVAIAkAHAaAEEIAHH8AEDyAHX8AECQAIIXAICrAIQVAID7AIr1AIDEAI0FAIB4AJGcAIBdAKAdAEDoAKDwAICpAKEVAICsAK4OAECFALsZAEDjAL%2FwAECBAMSkAIBXAMWjAEDYAMjyAICUANYaAID%2BANZrAEBJAOYkAECwAOr%2BAECWAOsaAIBSAPikAEDbAQTmAEBUAQUpAICXARAcAIClARUEAECjASf%2BAIBtASilAEEJAS9eAEA6ATCfAEDjATDcAICBATMnAEEKAUPVAECYAUsTAICgAVL%2BAIC4AVdwAECHAV6aAIB4AWkfAAAA");
                                fields.setNewMember(member.isNewMember());
                                fields.setHasFingerprint(member.isHasFingerprint());
                                fields.setHasImage(member.isHasImage());
                                fields.setHasVoterId(member.isHasVoterId());
                                memberDetails.setDateOfBirthForm(member.getDateOfBirth());
                                memberDetails.setSex(member.getSex());
                                memberDetails.setMarital(member.getMaritalStatus());
                                BaseResponse response = new BaseResponse();
                                response.setSuccess(true);
                                fetchMemberLiveEvent.postValue(new ResponseWrapper(response));
                            },
                            throwable -> {
                                MessageResponse response = new MessageResponse();
                                response.setSuccess(false);
                                response.setMessage("Could not fetch members");
                                fetchMemberLiveEvent.postValue(new ResponseWrapper(response));
                            })
            );
        }
    }

    public void createMember() {
        Log.d(TAG, "createMember: called");
        MemberFields fields = memberDetails.getFields();
        if (fields.getVerificationStatus() == null) {
            Member member = new Member();
            member.setId(fields.getId());
            member.setBranchId(fields.getBranchId());
            member.setCenterId(fields.getCenterId());
            member.setMemberId(fields.getMemberId());
            member.setMemberName(fields.getMemberName());
            member.setSaversOnlyFlag(fields.getSaversOnlyFlag());
            member.setBiometricFlag("Y");
            member.setIsBlank("N");
            member.setFatherName(fields.getFatherName());
            member.setMotherName(fields.getMotherName());
            member.setSpouseName(fields.getSpouseName());
            member.setSex(fields.getSex());
            member.setMaritalStatus(fields.getMaritalStatus());
            member.setAddress(fields.getAddress());
            member.setSuccessor(fields.getSuccessor());
            member.setDateOfBirth(fields.getDateOfBirth());
            member.setVoterId(fields.getVoterId());
            member.setMobileNo(fields.getMobileNo());
            member.setImage(fields.getImage());
            member.setTemplate1(fields.getTemplate1());
            member.setTemplate2(fields.getTemplate2());
            member.setTemplate3(fields.getTemplate3());
            member.setTemplate4(fields.getTemplate4());
            member.setVerificationStatus("P");
            member.setNewMember(false);
            member.setHasFingerprint(true);
            member.setHasImage(true);
            member.setHasVoterId(true);
            Completable.fromAction(() -> memberRepository.update(member))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::remoteCreateMember)
                    .subscribe();
        } else {
            remoteCreateMember();
        }
    }

    private void updateVerificationStatus(String status) {
        Log.d(TAG, "updateVerificationStatus: called");
        MemberFields fields = memberDetails.getFields();
        Completable.fromAction(() -> memberRepository.updateVerificationStatus(fields.getBranchId(),
                fields.getCenterId(), fields.getMemberId(), status))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void remoteCreateMember() {
        Log.d(TAG, "remoteCreateMember: called");
        Map<String, String> params = generateParams();
        compositeDisposable.add(webservice.createMember(params)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<MessageResponse>(this, converter,
                        memberRepository.hashCode()) {

                    @Override
                    protected void onSuccess(MessageResponse response) {
                        Log.d(TAG, "onSuccess: ");
                        updateVerificationStatus("V");
                        createMemberLiveEvent.postValue(new ResponseWrapper(response));
                    }
                })
        );
    }

    private Map<String, String> generateParams() {
        MemberFields fields = memberDetails.getFields();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sessionManager.getUserId());
        params.put("branch_id", fields.getBranchId());
        params.put("center_id", fields.getCenterId());
        params.put("member_id", fields.getMemberId());
        params.put("group_id", fields.getMemberId().substring(0, 1));
        params.put("member_name", fields.getMemberName());
        params.put("savers_only_flag", fields.getSaversOnlyFlag());
        params.put("father_name", fields.getFatherName());
        params.put("mother_name", fields.getMotherName());
        params.put("spouse_name", fields.getSpouseName());
        params.put("sex", fields.getSex());
        params.put("marital_status", fields.getMaritalStatus());
        params.put("address", fields.getAddress());
        params.put("successor", fields.getSuccessor());
        params.put("dob", fields.getDateOfBirth());
        params.put("voter_id", fields.getVoterId());
        params.put("mobile", fields.getMobileNo());
        params.put("template1", fields.getTemplate1());
        params.put("template2", fields.getTemplate2());
        params.put("template3", fields.getTemplate3());
        params.put("template4", fields.getTemplate4());
        params.put("member_image", fields.getImage());
        params.put("varification_status", "P");
        params.put("create_uid", sessionManager.getUserId());
        params.put("access_token", sessionManager.getAccessToken());
        return params;
    }

    private void enqueueBackgroundJob() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(false)
                .build();
        MemberFields fields = memberDetails.getFields();
        Data data = new Data.Builder()
                .putString("user_id", sessionManager.getUserId())
                .putString("branch_id", fields.getBranchId())
                .putString("center_id", fields.getCenterId())
                .putString("member_id", fields.getMemberId())
                .putString("create_uid", sessionManager.getUserId())
                .putString("access_token", sessionManager.getAccessToken())
                .build();
        OneTimeWorkRequest createMember = new OneTimeWorkRequest.Builder(CreateMemberWorker.class)
                .setConstraints(constraints)
                .addTag(fields.getBranchId()
                        .concat(fields.getCenterId())
                        .concat(fields.getMemberId()))
                .setInputData(data)
                .setInitialDelay(3, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueueUniqueWork("create_member", ExistingWorkPolicy.APPEND, createMember);
    }

    public void onValidate() {
        memberDetails.onClick();
    }

    public SingleLiveEvent<ResponseWrapper> getFetchMemberStatus() {
        return fetchMemberLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getCreateMemberStatus() {
        return createMemberLiveEvent;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        Log.d(TAG, "onHttpError: " + responseCode);
        if (responseCode == 409) {
            updateVerificationStatus(response.getStatus());
        }
        createMemberLiveEvent.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        Log.d(TAG, "onError: enqueuing new work");
        enqueueBackgroundJob();
        createMemberLiveEvent.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
