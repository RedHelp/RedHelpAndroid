package org.redhelp.service;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by harshis on 7/21/14.
 */
public class NotificationsServiceHelper {

    private static final String notificationHashKey = "NOTIFICATION";
    private Map<String,Long> pendingRequests = new HashMap<String,Long>();
    private Context ctx;


    private static Object lock = new Object();
    private static NotificationsServiceHelper instance;

    private NotificationsServiceHelper(Context ctx){
        this.ctx = ctx.getApplicationContext();
    }

    public static NotificationsServiceHelper getInstance(Context ctx){
        synchronized (lock) {
            if(instance == null){
                instance = new NotificationsServiceHelper(ctx);
            }
        }

        return instance;
    }

    public Long getNotifications() {

        if(pendingRequests.containsKey(notificationHashKey)){
            return pendingRequests.get(notificationHashKey);
        }

        long requestId = generateRequestID();
        pendingRequests.put(notificationHashKey, requestId);

        ResultReceiver serviceCallback = new ResultReceiver(null){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                //TODO handleGetProfileResponse(resultCode, resultData);
            }

        };

      /* TODO
        Intent intent = new Intent(this.ctx, TwitterService.class);
        intent.putExtra(TwitterService.METHOD_EXTRA, TwitterService.METHOD_GET);
        intent.putExtra(TwitterService.RESOURCE_TYPE_EXTRA, TwitterService.RESOURCE_TYPE_PROFILE);
        intent.putExtra(TwitterService.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);

        this.ctx.startService(intent);*/

        return requestId;



    }

    private long generateRequestID() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }
}
