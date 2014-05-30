package org.redhelp.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by harshis on 5/19/14.
 */

public class SessionManager {

    public static SessionManager session_manager = null;

    public static SessionManager getSessionManager(Context ctx) {
        if(session_manager == null) {
            session_manager = new SessionManager(ctx);
        }
        return session_manager;
    }

    public void changeContext(Context ctx) {
        this._context = ctx;
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
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email_id";
    // User_id given by server
    public static final String KEY_U_ID = "u_id";
    public static final String KEY_PHONE = "phone_no";
    public static final String KEY_BP_ID = "b_p_id";
    public static final String KEY_UAA_ID = "uaa_id";
    public static final String KEY_TOKEN = "fb_token";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public boolean createLoginSession(String name, String email, Long user_id, String phone_no,
                    Long uaa_id){

        if(user_id == null || email == null || name == null)
            return false;

        editor.putLong(KEY_U_ID, user_id);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);

        if(uaa_id != null)
            editor.putLong(KEY_UAA_ID, uaa_id);
        if(phone_no != null)
            editor.putString(KEY_PHONE, phone_no);

        // commit changes
        editor.commit();
        return true;
    }


    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }


    public Long getUid() {
        if(!isLoggedIn())
            return null;
        else return  pref.getLong(KEY_U_ID, 0);
    }
}