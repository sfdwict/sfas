package org.sdfw.biometric.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import org.sdfw.biometric.lifecycle.SingleLiveEvent;
import org.sdfw.biometric.repository.MemberRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_PREF_LAST_CLEANUP_DATE;
import static org.sdfw.biometric.util.Constant.TAG;

public class SplashViewModel extends ViewModel {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ScheduledExecutorService executor;

    @Inject
    MemberRepository memberRepository;

    @Inject
    public SplashViewModel() {
    }

    public SingleLiveEvent<Boolean> getSplashScreenTimerUpdate() {
        SingleLiveEvent<Boolean> data = new SingleLiveEvent<>();
        executor.schedule(() -> {
            cleanupMembers();
            data.postValue(true);
        }, 2, TimeUnit.SECONDS);
        return data;
    }

    @SuppressLint("ApplySharedPref")
    private void cleanupMembers() {
        String lastDate = sharedPreferences.getString(KEY_PREF_LAST_CLEANUP_DATE, "");
        String currDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d(TAG, "cleanupMembers: day difference: " + !currDate.equals(lastDate));
        if (!currDate.equals(lastDate)) {
            Log.d(TAG, "cleanupMembers: from cache");
            memberRepository.deleteAll();
            sharedPreferences.edit()
                    .putString(KEY_PREF_LAST_CLEANUP_DATE, currDate)
                    .commit();
        }
    }
}
