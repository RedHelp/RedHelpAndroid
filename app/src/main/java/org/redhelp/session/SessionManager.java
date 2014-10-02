package org.redhelp.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.Session;

import org.redhelp.alarmmanger.NotificationAlarmManager;
import org.redhelp.fagment.HomeFragment;

/**
 * Created by harshis on 5/19/14.
 */

public class SessionManager {
    private static final String TAG = "RedhHelp:SessionManager";

    public static SessionManager session_manager = null;

    public static SessionManager getSessionManager(Context ctx) {
        if (session_manager == null) {
            session_manager = new SessionManager(ctx);
        }
        return session_manager;
    }

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    public SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "RedHelpUserPref";

    public static final String KEY_U_ID = "u_id";
    public static final String KEY_B_P_ID = "b_p_id";
    public static final String KEY_LOGIN_STATE = "login_state";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    //LoginStates - {NOT_LOGGED_IN(0), USER_ACCOUNT_CREATED(1), LOGGED_IN(2)};
    public boolean updateLoginState(int desiredLoginState, Long u_id, Long b_p_id) {
        int currentLoginState = pref.getInt(KEY_LOGIN_STATE, 0);
        Log.i(TAG, "updateLoginState: currentstate = "+currentLoginState+"desired state= "+desiredLoginState);
        if (desiredLoginState == 1 && u_id != null) {
            editor.putInt(KEY_LOGIN_STATE, 1);
            editor.putLong(KEY_U_ID, u_id);
            editor.commit();
            return true;
        } else if (desiredLoginState == 2 && currentLoginState == 1 && b_p_id != null) {
            editor.putInt(KEY_LOGIN_STATE, 2);
            editor.putLong(KEY_B_P_ID, b_p_id);
            editor.commit();

            NotificationAlarmManager.setUpAlarm(_context, b_p_id);
            return true;
        } else if (desiredLoginState == 2 && currentLoginState == 0 && b_p_id != null && u_id != null) {
            editor.putInt(KEY_LOGIN_STATE, 2);
            editor.putLong(KEY_U_ID, u_id);
            editor.putLong(KEY_B_P_ID, b_p_id);
            editor.commit();

            NotificationAlarmManager.setUpAlarm(_context, b_p_id);
            return true;
        } else if (desiredLoginState == 0 && (currentLoginState == 1 || currentLoginState == 2)) {
            editor.putInt(KEY_LOGIN_STATE, 0);
            editor.clear();
            editor.commit();
            return true;
        }

        return false;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        updateLoginState(0, null, null);

        //Clear facebook session
        Session fbSession = Session.getActiveSession();
        if(fbSession == null) {
            // try to restore from cache
            fbSession = Session.openActiveSessionFromCache(_context);
        }
        if(fbSession != null) {
            Log.d(TAG, "Clearing fb Session");
            fbSession.closeAndClearTokenInformation();
        }
        if(HomeFragment.cachedSearchResponse != null)
            HomeFragment.cachedSearchResponse = null;
    }

    /**
     * Quick check for login
     * *
     */
    public boolean isLoggedIn() {
        int currentLoginState = pref.getInt(KEY_LOGIN_STATE, 0);
        if(currentLoginState == 2)
            return true;
        else return false;
    }

    public int getCurrentLoginState() {
        return pref.getInt(KEY_LOGIN_STATE, 0);
    }


    public Long getUid() {
        return pref.getLong(KEY_U_ID, 0);
    }

    public Long getBPId() {
        if (!isLoggedIn())
            return null;
        else return pref.getLong(KEY_B_P_ID, 0);
    }

    public void sessionManagerDump() {
        Log.i(TAG, "isLoggedIn ? :"+isLoggedIn());
        Log.i(TAG, "u_id ? :"+getUid());
        Log.i(TAG, "b_p_id ? :"+getBPId());
    }
}