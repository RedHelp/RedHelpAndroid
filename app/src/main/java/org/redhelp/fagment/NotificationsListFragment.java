package org.redhelp.fagment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.redhelp.adapter.NotificationsAdapter;
import org.redhelp.adapter.items.ProfileItem;
import org.redhelp.adapter.items.SlidingItem;
import org.redhelp.common.GetAllNotificationsResponse;
import org.redhelp.common.NotificationCommonFields;

import java.util.SortedSet;

/**
 * Created by harshis on 7/21/14.
 */
public class NotificationsListFragment extends android.support.v4.app.ListFragment {
    public  static final String BUNDLE_NOTIFICATIONS = "bundle_notifications";

    public static NotificationsListFragment createNotificationsListFragmentInstance(GetAllNotificationsResponse allNotificationsResponse) {
        NotificationsListFragment notificationsListFragment = new NotificationsListFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_NOTIFICATIONS, allNotificationsResponse);
        notificationsListFragment.setArguments(content);
        return notificationsListFragment;
    }

    private NotificationsAdapter notificationsAdapter;


    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        GetAllNotificationsResponse getAllNotificationsResponse = null;
        SortedSet<NotificationCommonFields> notificationCommonFields = null;
        if(data_passed != null){
            try {
                getAllNotificationsResponse = (GetAllNotificationsResponse) data_passed.getSerializable(BUNDLE_NOTIFICATIONS);
                notificationCommonFields = getAllNotificationsResponse.getNotificationCommonFields();
            }catch (Exception e){
                return;
            }
        }

        if(notificationCommonFields != null) {
            notificationsAdapter = createAdapter(notificationCommonFields);
            setListAdapter(notificationsAdapter);
        }




    }

    private NotificationsAdapter createAdapter(SortedSet<NotificationCommonFields> notificationCommonFields){
        NotificationsAdapter adapter = new NotificationsAdapter(getActivity());
        for(NotificationCommonFields notification : notificationCommonFields) {
            SlidingItem slidingItem = new SlidingItem(notification.getTitle(), android.R.drawable.btn_radio);
            adapter.add(slidingItem);
        }

        return adapter;

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProfileItem profileItem = null;
        try {
            profileItem = (ProfileItem) getListAdapter().getItem(position);
        }catch (Exception e){

        }
        if(profileItem != null){
           //TODO handle notification click here.
        }

    }
}
