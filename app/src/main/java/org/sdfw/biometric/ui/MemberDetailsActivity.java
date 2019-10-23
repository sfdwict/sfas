package org.sdfw.biometric.ui;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.sdfw.biometric.R;
import org.sdfw.biometric.databinding.ActivityMemberDetailsBinding;
import org.sdfw.biometric.form.field.MemberFields;
import org.sdfw.biometric.network.response.MessageResponse;
import org.sdfw.biometric.viewmodel.MemberDetailsViewModel;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import static org.sdfw.biometric.util.Constant.CODE_MEMBER_FINGERPRINT;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_BRANCH_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_CENTER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_MEMBER_ID;
import static org.sdfw.biometric.util.Constant.KEY_INTENT_REGISTER_MEMBER_FP;
import static org.sdfw.biometric.util.Constant.KEY_SHOW_DIALOG;
import static org.sdfw.biometric.util.Constant.TAG;

public class MemberDetailsActivity extends DaggerDialogActivity implements IPickResult, DatePickerDialog.OnDateSetListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DatePickerDialog datePickerDialog;
    private ActivityMemberDetailsBinding mBinding;
    private MemberDetailsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_member_details);
        setupDialog();
        setupCalendar();
        setupViewModel(savedInstanceState);
        observeFetchMemberStatus();
        observeValidationStatus();
        observeCreateMemberStatus();
        mBinding.imageViewProfile.setOnClickListener(view -> {
            mBinding.txtImageStatus.setError(null);
            PickImageDialog.build(new PickSetup()
                    .setPickTypes(EPickType.CAMERA)
                    .setWidth(200)
                    .setHeight(200)
            ).show(this);
        });
        mBinding.etDob.setOnClickListener(view -> {
            mBinding.etDob.setError(null);
            datePickerDialog.show();
        });
        mBinding.imgRegisterFingerprint.setOnClickListener(view -> {
            mBinding.txtRegisterFingerprint.setError(null);
            Intent intent = new Intent(MemberDetailsActivity.this, FingerprintRegistrationActivity.class);
            intent.putExtra(KEY_INTENT_REGISTER_MEMBER_FP, true);
            startActivityForResult(intent, CODE_MEMBER_FINGERPRINT);
        });
        mBinding.btnSubmit.setOnClickListener(view -> {
            clearFocusAndHideKeyboard();
            mViewModel.onValidate();
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_DIALOG, isDialogShowing());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void observeFetchMemberStatus() {
        mViewModel.getFetchMemberStatus().observe(this, responseWrapper -> {
            if (responseWrapper.getResponse().isSuccess()) {
                mBinding.setModel(mViewModel);
                updateEditability();
                dismissDialog();
            }
        });
    }

    private void observeValidationStatus() {
        mViewModel.getValidationStatus().observe(this, validationStatus -> {
            if (validationStatus.isSuccess()) {
                mViewModel.createMember();
                showDialog();
            } else {
                if (mViewModel.getMemberDetails().getFields().getImage() == null) {
                    mBinding.scrParent.fullScroll(View.FOCUS_UP);
                }
                if (validationStatus.getMessage() != null) {
                    Snackbar.make(mBinding.btnSubmit, validationStatus.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void observeCreateMemberStatus() {
        mViewModel.getCreateMemberStatus().observe(this, responseWrapper -> {
            if (responseWrapper != null) {
                MessageResponse response = (MessageResponse) responseWrapper.getResponse();
                if (response.isSuccess()) {
                    Log.d(TAG, "create success");
                    showSuccessDialog(response.getMessage());
                } else {
                    if (responseWrapper.getResponseCode() == 409) {
                        showWarningDialog(response.getMessage());
                    } else if (responseWrapper.getResponseCode() > 0) {
                        Snackbar.make(mBinding.btnSubmit, response.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        showSuccessDialog(getString(R.string.member_saved));
                    }
                }
                dismissDialog();
            }
        });
    }

    private void updateEditability() {
        MemberFields fields = mViewModel.getMemberDetails().getFields();
        if (!fields.isNewMember()) {
            boolean notVerified = fields.getVerificationStatus() == null;
            Log.d(TAG, "updateEditability: verification status " + fields.getVerificationStatus());
            mBinding.etMemberName.setFocusable(fields.getMemberName() == null && notVerified);
            mBinding.spnSaversOnly.setOnTouchListener((view, motionEvent) -> true);
            mBinding.etParentName.setFocusable(fields.getSpouseName() == null && notVerified);
            mBinding.etNomineeName.setFocusable(fields.getSuccessor() == null && notVerified);
            if (!(fields.getDateOfBirth() == null && notVerified)) {
                mBinding.etDob.setOnClickListener(null);
            }
            mBinding.etFatherName.setFocusable(fields.getFatherName() == null && notVerified);
            mBinding.etMotherName.setFocusable(fields.getMotherName() == null && notVerified);
            mBinding.etNid.setFocusable((fields.getVoterId() == null ||
                    !Arrays.asList(10, 13, 17).contains(fields.getVoterId().length())) && notVerified);
            mBinding.etMobileNo.setFocusable((fields.getMobileNo() == null || fields.getMobileNo().length() != 11) && notVerified);
            mBinding.etAddress.setFocusable(fields.getAddress() == null && notVerified);
            if (!(fields.getSex() == null && notVerified)) {
                mBinding.spnSex.setOnTouchListener((view, motionEvent) -> true);
            }
            if (!(fields.getMaritalStatus() == null && notVerified)) {
                mBinding.spnMarital.setOnTouchListener((view, motionEvent) -> true);
            }
            mBinding.imageViewProfile.setEnabled(!fields.isHasImage() && notVerified);
            mBinding.imgRegisterFingerprint.setEnabled(!fields.isHasFingerprint() && notVerified);
            mBinding.btnSubmit.setEnabled(notVerified || fields.getVerificationStatus().equals("P"));
        }
    }

    private void setupCalendar() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(MemberDetailsActivity.this, R.style.DialogLightTheme, MemberDetailsActivity.this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        calendar.add(Calendar.YEAR, -17);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, -47); // max age 64 (64 - 17 = 47)
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    private void setupViewModel(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MemberDetailsViewModel.class);
        if (savedInstanceState == null) {
            mViewModel.init();

            Intent intent = getIntent();
            String branchId = intent.getStringExtra(KEY_INTENT_BRANCH_ID);
            String centerId = intent.getStringExtra(KEY_INTENT_CENTER_ID);
            String memberId = intent.getStringExtra(KEY_INTENT_MEMBER_ID);
            mViewModel.fetchMember(branchId, centerId, memberId);
            showDialog();
        } else {
            mBinding.setModel(mViewModel);
            updateEditability();
            if (savedInstanceState.getBoolean(KEY_SHOW_DIALOG)) {
                showDialog();
            }
        }
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        mBinding.imageViewProfile.setImageBitmap(pickResult.getBitmap());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pickResult.getBitmap().compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Log.d(TAG, "onPickResult: " + bytes.length);
//        Log.d(TAG, Base64.encodeToString(bytes, Base64.NO_WRAP));
        mViewModel.getMemberDetails().getFields().setImage(Base64.encodeToString(bytes, Base64.NO_WRAP));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "onDateSet: " + year + "/" + monthOfYear + "/" + dayOfMonth);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        mViewModel.getMemberDetails().setDateOfBirthForm(sdf.format(calendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CODE_MEMBER_FINGERPRINT) {
            if (resultCode == RESULT_OK) {
                MemberFields fields = mViewModel.getMemberDetails().getFields();
//                Log.d(TAG, "onActivityResult: " + data.getStringExtra("template1"));
                fields.setTemplate1(data.getStringExtra("template1"));
                fields.setTemplate2(data.getStringExtra("template2"));
                fields.setTemplate3(data.getStringExtra("template3"));
                fields.setTemplate4(data.getStringExtra("template4"));
                mBinding.imgRegistrationStatus.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearFocusAndHideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSuccessDialog(String message) {
        new AwesomeSuccessDialog(this)
                .setTitle(R.string.operation_successful)
                .setMessage(message)
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                .setCancelable(false)
                .setPositiveButtonText(getString(R.string.ok))
                .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(this::finish)
                .show();
    }

    private void showWarningDialog(String message) {
        new AwesomeErrorDialog(this)
                .setTitle(R.string.error_occurred)
                .setMessage(message)
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false)
                .setButtonText(getString(R.string.ok))
                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                .setButtonTextColor(R.color.white)
                .setErrorButtonClick(this::finish)
                .show();
    }
}
