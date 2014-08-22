package org.redhelp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.redhelp.alarmmanger.NotificationAlarmManager;
import org.redhelp.session.SessionManager;

/**
 * Created by harshis on 8/10/14.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "RedHelp:BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(SessionManager.getSessionManager(context).isLoggedIn()){
                // Set the notification alarm here. if user has logged in.
                Log.i(TAG, "Setting notification alarm.");
                Long b_p_id = SessionManager.getSessionManager(context).getBPId();
                NotificationAlarmManager.setUpAlarm(context, b_p_id);
            }
        }
    }
}
