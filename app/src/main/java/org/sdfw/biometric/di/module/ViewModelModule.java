package org.sdfw.biometric.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import org.sdfw.biometric.di.key.ViewModelKey;
import org.sdfw.biometric.viewmodel.EarlySettlementDetailsViewModel;
import org.sdfw.biometric.viewmodel.EarlySettlementViewModel;
import org.sdfw.biometric.viewmodel.FactoryViewModel;
import org.sdfw.biometric.viewmodel.FingerprintRegistrationViewModel;
import org.sdfw.biometric.viewmodel.HomeViewModel;
import org.sdfw.biometric.viewmodel.MemberDetailsViewModel;
import org.sdfw.biometric.viewmodel.LoanDisbursementDetailsViewModel;
import org.sdfw.biometric.viewmodel.LoanDisbursementViewModel;
import org.sdfw.biometric.viewmodel.MemberMatrixViewModel;
import org.sdfw.biometric.viewmodel.SavingWithdrawnDetailsViewModel;
import org.sdfw.biometric.viewmodel.SavingWithdrawnViewModel;
import org.sdfw.biometric.viewmodel.SigninViewModel;
import org.sdfw.biometric.viewmodel.FingerprintAuthViewModel;
import org.sdfw.biometric.viewmodel.SplashViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FingerprintAuthViewModel.class)
    abstract ViewModel bindFingerprintAuthViewModel(FingerprintAuthViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SigninViewModel.class)
    abstract ViewModel bindSigninViewModel(SigninViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FingerprintRegistrationViewModel.class)
    abstract ViewModel bindFingerprintRegistrationViewModel(FingerprintRegistrationViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MemberMatrixViewModel.class)
    abstract ViewModel bindMemberMatrixViewModel(MemberMatrixViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MemberDetailsViewModel.class)
    abstract ViewModel bindMemberDetailsViewModel(MemberDetailsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoanDisbursementViewModel.class)
    abstract ViewModel bindLoanDisbursementViewModel(LoanDisbursementViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EarlySettlementViewModel.class)
    abstract ViewModel bindEarlySettlementViewModel(EarlySettlementViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SavingWithdrawnViewModel.class)
    abstract ViewModel bindSavingWithdrawnViewModel(SavingWithdrawnViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoanDisbursementDetailsViewModel.class)
    abstract ViewModel bindLoanDisbursementDetailsViewModel(LoanDisbursementDetailsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EarlySettlementDetailsViewModel.class)
    abstract ViewModel bindEarlySettlementDetailsViewModel(EarlySettlementDetailsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SavingWithdrawnDetailsViewModel.class)
    abstract ViewModel bindSavingWithdrawnDetailsViewModel(SavingWithdrawnDetailsViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
