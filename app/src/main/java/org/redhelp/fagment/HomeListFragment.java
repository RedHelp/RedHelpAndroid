package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.redhelp.app.R;
import org.redhelp.common.BloodProfileSearchResponse;
import org.redhelp.common.BloodRequestSearchResponse;
import org.redhelp.common.EventSearchResponse;
import org.redhelp.common.SearchResponse;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by harshis on 7/11/14.
 */
public class HomeListFragment extends Fragment {
    public static final String BUNDLE_SEARCH_DATA = "SearchResult";


    public static HomeListFragment createHomeListFragmentInstance(SearchResponse searchResponse) {
        HomeListFragment homeListFragment =  new HomeListFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_SEARCH_DATA, searchResponse);
        homeListFragment.setArguments(content);
        return homeListFragment;
    }

    private TextView tv_no_events;
    private TextView tv_no_requests;
    private TextView tv_no_donors;

    private LinearLayout ll_no_events;
    private LinearLayout ll_no_requests;
    private LinearLayout ll_no_donors;

    private Button bt_post_blood_request;

    private static View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_list_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
    }

    private void initializeViews() {
        tv_no_events = (TextView) getActivity().findViewById(R.id.tv_no_of_events_home_list_layout);
        tv_no_requests = (TextView) getActivity().findViewById(R.id.tv_no_of_requests_home_list_layout);
        tv_no_donors = (TextView) getActivity().findViewById(R.id.tv_no_of_donors_home_list_layout);

        ll_no_events = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_event_home_list_layout);
        ll_no_requests = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_blood_request_home_list_layout);
        ll_no_donors = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_blood_profiles_home_list_layout);

        bt_post_blood_request = (Button) getActivity().findViewById(R.id.bt_post_blood_request_home_list_layout);
        if(bt_post_blood_request != null)
        bt_post_blood_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
              //  NotificationAlarmManager.setUpAlarm(getActivity().getApplicationContext(), b_p_id);

                //TODO uncomment this.
                Fragment createBloodRequestFragment = new CreateBloodRequestFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, createBloodRequestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle data_passed = getArguments();
        SearchResponse searchResponse = null;
        if(data_passed != null){
            try {
                searchResponse = (SearchResponse) data_passed.getSerializable(BUNDLE_SEARCH_DATA);
            }catch (Exception e){
                return;
            }
        }

        showData(searchResponse);

    }

    public void showData(final SearchResponse searchResponse) {
        if(searchResponse == null)
            return;

        Set<EventSearchResponse> eventSearchResponse = searchResponse.getSet_events();

        Set<BloodProfileSearchResponse> bloodProfileSearchResponse = searchResponse.getSet_blood_profiles();

        Set<BloodRequestSearchResponse> bloodRequestSearchResponse = searchResponse.getSet_blood_requests();

        int no_of_events = (eventSearchResponse != null) ? eventSearchResponse.size() : 0 ;
        String base_str_events = (no_of_events > 0) ?  ((no_of_events == 1) ? "%d Event In Your Area" : "%d Events In Your Area") : "Currently there are no events in your area !";
        String no_of_events_str = String.format(base_str_events, no_of_events);

        int no_of_requests = (bloodRequestSearchResponse != null) ? bloodRequestSearchResponse.size() : 0 ;
        String base_str_requests = (no_of_requests > 0) ?  ((no_of_requests == 1) ? "%d Request Near You" : "%d Requests Near You") : "Currently there are no requests in your area !";
        String no_of_requests_str = String.format(base_str_requests, no_of_requests);

        int no_of_donors = (bloodProfileSearchResponse != null) ? bloodProfileSearchResponse.size() : 0 ;
        String base_str_donors = (no_of_donors > 0) ?  ((no_of_donors == 1) ? "%d Donor Near You" : "%d Donors Near You") : "Currently there are no donors in your area !";
        String no_of_donors_str = String.format(base_str_donors, no_of_donors);

        tv_no_events.setText(no_of_events_str);
        tv_no_donors.setText(no_of_donors_str);
        tv_no_requests.setText(no_of_requests_str);


        ll_no_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventsListFragment viewEventFragment =  EventsListFragment.createEventsListFragmentInstance(searchResponse);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, viewEventFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ll_no_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BloodRequestListFragment bloodRequestListFragment =  BloodRequestListFragment.createBloodRequestListFragmentInstance(searchResponse);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, bloodRequestListFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        ll_no_donors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BloodProfileListFragment bloodProfileListFragment =  BloodProfileListFragment.createBloodProfileListFragmentInstance(
                        getBloodProfileDataWrapperFromSearchResponse(searchResponse));
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, bloodProfileListFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });

    }

    private BloodProfileDataWrapper getBloodProfileDataWrapperFromSearchResponse(SearchResponse searchResponse) {
        BloodProfileDataWrapper dataWrapper = new BloodProfileDataWrapper();
        Set<BloodProfileListData> bloodProfileListDataSortedSet = new HashSet<BloodProfileListData>();
        for(BloodProfileSearchResponse bloodProfileSearchResponse:searchResponse.getSet_blood_profiles()){
            BloodProfileListData bloodProfileListData = new BloodProfileListData();
            bloodProfileListData.b_p_id = bloodProfileSearchResponse.getB_p_id();
            bloodProfileListData.profile_pic = bloodProfileSearchResponse.getUser_image();
            bloodProfileListData.blood_grp = bloodProfileSearchResponse.getBlood_grp();
            bloodProfileListData.title = bloodProfileSearchResponse.getTitle();

            bloodProfileListDataSortedSet.add(bloodProfileListData);
        }
        dataWrapper.bloodProfileListDataSortedSet = bloodProfileListDataSortedSet;
       return dataWrapper;
    }

}
