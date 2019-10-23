package org.sdfw.biometric.util;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static org.sdfw.biometric.util.Constant.KEY_PREF_EIN;
import static org.sdfw.biometric.util.Constant.KEY_PREF_LAST_ACCESS_TIME;
import static org.sdfw.biometric.util.Constant.KEY_PREF_OPENING_DATE;
import static org.sdfw.biometric.util.Constant.KEY_PREF_TOKEN;
import static org.sdfw.biometric.util.Constant.TAG;

public class SessionManager {

    private static final int IDLE_TIME = 300000;

    private SharedPreferences sharedPreferences;

    public SessionManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isValidSession() {
        long lastAccessTime = sharedPreferences.getLong(KEY_PREF_LAST_ACCESS_TIME, 0L);
        if (lastAccessTime == 0L) {
            return true;
        }
        Log.d(TAG, "isValidSession: " + (System.currentTimeMillis() - lastAccessTime));
        clearLastAccessTime();
        return System.currentTimeMillis() - lastAccessTime <= IDLE_TIME;
    }

    public void logLastAccessTime() {
        Log.d(TAG, "logLastAccessTime: " + System.currentTimeMillis());
        sharedPreferences.edit()
                .putLong(KEY_PREF_LAST_ACCESS_TIME, System.currentTimeMillis())
                .apply();
    }

    private void clearLastAccessTime() {
        sharedPreferences.edit()
                .remove(KEY_PREF_LAST_ACCESS_TIME)
                .apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_PREF_EIN, "");
    }

    public String getUserId(String defaultValue) {
        return sharedPreferences.getString(KEY_PREF_EIN, defaultValue);
    }

    public String getAccessToken() {
        String userId = getUserId(null);
        String strTokens = sharedPreferences.getString(KEY_PREF_TOKEN, null);
        if (userId != null && strTokens != null) {
            try {
                JSONObject tokens = new JSONObject(strTokens);
                return tokens.getString(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getAccessToken(String defaultValue) {
        String token = getAccessToken();
        return !token.isEmpty() ? token : defaultValue;
    }

    public String getDayOpening() {
        String userId = getUserId(null);
        String strDayOpenings = sharedPreferences.getString(KEY_PREF_OPENING_DATE, null);
        if (userId != null && strDayOpenings != null) {
            try {
                JSONObject dayOpenings = new JSONObject(strDayOpenings);
                return dayOpenings.getString(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getDayOpening(String defaultValue) {
        return sharedPreferences.getString(KEY_PREF_OPENING_DATE, defaultValue);
    }

    @SuppressLint("ApplySharedPref")
    public void setUserId(String userId) {
        sharedPreferences.edit().putString(KEY_PREF_EIN, userId).commit();
    }

    @SuppressLint("ApplySharedPref")
    public void setUserIdAndAccessToken(String userId, String token) {
        String strTokenMap = generateAccessTokenMap(userId, token);
        sharedPreferences.edit()
                .putString(KEY_PREF_EIN, userId)
                .putString(KEY_PREF_TOKEN, strTokenMap)
                .commit();
    }

    @SuppressLint("ApplySharedPref")
    public void setDayOpening(String dayOpening) {
        String userId = getUserId(null);
        if (userId != null) {
            String strDayOpeningMap = generateDayOpeningMap(userId, dayOpening);
            sharedPreferences.edit().putString(KEY_PREF_OPENING_DATE, strDayOpeningMap).commit();
        }
    }

    private String generateAccessTokenMap(String userId, String token) {
        String strTokens = sharedPreferences.getString(KEY_PREF_TOKEN, null);
        try {
            JSONObject tokens;
            if (strTokens != null) {
                tokens = new JSONObject(strTokens);
                tokens.put(userId, token);
            } else {
                tokens = new JSONObject();
                tokens.put(userId, token);
            }
            return tokens.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateDayOpeningMap(String userId, String dayOpening) {
        String strDayOpenings = sharedPreferences.getString(KEY_PREF_OPENING_DATE, null);
        try {
            JSONObject dayOpenings;
            if (strDayOpenings != null) {
                dayOpenings = new JSONObject(strDayOpenings);
                dayOpenings.put(userId, dayOpening);
            } else {
                dayOpenings = new JSONObject();
                dayOpenings.put(userId, dayOpening);
            }
            return dayOpenings.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
