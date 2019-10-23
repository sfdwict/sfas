package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import org.sdfw.biometric.form.MatrixForm;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.model.Member;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.BaseResponse;
import org.sdfw.biometric.network.response.FetchCentersResponse;
import org.sdfw.biometric.network.response.FetchMemberMatrixResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.CenterRepository;
import org.sdfw.biometric.repository.MemberRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class MemberMatrixViewModel extends ViewModel implements OnResponseErrorListener {

    @Inject
    SessionManager sessionManager;

    @Inject
    UserRepository userRepository;

    @Inject
    CenterRepository centerRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    ShaktiWebservice webservice;

    @Inject
    Converter<ResponseBody, MessageResponse> converter;

    @Inject
    Executor executor;

    private CompositeDisposable compositeDisposable;

    private MatrixForm matrix;

    private SingleLiveEvent<Boolean> findUserLiveEvent;
    private SingleLiveEvent<ResponseWrapper> centerLiveEvent;
    private SingleLiveEvent<ResponseWrapper> memberMatrixLiveEvent;

    @Inject
    public MemberMatrixViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        matrix = new MatrixForm();
        findUserLiveEvent = new SingleLiveEvent<>();
        centerLiveEvent = new SingleLiveEvent<>();
        memberMatrixLiveEvent = new SingleLiveEvent<>();
    }

    public MatrixForm getMatrix() {
        return matrix;
    }

    public void findUser() {
        compositeDisposable.clear();
        compositeDisposable.add(userRepository.findUser(sessionManager.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                    Log.d(TAG, "findUser: received");
                    matrix.setBasicFields(user);
                    findUserLiveEvent.postValue(true);
                })
        );
    }

    public void reloadCenterAdapter() {
        Log.d(TAG, "reloadCenterAdapter: called");
        FetchCentersResponse response = new FetchCentersResponse();
        response.setSuccess(true);
        response.setCenters(matrix.getFields().getCenters());
        centerLiveEvent.postValue(new ResponseWrapper(response));
    }

    public void fetchCenters() {
        Log.d(TAG, "fetchCenters: called");
        if (sessionManager.getAccessToken(null) != null) {
            compositeDisposable.clear();
            compositeDisposable.add(centerRepository.getAll(matrix.getFields().getBranchId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(centers -> {
                                Log.d(TAG, "fetchCenters: " + centers.size());
                                if (centers.size() > 0) {
                                    Log.d(TAG, "fetchCenters: size > 0");
                                    matrix.getFields().setCenters(centers);
                                    FetchCentersResponse response = new FetchCentersResponse();
                                    response.setSuccess(true);
                                    response.setCenters(centers);
                                    Log.d(TAG, "fetchCenters: " + response.isSuccess());
                                    centerLiveEvent.postValue(new ResponseWrapper(response));
                                }
                            },
                            throwable -> {
                                Log.d(TAG, "fetchCenters: error");
                                MessageResponse response = new MessageResponse();
                                response.setSuccess(false);
                                response.setMessage("Could not fetch centers");
                                centerLiveEvent.postValue(new ResponseWrapper(response));
                            })
            );
            remoteFetchCenters();
        } else {
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("Invalid access token");
            centerLiveEvent.postValue(new ResponseWrapper(401, response));
        }
    }

    private void remoteFetchCenters() {
        compositeDisposable.add(webservice.fetchCenters(sessionManager.getUserId(), matrix.getFields().getBranchId(), sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .delaySubscription(500, TimeUnit.MILLISECONDS)
                .subscribeWith(new ObserverWithErrorWrapper<FetchCentersResponse>(this, converter,
                        centerRepository.hashCode()) {

                    @Override
                    protected void onSuccess(FetchCentersResponse response) {
                        Log.d(TAG, "onSuccess: saving centers " + response.getCenters().size());
                        centerRepository.saveAll(response.getCenters());
                    }
                })
        );
    }

    public void clearMemberMatrix() {
        matrix.clearMembers();
    }

    public void fetchMemberMatrix() {
        Log.d(TAG, "fetchMemberMatrix: called");
        if (sessionManager.getAccessToken(null) != null) {
            if (matrix.getFields().getCenterId() != null) {
                compositeDisposable.clear();
                compositeDisposable.add(memberRepository.getAll(matrix.getFields().getBranchId(),
                        matrix.getFields().getCenterId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(members -> {
                                    Log.d(TAG, "fetchMemberMatrix: " + members.size());
                                    if (members.size() > 0) {
                                        matrix.setMembers(members);
                                        BaseResponse response = new BaseResponse();
                                        response.setSuccess(true);
                                        memberMatrixLiveEvent.postValue(new ResponseWrapper(response));
                                    } else {
                                        remoteFetchMemberMatrix();
                                    }
                                },
                                throwable -> {
                                    MessageResponse response = new MessageResponse();
                                    response.setSuccess(false);
                                    response.setMessage("Could not fetch members");
                                    memberMatrixLiveEvent.postValue(new ResponseWrapper(response));
                                })
                );
            } else {
                MessageResponse response = new MessageResponse();
                response.setSuccess(false);
                response.setMessage("Center not selected");
                memberMatrixLiveEvent.postValue(new ResponseWrapper(response));
            }
        } else {
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("Invalid access token");
            memberMatrixLiveEvent.postValue(new ResponseWrapper(401, response));
        }
    }

    private void remoteFetchMemberMatrix() {
        Log.d(TAG, "remoteFetchMemberMatrix: called");
        compositeDisposable.add(webservice.fetchMemberMatrix(matrix.getFields().getUserId(),
                matrix.getFields().getBranchId(), matrix.getFields().getCenterId(), sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ObserverWithErrorWrapper<FetchMemberMatrixResponse>(this, converter,
                        memberRepository.hashCode()) {

                    @Override
                    protected void onSuccess(FetchMemberMatrixResponse response) {
                        List<Member> members = response.getMembers();
                        Log.d(TAG, "onSuccess: members: " + members.size());
                        for (Member member : members) {
                            member.setNewMember(member.getIsBlank().equals("Y"));
                            member.setHasFingerprint(member.getBiometricFlag() != null
                                    && member.getBiometricFlag().equals("Y"));
                            member.setHasImage(member.getImage() != null
                                    && !TextUtils.isEmpty(member.getImage()));
                            member.setHasVoterId(member.getVoterId() != null
                                    && !TextUtils.isEmpty(member.getVoterId()));
                        }
                        memberRepository.saveAll(response.getMembers());
//                        int count = memberRepository.getCount(matrix.getFields().getBranchId(), matrix.getFields().getCenterId());
//                        Log.d(TAG, "onSuccess: save count: " + count);
                    }
                })
        );
    }

    public void deleteAllData() {
        executor.execute(() -> {
            centerRepository.deleteAll(matrix.getFields().getBranchId());
            memberRepository.deleteAllByBranch(matrix.getFields().getBranchId());
        });
    }

    public SingleLiveEvent<Boolean> getFindUserStatus() {
        return findUserLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getFetchCentersStatus() {
        return centerLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getMemberMatrixStatus() {
        return memberMatrixLiveEvent;
    }

    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        Log.d(TAG, "onHttpError: " + responseCode);
        if (hashCode == centerRepository.hashCode()) {
            if (responseCode == 400) {
                deleteAllData();
            }
            centerLiveEvent.postValue(new ResponseWrapper(responseCode, response));
        } else {
            memberMatrixLiveEvent.postValue(new ResponseWrapper(responseCode, response));
        }
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        Log.d(TAG, "onError: ");
        if (hashCode == centerRepository.hashCode()) {
            centerLiveEvent.postValue(new ResponseWrapper(response));
        } else {
            memberMatrixLiveEvent.postValue(new ResponseWrapper(response));
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
