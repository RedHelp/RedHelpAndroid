package org.redhelp.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import org.redhelp.app.R;

/**
 * Created by harshis on 7/22/14.
 */
public class NotificationHelper {

    public static Notification createNotification(Context ctx, String title, String text, PendingIntent pendingIntent) {
     title = title.substring(0, Math.min(title.length(), 50));
     NotificationCompat.Builder builder= new NotificationCompat.Builder(ctx)
                                    .setContentTitle("RedHelp")
                                    .setContentText(title)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setAutoCancel(true);
        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        return notification;

    }
}
