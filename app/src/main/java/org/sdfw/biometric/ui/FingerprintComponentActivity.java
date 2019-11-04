package org.sdfw.biometric.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import org.sdfw.biometric.R;
import org.sdfw.biometric.service.FingerprintService;

import static org.sdfw.biometric.util.Constant.KEY_BITMAP;
import static org.sdfw.biometric.util.Constant.KEY_CANDIDATE;
import static org.sdfw.biometric.util.Constant.KEY_CODE;
import static org.sdfw.biometric.util.Constant.KEY_FINGERPRINT_MATCHED;
import static org.sdfw.biometric.util.Constant.KEY_PROBE;
import static org.sdfw.biometric.util.Constant.KEY_SCORE;
import static org.sdfw.biometric.util.Constant.KEY_TEMPLATE;
import static org.sdfw.biometric.util.Constant.MSG_CAPTURE_ERROR;
import static org.sdfw.biometric.util.Constant.MSG_CAPTURE_FINGERPRINT;
import static org.sdfw.biometric.util.Constant.MSG_DEVICE_DISCONNECTED;
import static org.sdfw.biometric.util.Constant.MSG_FINGERPRINT_RECEIVED;
import static org.sdfw.biometric.util.Constant.MSG_HAS_PERMISSION;
import static org.sdfw.biometric.util.Constant.MSG_NO_DEVICE;
import static org.sdfw.biometric.util.Constant.MSG_REGISTER_CLIENT;
import static org.sdfw.biometric.util.Constant.MSG_UNREGISTER_CLIENT;
import static org.sdfw.biometric.util.Constant.MSG_VERIFICATION_COMPLETE;
import static org.sdfw.biometric.util.Constant.MSG_VERIFY_FINGERPRINT;
import static org.sdfw.biometric.util.Constant.TAG;

public abstract class FingerprintComponentActivity extends DaggerDialogActivity {

    Messenger mService;
    private boolean mIsBound = false;

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NO_DEVICE:
                    onDeviceUnavailable();
                    break;
                case MSG_HAS_PERMISSION:
                    onDeviceHasPermission();
                    break;
                case MSG_FINGERPRINT_RECEIVED:
                    Bundle data = msg.getData();
                    onFingerprintCaptured(data.getParcelable(KEY_BITMAP), data.getByteArray(KEY_TEMPLATE));
                    break;
                case MSG_CAPTURE_ERROR:
                    onCaptureError(msg.getData().getInt(KEY_CODE), msg.getData().getInt(KEY_SCORE, 0));
                    break;
                case MSG_VERIFICATION_COMPLETE:
                    onVerificationComplete(msg.getData().getBoolean(KEY_FINGERPRINT_MATCHED, false));
                    break;
                case MSG_DEVICE_DISCONNECTED:
                    onDeviceDisconnected();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            try {
                Message msg = Message.obtain(null, MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected: service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected: service disconnected");
        }
    };

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setupDialog();
        setupViewModel(savedInstanceState);

    }



    @Override
    protected void onStart() {
        super.onStart();
        bindService();
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
        Log.d(TAG, "onStop: called");
    }

    protected void bindService() {
        bindService(new Intent(this, FingerprintService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    protected void unbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                    e.printStackTrace();
                }
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void captureFingerprint() {
        captureFingerprint(false);
    }

    public void captureFingerprint(boolean enroll) {
        Log.d(TAG, "captureFingerprint: mService null? " + (mService == null));
        if (mService != null) {
            try {
                Bundle data = new Bundle();
                data.putBoolean("enroll", enroll);
                Message msg = Message.obtain(null, MSG_CAPTURE_FINGERPRINT);
                msg.setData(data);
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void verifyFingerprint(String candidate, String... probes) {
        if (probes[0] == null || probes[1] == null || probes[2] == null || probes[1] == null) {
            onVerificationComplete(false);
        }
        if (mService != null) {
            try {
                Message msg = Message.obtain(null, MSG_VERIFY_FINGERPRINT);
                Bundle data = new Bundle();
                data.putString(KEY_CANDIDATE, candidate);
                data.putStringArray(KEY_PROBE, probes);
                msg.setData(data);
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    protected void showScannerErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogLightTheme);
        builder.setTitle(R.string.scanner_error);
        builder.setMessage(R.string.scanner_error_message);
        builder.setIcon(R.drawable.ic_dialog_warning);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ঠিক আছে", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    protected void showQualityNotOkayAlert(int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogLightTheme);
        builder.setTitle(R.string.fingerprint_error);
        builder.setMessage(getString(R.string.fingerprint_error_message, score));
        builder.setIcon(R.drawable.ic_dialog_warning);
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ঠিক আছে", (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    public abstract void setContentView();
    public abstract void setupViewModel(Bundle savedInstanceState);
    public abstract void onDeviceUnavailable();
    public abstract void onDeviceHasPermission();
    public abstract void onFingerprintCaptured(Bitmap bitmap, byte[] template);
    public abstract void onCaptureError(int code, int quality);
    public abstract void onVerificationComplete(boolean matched);
    public abstract void onDeviceDisconnected();

}
