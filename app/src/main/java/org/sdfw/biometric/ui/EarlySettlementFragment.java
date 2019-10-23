package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sdfw.biometric.R;
import org.sdfw.biometric.adapter.EarlySettlementAdapter;
import org.sdfw.biometric.databinding.FragmentEarlySettlementBinding;
import org.sdfw.biometric.delegate.ItemClickListener;
import org.sdfw.biometric.model.EarlySettlement;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.EarlySettlementViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_INTENT_BRANCH_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_CENTER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_MEMBER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class EarlySettlementFragment extends DaggerDialogFragment implements ItemClickListener<EarlySettlement> {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EarlySettlementViewModel mViewModel;
    private FragmentEarlySettlementBinding mBinding;


    public static EarlySettlementFragment newInstance() {
        return new EarlySettlementFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_early_settlement, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: EarlySettlementFragment " + this.hashCode());
        setupDialog();
        setupViewModel(savedInstanceState);
        setupEarlySettlementAdapter();
        mBinding.swipeRefresh.setOnRefreshListener(() -> mViewModel.fetchEarlySettlementList());
        observeFindUserStatus();
        observeEarlySettlementListStatus();
    }

    private void observeFindUserStatus() {
        mViewModel.getFindUserStatus().observe(this, success -> {
            if (success != null && success) {
                showDialog();
                mViewModel.fetchEarlySettlementList();
            }
        });
    }

    private void observeEarlySettlementListStatus() {
        mViewModel.getLoanDisbursementsStatus().observe(this, responseWrapper -> {
            if (responseWrapper.getResponse().isSuccess()) {
                toggleViewsOnEmptyCondition(true);
            } else {
                if (responseWrapper.getResponseCode() == 400) {
                    mViewModel.clearEarlySettlements();
                }
                if (mViewModel.getEarlySettlementListForm().getEarlySettlements().size() == 0) {
                    MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                    mBinding.txtEmpty.setText(response.getMessage());
                    toggleViewsOnEmptyCondition(false);
                } else {
                    toggleViewsOnEmptyCondition(true);
                }
            }
            dismissDialog();
            mBinding.swipeRefresh.setRefreshing(false);
        });
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(EarlySettlementViewModel.class);
        Log.d(TAG, "setupViewModel: savedInstanceState == null? " + (savedInstanceState == null));
        if (savedInstanceState == null) {
            mViewModel.init();
        } else {
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }
        mBinding.setModel(mViewModel);
        if (savedInstanceState == null) {
            mViewModel.findUser();
        } else {
            mViewModel.reloadEarlySettlementsAdapter();
        }
    }

    private void setupEarlySettlementAdapter() {
        mBinding.rclEarlySettlement.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rclEarlySettlement.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mBinding.rclEarlySettlement.setAdapter(new EarlySettlementAdapter(this));
    }

    private void toggleViewsOnEmptyCondition(boolean show) {
        mBinding.txtDayOpening.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.rclEarlySettlement.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtEmpty.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onItemClick(EarlySettlement item) {
        if (item == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), EarlySettlementDetailsActivity.class);
        intent.putExtra(KEY_INTENT_BRANCH_ID, item.getBranchId());
        intent.putExtra(KEY_INTENT_CENTER_ID, item.getCenterId());
        intent.putExtra(KEY_INTENT_MEMBER_ID, item.getMemberId());
        startActivity(intent);
    }
}
