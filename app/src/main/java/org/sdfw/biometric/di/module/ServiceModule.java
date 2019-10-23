package org.sdfw.biometric.di.module;

import org.sdfw.biometric.service.FingerprintService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceModule {
    @ContributesAndroidInjector()
    abstract FingerprintService contributeFingerprintService();
}
