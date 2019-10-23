package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.FragmentHomeBinding;
import org.sdfw.biometric.delegate.NavigationListener;
import org.sdfw.biometric.model.User;
import org.sdfw.biometric.viewmodel.HomeViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.TAG;

public class DashboardFragment extends DaggerDialogFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentHomeBinding mBinding;
    private HomeViewModel mViewModel;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewModel(savedInstanceState);
        observeFindUserStatus();
        mBinding.btnMemberships.setOnClickListener(view -> ((NavigationListener) getActivity()).onNavigate(R.id.nav_memberships));
        mBinding.btnLoanDisbursements.setOnClickListener(view -> ((NavigationListener) getActivity()).onNavigate(R.id.nav_loan_disbursements));
        mBinding.btnSavingWithdrawns.setOnClickListener(view -> ((NavigationListener) getActivity()).onNavigate(R.id.nav_saving_withdrawns));
        mBinding.btnEarlySettlements.setOnClickListener(view -> ((NavigationListener) getActivity()).onNavigate(R.id.nav_early_settlements));
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();
        }
        mViewModel.findUser();
    }

    private void observeFindUserStatus() {
        mViewModel.getFindUserStatus().observe(this, user -> {
            if (user != null) {
                configurePrivilege(user);
            }
        });
    }

    private void configurePrivilege(User user) {
        Log.d(TAG, "configurePrivilege: " + user.getAppRole());
        if (user.getAppRole() == null) return;
        switch (user.getAppRole()) {
            case "A":
                mBinding.btnLoanDisbursements.setEnabled(true);
                mBinding.btnSavingWithdrawns.setEnabled(true);
                mBinding.btnEarlySettlements.setEnabled(true);
                break;
            case "B":
                mBinding.btnMemberships.setEnabled(true);
                break;
            case "C":
                mBinding.btnMemberships.setEnabled(true);
                mBinding.btnLoanDisbursements.setEnabled(true);
                mBinding.btnSavingWithdrawns.setEnabled(true);
                mBinding.btnEarlySettlements.setEnabled(true);
                break;
        }
    }
}
