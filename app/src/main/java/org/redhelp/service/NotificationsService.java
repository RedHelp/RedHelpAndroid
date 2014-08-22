package org.redhelp.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;

import org.redhelp.app.HomeScreenActivity;
import org.redhelp.common.GetNewNotificationsResponse;
import org.redhelp.common.MarkNotificaionAsReadRequest;
import org.redhelp.common.NotificationCommonFields;
import org.redhelp.common.types.GetNewNotificationsResponseType;
import org.redhelp.common.types.NotificationTypes;
import org.redhelp.network.RestClientCall;
import org.redhelp.task.MarkNotificationAsReadTask;
import org.redhelp.util.NotificationHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by harshis on 7/21/14.
 */
public class NotificationsService extends IntentService {

    private String TAG = "RedHelp-NotificationService";
    private Intent mOriginalRequestIntent;
    private ResultReceiver mCallback;

    public static final String SERVICE_CALLBACK = "com.jeremyhaberman.restfulandroid.service.SERVICE_CALLBACK";

    public static final String BUNDLE_B_P_ID = "b_p_id";


    public NotificationsService() {
        super("NotificationsService");
    }

    @Override
    protected void onHandleIntent(Intent requestIntent) {
        Log.i(TAG, "NotificationService called !, fetching new notifications..");
        mOriginalRequestIntent = requestIntent;
        Long b_p_id = null;
        if (requestIntent !=null && requestIntent.getExtras()!=null)
            b_p_id = requestIntent.getExtras().getLong(BUNDLE_B_P_ID);
        if(b_p_id == null)
            return;

        String json_response = null;
        try{
            json_response = RestClientCall.getCall("/notification/new/" + b_p_id);
        }catch(Exception e) {
            Log.e(TAG, "Exception while getting new notifications, Exception dump:"+e.toString());
            return;
        }

        if(json_response != null) {
            Gson gson = new Gson();
            GetNewNotificationsResponse response = null;
            try {
                response = gson.fromJson(json_response, GetNewNotificationsResponse.class);
                handleResponse(response);
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                return;
            }
        }
    }

    private void handleResponse(GetNewNotificationsResponse response) {

        if(response.getResponseType().equals(GetNewNotificationsResponseType.NO_NEW_NOTIFICATIONS)
            || response.getResponseType().equals(GetNewNotificationsResponseType.UNSUCCESSFUL))
            return;
        else if(response.getResponseType().equals(GetNewNotificationsResponseType.SUCCESSFUL)) {
            SortedSet<NotificationCommonFields> notifications = response.getNotificationCommonFields();
            int num_of_notifications = notifications.size();
            if(num_of_notifications > 0) {
                List<Long> n_id_list = new LinkedList<Long>();
                for(NotificationCommonFields notification : notifications) {
                    showNotification(notification);
                    n_id_list.add(notification.getN_id());
                }
                if(!n_id_list.isEmpty()) {
                    MarkNotificaionAsReadRequest markNotificaionAsReadRequest = new MarkNotificaionAsReadRequest();
                    markNotificaionAsReadRequest.setN_id_list(n_id_list);

                    MarkNotificationAsReadTask asReadTask = new MarkNotificationAsReadTask(getApplicationContext());
                    asReadTask.execute(markNotificaionAsReadRequest);
                }
            }
        }
    }

    private void showNotification(NotificationCommonFields notification) {
        PendingIntent pendingIntent = getPendingIntent(notification);
        Notification not = NotificationHelper.createNotification(this, notification.getTitle(), notification.getCreation_datetime(), pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,not);
    }

    private PendingIntent getPendingIntent(NotificationCommonFields notification) {
        PendingIntent pi = null;
        Intent intent = null;
        if(NotificationTypes.MISC_NOTIFICATION.equals(notification.getNotification_type())) {
            intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        } else if(NotificationTypes.BLOOD_REQUEST_RECEIVED_NOTIFICATION.equals(notification.getNotification_type())) {
            Long b_r_id = notification.getB_r_id();
            if(b_r_id == null)
                return null;
            Bundle data_to_pass = new Bundle();
            data_to_pass.putLong(HomeScreenActivity.BUNDLE_B_R_ID, b_r_id);
            data_to_pass.putString(HomeScreenActivity.HOMESCREEN_FRAGMENT, "VIEW_BLOOD_REQUEST");

            intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            intent.putExtras(data_to_pass);
        }
        if(intent != null)
            pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        return pi;
    }


}
