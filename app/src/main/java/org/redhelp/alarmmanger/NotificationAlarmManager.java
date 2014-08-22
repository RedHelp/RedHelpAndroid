package org.redhelp.alarmmanger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.redhelp.service.NotificationsService;

/**
 * Created by harshis on 8/10/14.
 */
public class NotificationAlarmManager {

    private static Long INTERVAL_MILISEC = 30000l;

    private static PendingIntent getPendingIntent(Context context, Long b_p_id) {
        Intent serviceIntent = new Intent(context, NotificationsService.class);
        serviceIntent.putExtra(NotificationsService.BUNDLE_B_P_ID, b_p_id);

        PendingIntent pending_intent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return  pending_intent;
    }

    private static boolean isAlarmAlreadyScheduled(){ return  true;}
    /**
     * Set up recurring location updates using AlarmManager
     */
    public static void setUpAlarm(Context context, Long b_p_id) {

        AlarmManager alarm_mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), INTERVAL_MILISEC, getPendingIntent(context, b_p_id));
    }
}
