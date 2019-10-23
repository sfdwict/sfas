package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.sdfw.biometric.viewmodel.SplashViewModel;

import javax.inject.Inject;

import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static org.sdfw.biometric.util.Constant.TAG;

public class SplashActivity extends DaggerDialogActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearWorkManagerIfFailed();
        setupViewModel(savedInstanceState);
    }

    private void clearWorkManagerIfFailed() {
        // check work manager queue
        WorkManager.getInstance().getWorkInfosForUniqueWorkLiveData("create_member").observe(this, workInfos -> {
            for (WorkInfo info : workInfos) {
                Log.d(TAG, "onCreate: " + info.toString());
//                if (info.getState() == WorkInfo.State.FAILED) {
//                    Log.d(TAG, "clearWorkManagerIfFailed: cancelling " + info.getId());
//                    WorkManager.getInstance().cancelWorkById(info.getId());
//                    break;
//                }
            }
        });
    }

    private void setupViewModel(Bundle savedInstanceState) {
        SplashViewModel mViewModel = ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel.class);
        mViewModel.getSplashScreenTimerUpdate().observe(this, complete -> {
            if (complete) {
                Intent intent = new Intent(this, FingerprintAuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}