package org.sdfw.biometric.di.module;

import org.sdfw.biometric.ui.EarlySettlementFragment;
import org.sdfw.biometric.ui.DashboardFragment;
import org.sdfw.biometric.ui.LoanDisbursementFragment;
import org.sdfw.biometric.ui.MemberMatrixFragment;
import org.sdfw.biometric.ui.SavingWithdrawnFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract DashboardFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract MemberMatrixFragment contributeMemberMatrixFragment();

    @ContributesAndroidInjector
    abstract LoanDisbursementFragment contributeLoanDisbursementFragment();

    @ContributesAndroidInjector
    abstract EarlySettlementFragment contributeEarlySettlementFragment();

    @ContributesAndroidInjector
    abstract SavingWithdrawnFragment contributeSavingWithdrawnFragment();
}
