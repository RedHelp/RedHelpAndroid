package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

import org.redhelp.adapter.BloodProfileListViewAdapter;
import org.redhelp.adapter.items.ProfileItem;
import org.redhelp.app.R;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;
import org.redhelp.session.SessionManager;

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


    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        Set<BloodProfileListData> bloodProfileListDataSet = null;
        if(data_passed != null){
            try {
                BloodProfileDataWrapper dataWrapper = (BloodProfileDataWrapper) data_passed.getSerializable(BUNDLE_BLOOD_PROFILE);
                bloodProfileListDataSet = dataWrapper.bloodProfileListDataSortedSet;
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
    }

    private BloodProfileListViewAdapter createAdapter(Set<BloodProfileListData> bloodProfileListDataSet){
        BloodProfileListViewAdapter adapter = new BloodProfileListViewAdapter(getActivity());
        for(BloodProfileListData profile : bloodProfileListDataSet) {
            String blood_grp = "-";
            if(profile.blood_grp!= null)
                blood_grp = profile.blood_grp;

            byte[] pic = null;
            if(profile.profile_pic != null)
                pic = profile.profile_pic;

            ProfileItem profileItem = new ProfileItem(profile.b_p_id,
                    "2.0", profile.title,blood_grp, pic);
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
