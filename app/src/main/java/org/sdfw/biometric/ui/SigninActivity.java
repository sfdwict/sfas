package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivitySigninBinding;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.network.response.SigninResponse;
import org.sdfw.biometric.viewmodel.SigninViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_EIN;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_USER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class SigninActivity extends DaggerDialogActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivitySigninBinding mBinding;
    private SigninViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin);
        setupDialog();
        setupViewModel(savedInstanceState);
        observeValidationStatus();
        observeSigninStatus();
        checkForUpdate();
    }

    private void checkForUpdate() {
        AppUpdater appUpdater = new AppUpdater(getApplicationContext())
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setButtonDoNotShowAgain(null)
                .setButtonDismiss(null)
                .setCancelable(false)
                ;
        appUpdater.start();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DIALOG, isDialogShowing());
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

    private void observeValidationStatus() {
        mViewModel.getValidationStatus().observe(this, validationStatus -> {
            if (validationStatus.isSuccess()) {
                mViewModel.signin();
                showDialog();
            } else {
                if (validationStatus.getMessage() != null) {
                    Snackbar.make(mBinding.btnSignin, validationStatus.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeSigninStatus() {
        mViewModel.getSigninStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                if (responseWrapper.getResponse().isSuccess()) {
                    Log.d(TAG, "signin success");
                    SigninResponse response = (SigninResponse) responseWrapper.getResponse();
                    Intent intent = new Intent(this, FingerprintRegistrationActivity.class);
                    intent.putExtra(KEY_EIN, response.getUser().getUserId());
                    startActivity(intent);
                    finish();
                } else {
                    MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                    Snackbar.make(mBinding.btnSignin, response.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                dismissDialog();
            }
        });
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(SigninViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
        } else {
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }
        mViewModel.getSigninForm().getFields().setUserId(getIntent().getCharSequenceExtra(KEY_INTENT_USER_ID).toString());
        mBinding.setModel(mViewModel);
    }
}
