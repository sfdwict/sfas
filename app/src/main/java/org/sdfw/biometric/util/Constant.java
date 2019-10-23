package org.sdfw.biometric.util;

public class Constant {

//    public static final String BASE_URL = "http://192.168.31.79:8082/";
//    public static final String BASE_URL = "http://116.193.221.37:8080/shakti-foundation-test/";
//    public static final String BASE_URL = "http://116.193.221.37:8081/shakti-foundation/";
    //public static final String BASE_URL = "http://sfas.sfdw.org:8083/shakti-foundation-demo/";
   // public static final String BASE_URL = "http://116.193.221.37:8083/shakti-foundation-demo/";
   public static final String BASE_URL = "http://192.168.2.37:8083/shakti-foundation-demo/";

    public static final String TAG = "FINGERPRINT";
    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    public static final int MSG_NO_DEVICE = 3;
    public static final int MSG_HAS_PERMISSION = 4;
    public static final int MSG_CAPTURE_FINGERPRINT = 5;
    public static final int MSG_FINGERPRINT_RECEIVED = 6;
    public static final int MSG_CAPTURE_ERROR = 7;
    public static final int MSG_VERIFY_FINGERPRINT = 8;
    public static final int MSG_VERIFICATION_COMPLETE = 9;
    public static final int MSG_DEVICE_DISCONNECTED = 10;

    public static final int CODE_MEMBER_FINGERPRINT = 263;

    public static final String KEY_PREF_SESSION = "pref_session";
    public static final String KEY_PREF_EIN = "pref_user_id";
    public static final String KEY_PREF_TOKEN = "pref_access_token";
    public static final String KEY_PREF_OPENING_DATE = "pref_opening_date";
    public static final String KEY_PREF_LAST_ACCESS_TIME = "pref_last_access_time";
    public static final String KEY_PREF_LAST_CLEANUP_DATE = "pref_last_cleanup_date";

    public static final String KEY_HAS_DEVICE = "has_device";
    public static final String KEY_HAS_PERMISSION = "has_permission";
    public static final String KEY_CODE = "code";
    public static final String KEY_SCORE = "score";
    public static final String KEY_BITMAP = "bitmap";
    public static final String KEY_TEMPLATE = "template";
    public static final String KEY_CANDIDATE = "candidate";
    public static final String KEY_PROBE = "probe";
    public static final String KEY_SHOW_DIALOG = "show_dialog";
    public static final String KEY_FINGERPRINT_MATCHED = "fingerprint_matched";
    public static final String KEY_EIN = "user_id";

    public static final String KEY_INTENT_USER_ID = "user_id";
    public static final String KEY_INTENT_BRANCH_ID = "branch_id";
    public static final String KEY_INTENT_CENTER_ID = "center_id";
    public static final String KEY_INTENT_MEMBER_ID = "member_id";
    public static final String KEY_INTENT_REGISTER_MEMBER_FP = "register_member_fp";

    public static final String CHANNEL_ID_OFFLINE_SYNC = "offline_sync";

}
