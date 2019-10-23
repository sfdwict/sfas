package org.sdfw.biometric;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Intent;
import android.util.Log;

import org.sdfw.biometric.di.component.AppComponent;
import org.sdfw.biometric.di.component.DaggerAppComponent;
import org.sdfw.biometric.service.FingerprintService;
import org.sdfw.biometric.ui.SplashActivity;
import org.sdfw.biometric.util.Constant;
import org.sdfw.biometric.util.SessionManager;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

import static org.sdfw.biometric.util.Constant.TAG;

public class App extends DaggerApplication implements LifecycleObserver {

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppStarted() {
        Log.d(TAG, "App started");
        if (!sessionManager.isValidSession()) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        startService(new Intent(this, FingerprintService.class));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppStopped() {
        Log.d(Constant.TAG, "App stopped");
        sessionManager.logLastAccessTime();
        stopService(new Intent(this, FingerprintService.class));
    }
}
