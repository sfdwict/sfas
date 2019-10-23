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
import org.sdfw.biometric.adapter.SavingWithdrawnAdapter;
import org.sdfw.biometric.databinding.FragmentSavingWithdrawnBinding;
import org.sdfw.biometric.delegate.ItemClickListener;
import org.sdfw.biometric.model.SavingWithdrawn;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.SavingWithdrawnViewModel;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_INTENT_BRANCH_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_CENTER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_MEMBER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class SavingWithdrawnFragment extends DaggerDialogFragment implements ItemClickListener<SavingWithdrawn> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SavingWithdrawnViewModel mViewModel;
    private FragmentSavingWithdrawnBinding mBinding;


    public static SavingWithdrawnFragment newInstance() {
        return new SavingWithdrawnFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_saving_withdrawn, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: SavingWithdrawnFragment " + this.hashCode());
        setupDialog();
        setupViewModel(savedInstanceState);
        setupSavingWithdrawnAdapter();
        mBinding.swipeRefresh.setOnRefreshListener(() -> mViewModel.fetchSavingWithdrawnList());
        observeFindUserStatus();
        observeSavingWithdrawnListStatus();
    }

    private void observeFindUserStatus() {
        mViewModel.getFindUserStatus().observe(this, success -> {
            if (success != null && success) {
                showDialog();
                mViewModel.fetchSavingWithdrawnList();
            }
        });
    }

    private void observeSavingWithdrawnListStatus() {
        mViewModel.getSavingWithdrawnListStatus().observe(this, responseWrapper -> {
            if (responseWrapper.getResponse().isSuccess()) {
                toggleViewsOnEmptyCondition(true);
            } else {
                if (responseWrapper.getResponseCode() == 400) {
                    mViewModel.clearSavingWithdrawns();
                }
                if (mViewModel.getSavingWithdrawnListForm().getSavingWithdrawns().size() == 0) {
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
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(SavingWithdrawnViewModel.class);
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
            mViewModel.reloadSavingWithdrawnListAdapter();
        }
    }


    private void setupSavingWithdrawnAdapter() {
        mBinding.rclSavingWithdrawn.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rclSavingWithdrawn.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mBinding.rclSavingWithdrawn.setAdapter(new SavingWithdrawnAdapter(this));
    }

    private void toggleViewsOnEmptyCondition(boolean show) {
        mBinding.txtDayOpening.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.rclSavingWithdrawn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtEmpty.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onItemClick(SavingWithdrawn item) {
        if (item == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), SavingWithdrawnDetailsActivity.class);
        intent.putExtra(KEY_INTENT_BRANCH_ID, item.getBranchId());
        intent.putExtra(KEY_INTENT_CENTER_ID, item.getCenterId());
        intent.putExtra(KEY_INTENT_MEMBER_ID, item.getMemberId());
        startActivity(intent);
    }
}
