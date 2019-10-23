package org.sdfw.biometric.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.sdfw.biometric.form.LoanDisbursementListForm;
import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.network.ObserverWithErrorWrapper;
import org.sdfw.biometric.network.OnResponseErrorListener;
import org.sdfw.biometric.network.ShaktiWebservice;
import org.sdfw.biometric.network.response.GetLoanDisbursementListResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.ResponseWrapper;
import org.sdfw.biometric.repository.LoanDisbursementRepository;
import org.sdfw.biometric.repository.UserRepository;
import org.sdfw.biometric.util.SessionManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static org.sdfw.biometric.util.Constant.TAG;

public class LoanDisbursementViewModel extends ViewModel implements OnResponseErrorListener {

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

    private LoanDisbursementListForm loanDisbursementListForm;

    private SingleLiveEvent<Boolean> findUserLiveEvent;
    private SingleLiveEvent<ResponseWrapper> loanDisbursementsLiveEvent;

    @Inject
    public LoanDisbursementViewModel() {
    }

    @VisibleForTesting
    public void init() {
        compositeDisposable = new CompositeDisposable();
        loanDisbursementListForm = new LoanDisbursementListForm();
        findUserLiveEvent = new SingleLiveEvent<>();
        loanDisbursementsLiveEvent = new SingleLiveEvent<>();
    }

    public LoanDisbursementListForm getLoanDisbursementListForm() {
        return loanDisbursementListForm;
    }

    public void findUser() {
        compositeDisposable.clear();
        compositeDisposable.add(userRepository.findUser(sessionManager.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(user -> {
                    Log.d(TAG, "findUser: received");
                    loanDisbursementListForm.setBasicFields(user);
                    findUserLiveEvent.postValue(true);
                })
        );
    }

    public void fetchLoanDisbursementsList() {
        Log.d(TAG, "fetchLoanDisbursementsMatrix: called day_opening: " + sessionManager.getDayOpening() + " branch_id: " + loanDisbursementListForm.getFields().getBranchId());
        if (sessionManager.getAccessToken(null) != null) {
            compositeDisposable.clear();
            compositeDisposable.add(loanDisbursementRepository.getAll(loanDisbursementListForm.getFields().getBranchId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(loanDisbursements -> {
                                Log.d(TAG, "fetchLoanDisbursementsMatrix: " + loanDisbursements.size());
                                if (loanDisbursements.size() > 0) {
                                    String openingDate = sessionManager.getDayOpening();
                                    loanDisbursementListForm.setLoanDisbursements(loanDisbursements);
                                    loanDisbursementListForm.setOpeningDate(openingDate);
                                    GetLoanDisbursementListResponse response = new GetLoanDisbursementListResponse();
                                    response.setSuccess(true);
                                    response.setLoanDisbursements(loanDisbursements);
                                    response.setDayOpening(openingDate);
                                    loanDisbursementsLiveEvent.postValue(new ResponseWrapper(response));
                                }
                            },
                            throwable -> {
                                MessageResponse response = new MessageResponse();
                                response.setSuccess(false);
                                response.setMessage("Could not fetch Loan Disbursements");
                                loanDisbursementsLiveEvent.postValue(new ResponseWrapper(response));
                            })
            );
            remoteFetchLoanDisbursementsList();
        } else {
            MessageResponse response = new MessageResponse();
            response.setSuccess(false);
            response.setMessage("Invalid access token");
            loanDisbursementsLiveEvent.postValue(new ResponseWrapper(401, response));
        }
    }

    private void remoteFetchLoanDisbursementsList() {
        Log.d(TAG, "remoteFetchLoanDisbursementsList: called");
        compositeDisposable.add(webservice.getLoanDisbursementList(loanDisbursementListForm.getFields().getUserId(),
                loanDisbursementListForm.getFields().getBranchId(),
                sessionManager.getAccessToken())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .delaySubscription(200, TimeUnit.MILLISECONDS)
                .subscribeWith(new ObserverWithErrorWrapper<GetLoanDisbursementListResponse>(this, converter,
                        loanDisbursementRepository.hashCode()) {

                    @Override
                    protected void onSuccess(GetLoanDisbursementListResponse response) {
                        Log.d(TAG, "onSuccess: ");
                        sessionManager.setDayOpening(response.getDayOpening());
                        loanDisbursementRepository.deleteAll(loanDisbursementListForm.getFields().getBranchId());
                        loanDisbursementRepository.saveAll(response.getLoanDisbursements());
                    }
                })
        );
    }

    public void reloadLoanDisbursementAdapter() {
        GetLoanDisbursementListResponse response = new GetLoanDisbursementListResponse();
        response.setSuccess(true);
        response.setLoanDisbursements(loanDisbursementListForm.getFields().getLoanDisbursements());
        loanDisbursementsLiveEvent.postValue(new ResponseWrapper(response));
    }

    public void clearLoanDisbursements() {
        loanDisbursementListForm.clearLoanDisbursements();
        loanDisbursementListForm.setOpeningDate(null);
    }

    public SingleLiveEvent<Boolean> getFindUserStatus() {
        return findUserLiveEvent;
    }

    public SingleLiveEvent<ResponseWrapper> getLoanDisbursementsStatus() {
        return loanDisbursementsLiveEvent;
    }


    @Override
    public void onHttpError(int hashCode, int responseCode, MessageResponse response) {
        Log.d(TAG, "onHttpError: " + responseCode);
        if (responseCode == 400) {
            loanDisbursementRepository.deleteAll(loanDisbursementListForm.getFields().getBranchId());
        }
        loanDisbursementsLiveEvent.postValue(new ResponseWrapper(responseCode, response));
    }

    @Override
    public void onError(int hashCode, MessageResponse response) {
        loanDisbursementsLiveEvent.postValue(new ResponseWrapper(response));
    }

    @Override
    public void onCleared() {
        // prevents memory leaks by disposing pending observable objects
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }
}
