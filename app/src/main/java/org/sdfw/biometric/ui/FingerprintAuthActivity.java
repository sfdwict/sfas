package org.sdfw.biometric.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivityFingerprintAuthBinding;
import org.sdfw.biometric.model.User;
import org.sdfw.biometric.network.response.FingerprintAuthResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.FingerprintAuthViewModel;
import org.sdfw.biometric.worker.ClearMemberSchedulerWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static org.sdfw.biometric.util.Constant.KEY_INTENT_USER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class FingerprintAuthActivity extends FingerprintComponentActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityFingerprintAuthBinding mBinding;
    private FingerprintAuthViewModel mViewModel;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DIALOG, isDialogShowing());
    }

    @Override
    public void setContentView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fingerprint_auth);
    }

    @Override
    public void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(FingerprintAuthViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
        } else {
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }
        mBinding.setModel(mViewModel);
        mBinding.btnFingerprint.setEnabled(false);
        mBinding.btnFingerprint.setOnClickListener(view -> {
            hideKeyboard();
            startFingerprintCapture();
//            startActivity(new Intent(this, HomeActivity.class));
        });
        mBinding.btnPasswordSignin.setOnClickListener(view -> startSigninActivity());
        observeValidationStatus();
        observeAuthenticationStatus();
        scheduleDailyClearMemberJob();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void observeValidationStatus() {
        mViewModel.getValidationStatus().observe(this, validationStatus -> {
            if (validationStatus.isSuccess()) {
                Log.d(TAG, "observeValidationStatus: validated");
                mViewModel.authenticate();
                showDialog();
            } else {
                if (validationStatus.getMessage() != null) {
                    Snackbar.make(mBinding.btnFingerprint, validationStatus.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeAuthenticationStatus() {
        mViewModel.getAuthenticationStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                Log.d(TAG, "observeAuthenticationStatus: invoked " + responseWrapper.getResponse().isSuccess());
                if (responseWrapper.getResponse().isSuccess()) {
                    FingerprintAuthResponse response = (FingerprintAuthResponse) responseWrapper.getResponse();
                    if (response.getAccessToken() == null) {
                        // it's a local response, verify fingerprint locally
                        User user = response.getUser();
//                        Log.d(TAG, "authenticate: email: " + user.getUserId() + " fingerprint: " + user.getTemplate1());
                        if (user.getTemplate1() == null || user.getTemplate2() == null
                                || user.getTemplate3() == null || user.getTemplate4() == null) {
                            checkAuthResult(false);
                        } else {
                            verifyFingerprint(mViewModel.getFingerprintAuthForm().getFields().getTemplate1(),
                                    user.getTemplate1(),
                                    user.getTemplate2(),
                                    user.getTemplate3(),
                                    user.getTemplate4());
                        }
                    } else {
                        checkAuthResult(response.isSuccess());
                    }
                } else {
                    MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                    Log.d(TAG, "observeAuthenticationStatus: " + response.getMessage());
                    Snackbar.make(mBinding.btnFingerprint, response.getMessage(), Snackbar.LENGTH_LONG).show();
                    dismissDialog();
                }
            } else {
                Log.d(TAG, "observeAuthenticationStatus: responsewrapper is null");
            }
        });
    }

    private void scheduleDailyClearMemberJob() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .build();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ClearMemberSchedulerWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance().enqueueUniqueWork("schedule_clean_operation", ExistingWorkPolicy.KEEP, request);
    }

    private void startFingerprintCapture() {
//        requestWritePermission();
        mBinding.btnFingerprint.setEnabled(false);
        captureFingerprint();
        Toast.makeText(this, R.string.scan_finger, Toast.LENGTH_LONG).show();
    }

    private void requestWritePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: Write Permission");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Log.d(TAG, "onPermissionDenied: Write Permission");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void checkAuthResult(boolean success) {
        mBinding.btnFingerprint.setEnabled(true);
        dismissDialog();
        if (success) {
            Log.d(TAG, "authenticate: " + success);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Snackbar.make(mBinding.tilEin, R.string.verify_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void startSigninActivity() {
        Intent intent = new Intent(this, SigninActivity.class);
        intent.putExtra(KEY_INTENT_USER_ID, mBinding.etEin.getText());
        startActivity(intent);
    }

    @Override
    public void onDeviceUnavailable() {
        Snackbar.make(mBinding.btnFingerprint, R.string.connect_fingerprint, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceHasPermission() {
        Log.d(TAG, "onDeviceHasPermission: ");
        mBinding.btnFingerprint.setEnabled(true);
    }

    @Override
    public void onFingerprintCaptured(Bitmap bitmap, byte[] template) {
        Log.d(TAG, "received fingerprint");
//        mBinding.imgLogo.setImageBitmap(bitmap);
//        mBinding.btnFingerprint.setImageBitmap(BitmapFactory.decodeByteArray(template, 0, template.length));
        mViewModel.getFingerprintAuthForm().getFields().setTemplate1(Base64.encodeToString(template, Base64.NO_WRAP));
        mViewModel.onVerificationStart();
        mBinding.btnFingerprint.setEnabled(true);
    }

    @Override
    public void onCaptureError(int code, int score) {
        if (code == -212) {
            showScannerErrorAlert();
        } else if (code == -10) {
            showQualityNotOkayAlert(score);
        } else {
            Snackbar.make(mBinding.btnFingerprint, R.string.scan_error, Snackbar.LENGTH_LONG).show();
            mBinding.btnFingerprint.setEnabled(true);
        }
    }

    @Override
    public void onVerificationComplete(boolean matched) {
        checkAuthResult(matched);
    }

    @Override
    public void onDeviceDisconnected() {
        mBinding.btnFingerprint.setEnabled(false);
    }
}
