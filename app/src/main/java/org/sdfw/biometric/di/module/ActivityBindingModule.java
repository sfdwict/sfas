package org.sdfw.biometric.di.module;

import org.sdfw.biometric.ui.EarlySettlementDetailsActivity;
import org.sdfw.biometric.ui.FingerprintAuthActivity;
import org.sdfw.biometric.ui.FingerprintRegistrationActivity;
import org.sdfw.biometric.ui.HomeActivity;
import org.sdfw.biometric.ui.MemberDetailsActivity;
import org.sdfw.biometric.ui.LoanDisbursementDetailsActivity;
import org.sdfw.biometric.ui.SavingWithdrawnDetailsActivity;
import org.sdfw.biometric.ui.SigninActivity;
import org.sdfw.biometric.ui.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector
    abstract FingerprintRegistrationActivity contributeFingerprintRegistrationActivity();

    @ContributesAndroidInjector
    abstract FingerprintAuthActivity contributeFingerprintAuthActivity();

    @ContributesAndroidInjector
    abstract SigninActivity contributeSigninActivity();

    @ContributesAndroidInjector
    abstract LoanDisbursementDetailsActivity contributeLoanDisbursementDetailsActivity();

    @ContributesAndroidInjector
    abstract EarlySettlementDetailsActivity contributeEarlySettlementDetailsActivity();

    @ContributesAndroidInjector
    abstract SavingWithdrawnDetailsActivity contributeSavingWithdrawnDetailsActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector
    abstract MemberDetailsActivity contributeMemberDetailsActivity();

}
