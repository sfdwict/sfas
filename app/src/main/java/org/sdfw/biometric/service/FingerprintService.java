package org.sdfw.biometric.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.suprema.BioMiniFactory;
import com.suprema.CaptureResponder;
import com.suprema.IBioMiniDevice;
import com.suprema.IUsbEventHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.android.DaggerService;

import static org.sdfw.biometric.util.Constant.KEY_BITMAP;
import static org.sdfw.biometric.util.Constant.KEY_CANDIDATE;
import static org.sdfw.biometric.util.Constant.KEY_CODE;
import static org.sdfw.biometric.util.Constant.KEY_FINGERPRINT_MATCHED;
import static org.sdfw.biometric.util.Constant.KEY_HAS_DEVICE;
import static org.sdfw.biometric.util.Constant.KEY_HAS_PERMISSION;
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

public class FingerprintService extends DaggerService {

    private static final String ACTION_USB_PERMISSION = "bd.com.robi.fingerprintdemo.permission.USB_PERMISSION";
    private static final int FINGERPRINT_QUALITY = 60;

    @Inject
    UsbManager usbManager;

    @Inject
    Executor executor;

    private UsbDevice mUsbDevice;
    private BioMiniFactory mBioMiniFactory;
    private IBioMiniDevice mCurrentDevice;
    private IBioMiniDevice.CaptureOption mCaptureOption;
    private CaptureResponder mResponder;

    private boolean mEnroll = false;

    private PendingIntent mPermissionIntent;

    private final BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (mUsbDevice != null) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            updateDevice();
                            notifyClient(MSG_HAS_PERMISSION);
                            Log.d(TAG, "Vendor ID : " + mUsbDevice.getVendorId() + "\n");
                            Log.d(TAG, "Product ID: " + mUsbDevice.getProductId() + "\n");
                        } else {
                            Log.e(TAG, "mUsbPermissionReceiver.onReceive() permission denied for device "
                                    + mUsbDevice);
                        }
                    } else {
                        Log.e(TAG, "mUsbPermissionReceiver.onReceive() Device is null");
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mUsbAttachedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                checkDevice();
            }
        }
    };

    private static final int DEVICE_CHECK = 0;
    private static final int PERMISSION_CHECK = 1;
    private static final int FINGERPRINT_VERIFY = 2;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            switch (message.what) {
                case DEVICE_CHECK:
                    if (data != null && data.getBoolean(KEY_HAS_DEVICE, false)) {
                        checkPermission();
                    } else {
                        notifyClient(MSG_NO_DEVICE);
                    }
                    break;
                case PERMISSION_CHECK:
                    if (data != null && data.getBoolean(KEY_HAS_PERMISSION, false)) {
                        updateDevice();
                        notifyClient(MSG_HAS_PERMISSION);
                    } else {
                        requestPermission(mPermissionIntent);
                    }
                    break;
                case FINGERPRINT_VERIFY:
                    notifyClient(MSG_VERIFICATION_COMPLETE, data);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    };

    private Messenger mClient;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    Log.d(TAG, "handleMessage: client registered");
                    mClient = msg.replyTo;
                    checkDevice();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    Log.d(TAG, "handleMessage: client unregistered");
                    mClient = null;
                    break;
                case MSG_CAPTURE_FINGERPRINT:
                    Log.d(TAG, "handleMessage: capture fingerprint");
                    captureFingerprint(msg.getData().getBoolean("enroll", false));
                    break;
                case MSG_VERIFY_FINGERPRINT:
                    Bundle data = msg.getData();
                    match(data.getString(KEY_CANDIDATE), data.getStringArray(KEY_PROBE));
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

    public FingerprintService() {
    }

    private void handleDeviceChangeEvent(IUsbEventHandler.DeviceChangeEvent event, Object o) {
        if (event == IUsbEventHandler.DeviceChangeEvent.DEVICE_ATTACHED && mCurrentDevice == null) {
            if (mBioMiniFactory != null) {
                Log.d(TAG, "handleDeviceChangeEvent: device count: " + mBioMiniFactory.getDeviceCount());
                mCurrentDevice = mBioMiniFactory.getDevice(0);
                mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_TYPE,
                        IBioMiniDevice.TemplateType.ISO19794_2.value()));
                mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, 10000));
                mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE, 1));
                mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.FAST_MODE, 1));
                mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.AUTO_ROTATE, 1));
            }
        } else if (mCurrentDevice != null && event == IUsbEventHandler.DeviceChangeEvent.DEVICE_DETACHED && mCurrentDevice.isEqual(o)) {
            Log.d(TAG, "handleDeviceChangeEvent: usb detached");
            mCurrentDevice = null;
            mUsbDevice = null;
            notifyClient(MSG_DEVICE_DISCONNECTED);
        }
    }

    private void init() {
        Log.d(TAG, "open:");
        close();
        mBioMiniFactory = new BioMiniFactory(getApplication(), usbManager) {
            @Override
            public void onDeviceChange(DeviceChangeEvent deviceChangeEvent, Object o) {
                Log.d(TAG, "onDeviceChange: invoked");
                handleDeviceChangeEvent(deviceChangeEvent, o);
            }
        };
        mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
    }

    private void setupOptions() {
        mCaptureOption = new IBioMiniDevice.CaptureOption();
        mCaptureOption.captureImage = true;
        mCaptureOption.extractParam.captureTemplate = true;
        mCaptureOption.extractParam.maxTemplateSize = IBioMiniDevice.MaxTemplateSize.MAX_TEMPLATE_384;
        mCaptureOption.frameRate = IBioMiniDevice.FrameRate.DEFAULT;
        mCaptureOption.captureTimeout = 10000;
    }

    private void setupCaptureResponder() {
        mResponder = new CaptureResponder() {
            @Override
            public boolean onCaptureEx(Object o, Bitmap bitmap, IBioMiniDevice.TemplateData templateData, IBioMiniDevice.FingerState fingerState) {
                Log.d(TAG, "onCaptureEx: captured");
                Log.d(TAG, "onCaptureEx: " + ((IBioMiniDevice) o).popPerformanceLog());
                byte[] fpImageRaw;
                if (templateData != null && (fpImageRaw = mCurrentDevice.getCaptureImageAsWsq(-1, -1, 2f, 0)) != null) {
                    if (mEnroll && templateData.quality < FINGERPRINT_QUALITY) {
                        Bundle data = new Bundle();
                        data.putInt(KEY_CODE, -10);
                        data.putInt(KEY_SCORE, templateData.quality);
                        notifyClient(MSG_CAPTURE_ERROR, data);
                    } else {
                        mEnroll = false;
//                        Log.d(TAG, "onCaptureEx: " + Base64.encodeToString(fpImageRaw, Base64.NO_WRAP));
//                        writeToFile(fpImageRaw);
                        Log.d(TAG, "onCaptureEx: raw size: " + fpImageRaw.length);
//                        Log.d(TAG, "onCaptureEx: template size: " + templateData.data.length);
                        Log.d(TAG, "onCaptureEx: template quality: " + templateData.quality);
                        Log.d(TAG, String.format(Locale.ENGLISH, "onCaptureEx: fpImage (%d) , FP Quality(%d)", fpImageRaw.length, mCurrentDevice.getFPQuality(mCurrentDevice.getCaptureImageAsBmp(), mCurrentDevice.getImageWidth(), mCurrentDevice.getImageHeight(), 2)));
                        Bundle data = new Bundle();
                        data.putParcelable(KEY_BITMAP, bitmap);
                        data.putByteArray(KEY_TEMPLATE, fpImageRaw);
                        notifyClient(MSG_FINGERPRINT_RECEIVED, data);
                    }
                } else {
                    Bundle data = new Bundle();
                    data.putInt(KEY_CODE, -11);
                    notifyClient(MSG_CAPTURE_ERROR, data);
                }
                return true;
            }

            @Override
            public void onCaptureError(Object o, int code, String s) {
                Log.d(TAG, "error code: " + code);
                Bundle data = new Bundle();
                data.putInt(KEY_CODE, code);
                notifyClient(MSG_CAPTURE_ERROR, data);
            }
        };
    }

    private void writeToFile(byte[] stream) {
        File file = new File(Environment.getExternalStorageDirectory(), "fingerprint.wsq");
        try {
            FileOutputStream fout = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            bos.write(stream);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDevice() {
        Log.d(TAG, "checkDevice: started");
        executor.execute(() -> {
            Message message = Message.obtain(null, DEVICE_CHECK);
            Bundle data = new Bundle();
            data.putBoolean(KEY_HAS_DEVICE, false);
            if (mUsbDevice == null) {
                if (usbManager != null) {
                    HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
                    for (UsbDevice device : deviceList.values()) {
                        if (device.getVendorId() == 0x16d1) {
                            mUsbDevice = device;
                            data.putBoolean(KEY_HAS_DEVICE, true);
                            break;
                        }
                    }
                }
            } else {
                data.putBoolean(KEY_HAS_DEVICE, true);
            }
            message.setData(data);
            mHandler.sendMessage(message);
        });
    }

    private void checkPermission() {
        executor.execute(() -> {
            Message message = Message.obtain(null, PERMISSION_CHECK);
            Bundle data = new Bundle();
            data.putBoolean(KEY_HAS_PERMISSION, false);
            if (usbManager != null) {
                data.putBoolean(KEY_HAS_PERMISSION, usbManager.hasPermission(mUsbDevice));
            }
            message.setData(data);
            mHandler.sendMessage(message);
        });
    }

    private void requestPermission(PendingIntent permissionIntent) {
        if (usbManager != null) {
            usbManager.requestPermission(mUsbDevice, permissionIntent);
        }
    }

    private void updateDevice() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.addDevice(mUsbDevice);
        }
    }

    private void captureFingerprint(boolean enroll) {
        Log.d(TAG, "captureFingerprint: ");
        mEnroll = enroll;
        executor.execute(() -> {
            Log.d(TAG, "captureFingerprint: mCurrentDevice null? " + (mCurrentDevice == null));
            if (mCurrentDevice != null) {
                mCurrentDevice.clearCaptureImageBuffer();
            }
            if (mCurrentDevice != null && !mCurrentDevice.isCapturing()) {
                mCurrentDevice.captureSingle(mCaptureOption, mResponder, true);
            }
        });
    }

    public void match(String strProbe, String[] strCandidates) {
        executor.execute(() -> {
            Message message = Message.obtain(null, FINGERPRINT_VERIFY);
            Bundle data = new Bundle();
            data.putBoolean(KEY_FINGERPRINT_MATCHED, false);
            if (mCurrentDevice != null) {
                byte[] probeTemplate = Base64.decode(strProbe, Base64.NO_WRAP);
                FingerprintTemplate probe = new FingerprintTemplate().create(probeTemplate);
                FingerprintMatcher matcher = new FingerprintMatcher().index(probe);
                for (String strCandidate : strCandidates) {
//                    if (mCurrentDevice.verify(candidateTemplate, Base64.decode(probe, Base64.NO_WRAP))) {
//                        data.putBoolean(KEY_FINGERPRINT_MATCHED, true);
//                        break;
//                    }
                    byte[] candidateTemplate = Base64.decode(strCandidate, Base64.NO_WRAP);
                    FingerprintTemplate candidate = new FingerprintTemplate().create(candidateTemplate);
                    double score = matcher.match(candidate);
                    if (score >= 40f) {
                        data.putBoolean(KEY_FINGERPRINT_MATCHED, true);
                        break;
                    }
                }
            }
            message.setData(data);
            mHandler.sendMessage(message);
        });
    }

    private void close() {
        Log.d(TAG, "close: called");
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
            mBioMiniFactory = null;
        }
    }

    private void notifyClient(int code) {
        notifyClient(code, null);
    }

    private void notifyClient(int code, Bundle data) {
        Log.d(TAG, "notifyClient: client null? " + (mClient == null));
        if (mClient != null) {
            try {
                Message msg = Message.obtain(null, code);
                if (data != null) {
                    msg.setData(data);
                }
                mClient.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service created");
        mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        registerReceiver(mUsbPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        registerReceiver(mUsbAttachedReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: received");
        init();
        setupOptions();
        setupCaptureResponder();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service destroyed");
        unregisterReceiver(mUsbPermissionReceiver);
        unregisterReceiver(mUsbAttachedReceiver);
        close();
    }
}
