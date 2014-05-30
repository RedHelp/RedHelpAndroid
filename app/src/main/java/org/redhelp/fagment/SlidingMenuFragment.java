package org.redhelp.fagment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.redhelp.app.R;

/**
 * Created by harshis on 5/24/14.
 */
public class SlidingMenuFragment extends Fragment {

    private static final String TAG = "SlidingMenuFragment";

    private ListView mMenuListView = null;
    private ListView mNotificationListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_menu_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMenuListView = (ListView) getActivity().findViewById(R.id.menu_list);
        mNotificationListView = (ListView) getActivity().findViewById(R.id.list_notifications);
        SlidingMenuAdapter mSlidingMenuAdapter = new SlidingMenuAdapter(getActivity());
        mSlidingMenuAdapter.add(new SlidingItem("My Account", R.drawable.profile_icon));
        mSlidingMenuAdapter.add(new SlidingItem("Blood Profile and History", R.drawable.blood_profile));
        mSlidingMenuAdapter.add(new SlidingItem("About", R.drawable.about));

        NotificationsAdapter mNotificationsAdapter = new NotificationsAdapter(getActivity());
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));
        mNotificationsAdapter.add(new SlidingItem("Blood Request Ad: user123 has requested blood from you, Click here to view request", android.R.drawable.btn_radio));

        mMenuListView.setAdapter(mSlidingMenuAdapter);
        mNotificationListView.setAdapter(mNotificationsAdapter);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if(position == 0){
                    //startActivity(new Intent(getActivity(),MyAccountActivity.class));
                }
                if(position == 1) {
                    //startActivity(new Intent(getActivity(), BloodProfileAfterRegisterActivity.class));
                }
                if(position == 2) {

                }

            }
        });
    }

    private class SlidingItem {
        public String tag;
        public int iconRes;
        public SlidingItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }
    private class SlidingMenuAdapter extends ArrayAdapter<SlidingItem> {

        public SlidingMenuAdapter(Context context) {
            super(context, 0);
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_sliding_menu_list, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setTextSize(15);
            title.setText(getItem(position).tag);

            return convertView;
        }

    }
    public class NotificationsAdapter extends ArrayAdapter<SlidingItem> {

        public NotificationsAdapter(Context context) {
            super(context, 0);
        }



        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_sliding_menu_notification_list, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setTextSize(10);
            //title.setTextColor(000000);
            title.setText(getItem(position).tag);

            return convertView;
        }

    }
}
