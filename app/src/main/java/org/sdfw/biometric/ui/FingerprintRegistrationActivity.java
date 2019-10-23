package org.sdfw.biometric.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivityFingerprintRegistrationBinding;
import org.sdfw.biometric.form.field.UserFields;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.FingerprintRegistrationViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_EIN;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_REGISTER_MEMBER_FP;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class FingerprintRegistrationActivity extends FingerprintComponentActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityFingerprintRegistrationBinding mBinding;
    private FingerprintRegistrationViewModel mViewModel;
    private int mCurrentFinger = 0;
    private boolean memberRegistration;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DIALOG, isDialogShowing());
    }

    @Override
    public void setContentView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fingerprint_registration);
    }

    @Override
    public void setupViewModel(Bundle savedInstanceState) {
        memberRegistration = getIntent().getBooleanExtra(KEY_INTENT_REGISTER_MEMBER_FP, false);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(FingerprintRegistrationViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
        } else {
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }
        mBinding.setModel(mViewModel);
        mBinding.btnConfirm.setOnClickListener(view -> {
            if (!memberRegistration) {
                showDialog();
                mViewModel.registerFingerprints();
            } else {
                Intent intent = new Intent();
//                Log.d(TAG, "setupViewModel: " + mViewModel.getFingerprintRegistrationForm().getFields().getTemplate1());
                intent.putExtra("template1", mViewModel.getFingerprintRegistrationForm().getFields().getTemplate1());
                intent.putExtra("template2", mViewModel.getFingerprintRegistrationForm().getFields().getTemplate2());
                intent.putExtra("template3", mViewModel.getFingerprintRegistrationForm().getFields().getTemplate3());
                intent.putExtra("template4", mViewModel.getFingerprintRegistrationForm().getFields().getTemplate4());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBinding.btnLeftIndex.setOnClickListener(view -> {
            Log.d(TAG, "setupViewModel: left index");
            startFingerprintCapture(1);
        });
        mBinding.btnLeftThumb.setOnClickListener(view -> {
            Log.d(TAG, "setupViewModel: left thumb");
            startFingerprintCapture(2);
        });
        mBinding.btnRightThumb.setOnClickListener(view -> {
            Log.d(TAG, "setupViewModel: right thumb");
            startFingerprintCapture(3);
        });
        mBinding.btnRightIndex.setOnClickListener(view -> {
            Log.d(TAG, "setupViewModel: right index");
            startFingerprintCapture(4);
        });
        mViewModel.getFingerprintRegistrationForm().getFields().setUserId(getIntent().getStringExtra(KEY_EIN));
        populateDeviceId();
        // temporary empty token
        mViewModel.getFingerprintRegistrationForm().getFields().setDeviceToken("null");
        observeRegistrationStatus();
        // need to call to update status in views
        mViewModel.onTemplateValidate();
    }

    @Override
    public void onDeviceUnavailable() {
        toggleFingers(false);
        Snackbar.make(mBinding.btnConfirm, R.string.connect_fingerprint, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDeviceHasPermission() {
        toggleFingers(true);
    }

    @Override
    public void onFingerprintCaptured(Bitmap bitmap, byte[] template) {
        Log.d(TAG, "received fingerprint");
        mBinding.imgFingerprint.setImageBitmap(bitmap);
        saveTemplate(template);
        mViewModel.onTemplateValidate();
        mCurrentFinger = 0;
        toggleFingers(true);
        toggleFingerStates(0);
    }

    @Override
    public void onCaptureError(int code, int score) {
        mCurrentFinger = 0;
        toggleFingers(true);
        toggleFingerStates(mCurrentFinger);
        if (code == -212) {
            showScannerErrorAlert();
        } else if (code == -10) {
            showQualityNotOkayAlert(score);
        } else {
            Snackbar.make(mBinding.imgFingerprint, R.string.scan_error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onVerificationComplete(boolean matched) {
        // nothing to do
    }

    @Override
    public void onDeviceDisconnected() {
        toggleFingers(false);
    }

    private void requestReadPhoneStatePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_PHONE_STATE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        populateDeviceId();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(mBinding.btnConfirm, R.string.permission_error, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void populateDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestReadPhoneStatePermission();
            return;
        }
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        Log.d(TAG, "populateDeviceId: device id received");
        mViewModel.getFingerprintRegistrationForm().getFields().setDeviceId(deviceId);
    }

    private void observeRegistrationStatus() {
        mViewModel.getRegistrationStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                if (response.isSuccess()) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                    dismissDialog();
                    unbindService();
                    finish();
                } else {
                    Log.d(TAG, "observeAuthenticationStatus: " + response.getMessage());
                    Snackbar.make(mBinding.btnConfirm, response.getMessage(), Snackbar.LENGTH_LONG).show();
                    dismissDialog();
                }
            } else {
                Log.d(TAG, "observeRegistrationStatus: responsewrapper is null");
            }
        });
    }

    private void startFingerprintCapture(int index) {
        mCurrentFinger = index;
        toggleFingers(false);
        toggleFingerStates(mCurrentFinger);
        mBinding.btnConfirm.setEnabled(false);
        resetFingerprintImage();
        captureFingerprint(true);
    }

    private void resetFingerprintImage() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.fingerprintAuthDrawable, typedValue, true);
        mBinding.imgFingerprint.setImageResource(typedValue.resourceId);
    }

    private void toggleFingers(boolean enable) {
        mBinding.btnLeftIndex.setEnabled(enable);
        mBinding.btnLeftThumb.setEnabled(enable);
        mBinding.btnRightThumb.setEnabled(enable);
        mBinding.btnRightIndex.setEnabled(enable);
    }
    
    private void toggleFingerStates(int index) {
        switch (index) {
            case 1:
                mBinding.btnLeftIndex.setChecked(true);
                mBinding.btnLeftThumb.setChecked(false);
                mBinding.btnRightThumb.setChecked(false);
                mBinding.btnRightIndex.setChecked(false);
                break;
            case 2:
                mBinding.btnLeftIndex.setChecked(false);
                mBinding.btnLeftThumb.setChecked(true);
                mBinding.btnRightThumb.setChecked(false);
                mBinding.btnRightIndex.setChecked(false);
                break;
            case 3:
                mBinding.btnLeftIndex.setChecked(false);
                mBinding.btnLeftThumb.setChecked(false);
                mBinding.btnRightThumb.setChecked(true);
                mBinding.btnRightIndex.setChecked(false);
                break;
            case 4:
                mBinding.btnLeftIndex.setChecked(false);
                mBinding.btnLeftThumb.setChecked(false);
                mBinding.btnRightThumb.setChecked(false);
                mBinding.btnRightIndex.setChecked(true);
                break;
            default:
                mBinding.btnLeftIndex.setChecked(false);
                mBinding.btnLeftThumb.setChecked(false);
                mBinding.btnRightThumb.setChecked(false);
                mBinding.btnRightIndex.setChecked(false);
                break;
        }
    }

    private void saveTemplate(byte[] template) {
        UserFields fields = mViewModel.getFingerprintRegistrationForm().getFields();
        String encodedTemplate = Base64.encodeToString(template, Base64.NO_WRAP);
        switch (mCurrentFinger) {
            case 1:
                Log.d(TAG, "saveTemplate: 1");
//                Log.d(TAG, "saveTemplate: " + encodedTemplate);
                fields.setTemplate1(encodedTemplate);
                break;
            case 2:
                Log.d(TAG, "saveTemplate: 2");
                fields.setTemplate2(encodedTemplate);
                break;
            case 3:
                Log.d(TAG, "saveTemplate: 3");
                fields.setTemplate3(encodedTemplate);
                break;
            case 4:
                Log.d(TAG, "saveTemplate: 4");
                fields.setTemplate4(encodedTemplate);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unbindService();
    }
}
