package org.sdfw.biometric.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.sdfw.biometric.R;
import org.sdfw.biometric.adapter.decor.ItemOffsetDecoration;
import org.sdfw.biometric.adapter.MemberMatrixAdapter;
import org.sdfw.biometric.databinding.FragmentMemberMatrixBinding;
import org.sdfw.biometric.delegate.ItemClickListener;
import org.sdfw.biometric.model.Center;
import org.sdfw.biometric.model.MemberLite;
import org.sdfw.biometric.network.response.FetchCentersResponse;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.MemberMatrixViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.KEY_INTENT_BRANCH_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_CENTER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_MEMBER_ID;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class MemberMatrixFragment extends DaggerDialogFragment implements ItemClickListener<MemberLite> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentMemberMatrixBinding mBinding;
    private MemberMatrixViewModel mViewModel;
    private ArrayAdapter<String> centerAdapter;

    public static MemberMatrixFragment newInstance() {
        return new MemberMatrixFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_member_matrix, container, false);
        setLegendsTint();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: MatrixFragment " + this.hashCode());
        setupDialog();
        setupViewModel(savedInstanceState);
        setupMemberAdapter();
        mBinding.swipeRefresh.setOnRefreshListener(() -> mViewModel.fetchMemberMatrix());
        observeFindUserStatus();
        observeFetchCentersStatus();
        observeFetchMembersStatus();
    }

    private void observeFindUserStatus() {
        mViewModel.getFindUserStatus().observe(this, success -> {
            if (success != null && success) {
                mViewModel.fetchCenters();
                showDialog();
            }
        });
    }

    private void observeFetchCentersStatus() {
        mViewModel.getFetchCentersStatus().observe(this, responseWrapper -> {
            Log.d(TAG, "observeFetchCentersStatus: invoked "  + responseWrapper.getResponse().isSuccess());
            if (responseWrapper.getResponse().isSuccess()) {
                Log.d(TAG, "observeFetchCentersStatus: success");
                FetchCentersResponse response = (FetchCentersResponse) responseWrapper.getResponse();
                setupCenterAdapter(response.getCenters());
                toggleViewsOnEmptyCondition(true, false);
            } else {
                if (responseWrapper.getResponseCode() == 400) {
                    MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                    mBinding.txtEmpty.setText(response.getMessage());
                    toggleViewsOnEmptyCondition(false, false);
                }
            }
            dismissDialog();
        });
    }

    private void observeFetchMembersStatus() {
        mViewModel.getMemberMatrixStatus().observe(this, responseWrapper -> {
            if (responseWrapper.getResponse().isSuccess()) {
                toggleViewsOnEmptyCondition(true, true);
            } else {
                MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                mBinding.txtEmpty.setText(response.getMessage());
                toggleViewsOnEmptyCondition(false, true);
            }
            dismissDialog();
            mBinding.swipeRefresh.setRefreshing(false);
        });
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MemberMatrixViewModel.class);
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
            mViewModel.reloadCenterAdapter();
        }
    }

    private void setupCenterAdapter(List<Center> centers) {
        Log.d(TAG, "setupCenterAdapter: called");
        if (centers.size() > 0) {
            List<String> entries = new ArrayList<>();
            for (Center c : centers) {
                entries.add(c.getCenterName() + " (" + c.getCenterId() + ")");
            }
            if (centerAdapter == null) {
                centerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, entries);
                centerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.spnCenter.setAdapter(centerAdapter);
            } else {
                centerAdapter.clear();
                centerAdapter.addAll(entries);
            }
            mBinding.spnCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, "onItemSelected: " + i);
                    mViewModel.getMatrix().getFields().setCenterIdIndex(i);
                    mViewModel.clearMemberMatrix();
                    if (i >= 0) {
                        mViewModel.fetchMemberMatrix();
                        showDialog();
                    } else {
                        mBinding.txtEmpty.setText(R.string.select_center);
                        toggleViewsOnEmptyCondition(true, false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Log.d(TAG, "onNothingSelected: ");
                    mViewModel.getMatrix().getFields().setCenterIdIndex(-1);
                    mViewModel.clearMemberMatrix();
                }
            });
        }
    }

    private void setLegendsTint() {
        Drawable check1 = ContextCompat.getDrawable(getContext(), R.drawable.ic_check);
        Drawable check2 = ContextCompat.getDrawable(getContext(), R.drawable.ic_check);
        Drawable check3 = ContextCompat.getDrawable(getContext(), R.drawable.ic_check);
        int red = ContextCompat.getColor(getContext(), R.color.lightRed);
        int green = ContextCompat.getColor(getContext(), R.color.lightGreen);
        int blue = ContextCompat.getColor(getContext(), R.color.lightBlue);
        DrawableCompat.setTint(check1.mutate(), red);
        mBinding.txtFingerprint.setCompoundDrawablesRelativeWithIntrinsicBounds(check1, null, null, null);
        DrawableCompat.setTint(check2.mutate(), green);
        mBinding.txtImage.setCompoundDrawablesRelativeWithIntrinsicBounds(check2, null, null, null);
        DrawableCompat.setTint(check3.mutate(), blue);
        mBinding.txtNid.setCompoundDrawablesRelativeWithIntrinsicBounds(check3, null, null, null);
    }

    private void setupMemberAdapter() {
        mBinding.rclMember.setHasFixedSize(true);
        mBinding.rclMember.setItemViewCacheSize(30);
        mBinding.rclMember.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mBinding.rclMember.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.grid_spacing));
        mBinding.rclMember.setAdapter(new MemberMatrixAdapter(this));
    }

    private void toggleViewsOnEmptyCondition(boolean show, boolean member) {
        mBinding.txtLegends.setVisibility(show && member ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtFingerprint.setVisibility(show && member ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtImage.setVisibility(show && member ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtNid.setVisibility(show && member ? View.VISIBLE : View.INVISIBLE);
        mBinding.lblMembers.setVisibility(show && member ? View.VISIBLE : View.INVISIBLE);
        mBinding.rclMember.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.txtEmpty.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onItemClick(MemberLite item) {
        // TODO implement click
        Log.d(TAG, "onItemClick: " + item.getMemberName());
        Intent intent = new Intent(getContext(), MemberDetailsActivity.class);
        intent.putExtra(KEY_INTENT_BRANCH_ID, item.getBranchId());
        intent.putExtra(KEY_INTENT_CENTER_ID, item.getCenterId());
        intent.putExtra(KEY_INTENT_MEMBER_ID, item.getMemberId());
        startActivity(intent);
    }
}
