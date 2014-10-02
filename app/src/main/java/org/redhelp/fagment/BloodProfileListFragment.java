package org.redhelp.fagment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.redhelp.adapter.BloodProfileListViewAdapter;
import org.redhelp.adapter.items.ProfileItem;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;
import org.redhelp.session.SessionManager;
import org.redhelp.util.AndroidVersion;

import java.util.Comparator;
import java.util.Set;

/**
 * Created by harshis on 7/13/14.
 */
public class BloodProfileListFragment extends android.support.v4.app.ListFragment {
    public  static final String BUNDLE_BLOOD_PROFILE = "bundle_blood_profile";


    public static BloodProfileListFragment createBloodProfileListFragmentInstance(BloodProfileDataWrapper bloodProfileDataWrapper) {
        BloodProfileListFragment bloodProfileListFragment = new BloodProfileListFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_BLOOD_PROFILE, bloodProfileDataWrapper);
        bloodProfileListFragment.setArguments(content);
        return bloodProfileListFragment;
    }

    private BloodProfileListViewAdapter bloodProfileListViewAdapter;

    private boolean showTitle;

    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        Set<BloodProfileListData> bloodProfileListDataSet = null;
        if(data_passed != null){
            try {
                BloodProfileDataWrapper dataWrapper = (BloodProfileDataWrapper) data_passed.getSerializable(BUNDLE_BLOOD_PROFILE);
                bloodProfileListDataSet = dataWrapper.bloodProfileListDataSortedSet;
                showTitle = dataWrapper.showTitle;
            }catch (Exception e){
                return;
            }
        }

        if(bloodProfileListDataSet != null) {
            bloodProfileListViewAdapter = createAdapter(bloodProfileListDataSet);
            setListAdapter(bloodProfileListViewAdapter);
            ListView listView = getListView();
            listView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        }

        // Disable filter button
        setHasOptionsMenu(true);
        if(getActivity() instanceof HomeScreenActivity){
            ((HomeScreenActivity)getActivity()).showFilterMenu(false);
        }


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        // Set title

        if(!AndroidVersion.isBeforeHoneycomb() && showTitle)
            getActivity().getActionBar()
                    .setTitle("Donors");
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.menuid_filter);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private BloodProfileListViewAdapter createAdapter(Set<BloodProfileListData> bloodProfileListDataSet){
        BloodProfileListViewAdapter adapter = new BloodProfileListViewAdapter(getActivity());
        for(BloodProfileListData profile : bloodProfileListDataSet) {
            String blood_grp = "-";

            if(profile.b_p_id == null)
                continue;

            if(profile.blood_grp!= null)
                blood_grp = profile.blood_grp;

            byte[] pic = null;
            if(profile.profile_pic != null)
                pic = profile.profile_pic;

            String distance = "-";
            if(profile.distance != null)
                distance = profile.distance;

            ProfileItem profileItem = new ProfileItem(profile.b_p_id,
                    distance, profile.title, blood_grp, pic);
            if(profile.isRequestAccepted != null) {
                if (profile.isRequestAccepted == true) {
                    profileItem.isRequestAccepted = true;
                    profileItem.showRequestStatus = true;
                } else if (profile.isRequestAccepted == false) {
                    profileItem.isRequestAccepted = false;
                    profileItem.showRequestStatus = true;
                }
            }

            adapter.add(profileItem);
        }

        // Writing comparater based on location
        adapter.sort(new Comparator<ProfileItem>() {
            @Override
            public int compare(ProfileItem profileItem, ProfileItem profileItem2) {
                Double distanceFirstDouble = null, distanceSecondDouble = null;
                try {
                    String distanceOfFirst = profileItem.num_km_str;
                    distanceFirstDouble = Double.parseDouble(distanceOfFirst);
                    String distanceOfSecond = profileItem2.num_km_str;
                    distanceSecondDouble = Double.parseDouble(distanceOfSecond);
                } catch (Exception e) {

                }
                if(distanceFirstDouble == null)
                    return -1;
                else if(distanceSecondDouble == null)
                    return 1;
                else
                    return distanceFirstDouble.compareTo(distanceSecondDouble);
            }
        });

        return adapter;

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ProfileItem profileItem = null;
        try {
            profileItem = (ProfileItem) getListAdapter().getItem(position);
        }catch (Exception e){
            //TODO handle exception
        }
        if(profileItem != null){
            Long requester_b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();

            ViewBloodProfileFragment viewBloodProfileFragment = ViewBloodProfileFragment.
                    createViewBloodProfileFragmentInstance(requester_b_p_id, profileItem.b_p_id);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame_main_screen, viewBloodProfileFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

    }
}
