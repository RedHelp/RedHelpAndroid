package org.redhelp.app;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import org.redhelp.service.NotificationsServiceHelper;

/**
 * Created by harshis on 7/21/14.
 */
public class NotificationsListActivity extends RESTfulListActivity {
    private static final String TAG = NotificationsListActivity.class.getSimpleName();

    private Long requestId;
    private BroadcastReceiver requestReceiver;

    private NotificationsServiceHelper mNotificationsServiceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentResId(R.layout.activity_notifications_list_layout);
        setRefreshable(true);

        super.onCreate(savedInstanceState);
    }


    @Override
    protected void refresh() {
        requestId = mNotificationsServiceHelper.getNotifications();
    }

    private void showNameToast(String name) {
        showToast("You are logged in as\n" + name);
    }
    private void showToast(String message) {
        if (!isFinishing()) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }




}
