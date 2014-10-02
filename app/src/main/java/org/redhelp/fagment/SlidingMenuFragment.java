package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.redhelp.adapter.SlidingMenuAdapter;
import org.redhelp.adapter.items.SlidingItem;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.GetAllNotificationsResponse;
import org.redhelp.session.SessionManager;
import org.redhelp.task.GetNotificationsAsyncTask;

/**
 * Created by harshis on 5/24/14.
 */
public class SlidingMenuFragment extends Fragment
        implements GetNotificationsAsyncTask.IGetNotificationsTaskResultListener{

    private static final String TAG = "SlidingMenuFragment";
    public static int currentMenuButtonPos = 0;

    private ListView mMenuListView = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_menu_layout, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMenuListView = (ListView) getActivity().findViewById(R.id.menu_list);

        SlidingMenuAdapter mSlidingMenuAdapter = new SlidingMenuAdapter(getActivity());

        mSlidingMenuAdapter.add(new SlidingItem(getResources().getString(R.string.menu_home_slidingmenu), R.drawable.about));
        mSlidingMenuAdapter.add(new SlidingItem(getResources().getString(R.string.myaccount_slidingmenu), R.drawable.profile_icon));
        mSlidingMenuAdapter.add(new SlidingItem(getResources().getString(R.string.bloodprofile_slidingmenu), R.drawable.blood_profile));


        SessionManager sessionManager = SessionManager.getSessionManager(getActivity());
        Long b_p_id = sessionManager.getBPId();
        if(b_p_id != null) {
            GetNotificationsAsyncTask getNotificationsAsyncTask = new GetNotificationsAsyncTask(getActivity(), this);
            getNotificationsAsyncTask.execute(b_p_id);
        }


        mMenuListView.setAdapter(mSlidingMenuAdapter);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                handleSliddingButtonsClick(position);

            }
        });
    }

    @Override
    public void handleNotificationsResult(GetAllNotificationsResponse notificationsResponse) {
        try {
            NotificationsListFragment notificationsListFragment = NotificationsListFragment.createNotificationsListFragmentInstance(notificationsResponse);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_list_notifications, notificationsListFragment);
            transaction.commit();
        } catch (Exception exp){

        }
    }

    @Override
    public void handleError(Exception e) {

    }

    private void handleSliddingButtonsClick(int desiredPosition) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        int backStackCount = fm.getBackStackEntryCount();
        //Return if backStackCount is zero and desiredPos is eq to currentPos
        if(currentMenuButtonPos == desiredPosition && backStackCount == 0) {
            if (getActivity() == null)
                return;

            if (getActivity() instanceof HomeScreenActivity) {
                HomeScreenActivity ra = (HomeScreenActivity) getActivity();
                ra.toggleSliddingMenu();
            }
            return;
        } else {
            currentMenuButtonPos = desiredPosition;

            Fragment newContent = null;
            String tag = null;

            if (desiredPosition == 0) {
                tag = HomeFragment.HOME_TAG;
                newContent = new HomeFragment();
            }
            else if (desiredPosition == 1) {
                tag = MyAccountFragment.MY_ACCOUNT_TAG;
                newContent = MyAccountFragment.getMyAccountFragmentInstance(SessionManager.getSessionManager(getActivity()).getUid());
            }
            else if (desiredPosition == 2) {
                tag = ViewBloodProfileFragment.VIEW_BP_TAG;
                Long user_b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
                newContent = ViewBloodProfileFragment.createViewBloodProfileFragmentInstance(user_b_p_id, user_b_p_id);
            }

            if (newContent != null)
                switchFragment(newContent, tag);
        }
    }

    // the meat of switching the above fragment
    private void switchFragment(Fragment fragment, String tag) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof HomeScreenActivity) {
            HomeScreenActivity ra = (HomeScreenActivity) getActivity();
            ra.switchContent(fragment, tag);
        }
    }
}
