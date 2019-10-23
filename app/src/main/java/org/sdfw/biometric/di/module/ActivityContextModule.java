package org.sdfw.biometric.di.module;

import android.support.v7.app.AppCompatActivity;

import org.sdfw.biometric.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityContextModule {

    private final AppCompatActivity activity;

    public ActivityContextModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public AppCompatActivity provideActivity() {
        return activity;
    }
}
