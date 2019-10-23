package org.sdfw.biometric.ui;

import android.app.AlertDialog;

import org.sdfw.biometric.R;

import dagger.android.support.DaggerAppCompatActivity;
import dmax.dialog.SpotsDialog;

public class DaggerDialogActivity extends DaggerAppCompatActivity {

    private AlertDialog dialog;

    protected void setupDialog() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setTheme(R.style.Dialog_Loading)
                .setMessage("অপেক্ষা করুন...")
                .build();
    }

    protected void showDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    protected void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected boolean isDialogShowing() {
        return dialog.isShowing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
