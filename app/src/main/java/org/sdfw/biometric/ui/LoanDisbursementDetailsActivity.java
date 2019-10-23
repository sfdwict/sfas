package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivityLoanDisbursementDetailsBinding;
import org.sdfw.biometric.form.field.LoanDisbursementFields;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.LoanDisbursementDetailsViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_INTENT_BRANCH_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_CENTER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_MEMBER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class LoanDisbursementDetailsActivity extends FingerprintComponentActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityLoanDisbursementDetailsBinding mBinding;
    private LoanDisbursementDetailsViewModel mViewModel;

    private String mBranchId;
    private String mCenterId;
    private String mMemberId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DIALOG, isDialogShowing());
    }

    @Override
    public void setContentView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_loan_disbursement_details);
    }

    @Override
    public void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoanDisbursementDetailsViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();

            Intent intent = getIntent();
            mBranchId = intent.getStringExtra(KEY_INTENT_BRANCH_ID);
            mCenterId = intent.getStringExtra(KEY_INTENT_CENTER_ID);
            mMemberId = intent.getStringExtra(KEY_INTENT_MEMBER_ID);
            mViewModel.fetchLoanDisbursement(mBranchId, mCenterId, mMemberId);
        } else {
            mBinding.setModel(mViewModel);
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }

        mBinding.ivFingerprintVerify.setOnClickListener(view -> startFingerprintCapture());
        observeFetchLoanDisbursementStatus();
        observeValidationStatus();
        observeDataSendStatus();
        observeVerifyFingerprintStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeviceUnavailable() {
        toggleFinger(false);
        Snackbar.make(mBinding.btnSendServer, R.string.connect_fingerprint, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceHasPermission() {
        toggleFinger(true);
    }

    @Override
    public void onFingerprintCaptured(Bitmap bitmap, byte[] template) {
        Log.d(TAG, "received fingerprint");
        showDialog();
        toggleFinger(true);
        String encodedTemplate = Base64.encodeToString(template, Base64.NO_WRAP);
        mViewModel.verifyFingerPrintData(mBranchId, mCenterId, mMemberId, encodedTemplate);
    }

    @Override
    public void onCaptureError(int code, int score) {
        toggleFinger(true);
        if (code == -212) {
            showScannerErrorAlert();
        } else if (code == -10) {
            showQualityNotOkayAlert(score);
        } else {
            Snackbar.make(mBinding.btnSendServer, R.string.scan_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onVerificationComplete(boolean matched) {
        // nothing to do
    }

    @Override
    public void onDeviceDisconnected() {
        toggleFinger(false);
    }

    private void observeFetchLoanDisbursementStatus() {
        mViewModel.fetchLoanDisbursementStatus().observe(this, responseWrapper -> {
            if (responseWrapper.getResponse().isSuccess()) {
                mBinding.setModel(mViewModel);
                updateEditability();
            }
        });
    }

    private void observeValidationStatus() {
        mViewModel.getValidationStatus().observe(this, validationStatus -> {
            if (validationStatus.isSuccess()) {
                showDialog();
                mViewModel.sendLoanDisbursementData();
            } else {
                if (validationStatus.getMessage() != null) {
                    Snackbar.make(mBinding.btnSendServer, validationStatus.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeVerifyFingerprintStatus() {
        mViewModel.getVerifyFingerPrintStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                if (response.isSuccess()) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                    dismissDialog();
                    mBinding.btnSendServer.setEnabled(true);
                    mBinding.ivFingerprintStatus.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "observeAuthenticationStatus: " + response.getMessage());
                    Snackbar.make(mBinding.btnSendServer, response.getMessage(), Snackbar.LENGTH_LONG).show();
                    dismissDialog();
                }
            } else {
                Log.d(TAG, "observeRegistrationStatus: responsewrapper is null");
            }
        });
    }

    private void observeDataSendStatus() {
        mViewModel.getLoanDisbursementStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                if (response.isSuccess()) {
                    showSuccessDialog(response.getMessage());
                } else {
                    Log.d(TAG, "observeAuthenticationStatus: " + response.getMessage());
                    Snackbar.make(mBinding.btnSendServer, response.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                dismissDialog();
            } else {
                Log.d(TAG, "observeRegistrationStatus: responsewrapper is null");
            }
        });
    }

    private void updateEditability() {
        LoanDisbursementFields fields = mViewModel.getLoanDisbursementForm().getFields();
        if (fields.getIsSynced().equals("Y")) {
            mBinding.etPassbookNo.setFocusable(false);
        }
    }

    private void startFingerprintCapture() {
        toggleFinger(false);
        captureFingerprint();
    }

    private void toggleFinger(boolean enable) {
        mBinding.ivFingerprintVerify.setEnabled(enable &&
                mViewModel.getLoanDisbursementForm().getFields().getIsSynced().equals("N"));
    }

    private void showSuccessDialog(String message) {
        new AwesomeSuccessDialog(this)
                .setTitle(R.string.operation_successful)
                .setMessage(message)
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                .setCancelable(false)
                .setPositiveButtonText(getString(R.string.ok))
                .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(this::finish)
                .show();
    }
}
