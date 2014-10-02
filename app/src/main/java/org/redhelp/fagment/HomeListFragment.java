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

import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.BloodProfileSearchResponse;
import org.redhelp.common.BloodRequestSearchResponse;
import org.redhelp.common.EventSearchResponse;
import org.redhelp.common.SearchResponse;
import org.redhelp.common.types.SearchItemTypes;
import org.redhelp.common.types.SearchRequestType;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;
import org.redhelp.data.BloodRequestDataWrapper;
import org.redhelp.data.BloodRequestListData;
import org.redhelp.data.EventDataWrapper;
import org.redhelp.data.EventListData;
import org.redhelp.data.SearchPrefData;
import org.redhelp.util.LocationHelper;

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
    private TextView tv_nothing_selected;

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

        tv_nothing_selected = (TextView) getActivity().findViewById(R.id.tv_nothing_selected_home_list_layout);

        ll_no_events = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_event_home_list_layout);
        ll_no_requests = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_blood_request_home_list_layout);
        ll_no_donors = (LinearLayout) getActivity().findViewById(R.id.ll_clickable_blood_profiles_home_list_layout);

        bt_post_blood_request = (Button) getActivity().findViewById(R.id.bt_post_blood_request_home_list_layout);
        if(bt_post_blood_request != null)
        bt_post_blood_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        SearchResponse searchResponse = null;
        Bundle data_passed = getArguments();

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

        SearchPrefData searchPrefData = ((HomeScreenActivity) getActivity()).searchPrefData;
        boolean isEverywhere = false;

        isEverywhere = SearchRequestType.ALL.equals(searchPrefData.getSearchRequestType());

        int no_of_events = (eventSearchResponse != null) ? eventSearchResponse.size() : 0 ;
        String base_str_events = (isEverywhere == true) ? ((no_of_events > 0) ?  ((no_of_events == 1)
                ? "%d Event right now"
                : "%d Events right now")
                : "No events right now!")
                : ((no_of_events > 0) ?  ((no_of_events == 1) ? "%d Event In Your Area" : "%d Events In Your Area")
                : "No events in your area !");
        String no_of_events_str = String.format(base_str_events, no_of_events);

        int no_of_requests = (bloodRequestSearchResponse != null) ? bloodRequestSearchResponse.size() : 0 ;
        String base_str_requests = (isEverywhere == true) ? ((no_of_requests > 0) ?  ((no_of_requests == 1)
                ? "%d Request right now" : "%d Requests right now")
                : "No requests right now!") :
                ((no_of_requests > 0) ?  ((no_of_requests == 1) ? "%d Request Near You" : "%d Requests Near You")
                : "No requests in your area !");
        String no_of_requests_str = String.format(base_str_requests, no_of_requests);

        int no_of_donors = (bloodProfileSearchResponse != null) ? bloodProfileSearchResponse.size() : 0 ;
        String base_str_donors = (isEverywhere == true) ?((no_of_donors > 0) ?  ((no_of_donors == 1)
                ? "%d Donor available" : "%d Donors available")
                : "No donors available!")
                :((no_of_donors > 0) ?  ((no_of_donors == 1) ? "%d Donor Near You"
                : "%d Donors Near You") : "No donors in your area !");
        String no_of_donors_str = String.format(base_str_donors, no_of_donors);

        tv_no_events.setText(no_of_events_str);
        tv_no_donors.setText(no_of_donors_str);
        tv_no_requests.setText(no_of_requests_str);

        if(getActivity() instanceof  HomeScreenActivity) {
            SearchPrefData prefData = ((HomeScreenActivity) getActivity()).searchPrefData;
            Set<SearchItemTypes> searchItemTypes = prefData.getSearchItemTypes();
            boolean someThingShown = false;
            if(!searchItemTypes.contains(SearchItemTypes.EVENTS))
                ll_no_events.setVisibility(View.GONE);
            else
                someThingShown = true;
            if(!searchItemTypes.contains(SearchItemTypes.BLOOD_PROFILE))
                ll_no_donors.setVisibility(View.GONE);
            else
                someThingShown = true;
            if(!searchItemTypes.contains(SearchItemTypes.BLOOD_REQUEST))
                ll_no_requests.setVisibility(View.GONE);
            else
                someThingShown = true;

            if(someThingShown == false)
                tv_nothing_selected.setVisibility(View.VISIBLE);


        }

        ll_no_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventsListFragment viewEventFragment =  EventsListFragment.createEventsListFragmentInstance(
                        getEventDataWrapperFromSearchResponse(searchResponse));
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, viewEventFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ll_no_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BloodRequestListFragment bloodRequestListFragment =  BloodRequestListFragment.createBloodRequestListFragmentInstance(
                        getBloodRequestDataWrapperFromSearchResponse(searchResponse));
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

            if(bloodProfileSearchResponse.getLast_updated_location() != null && LocationHelper.userCurrentLocationLat != null && LocationHelper.userCurrentLocationLng != null) {
                Double bloodProfileLocationLat = bloodProfileSearchResponse.getLast_updated_location().latitude;
                Double bloodProfileLocationLng = bloodProfileSearchResponse.getLast_updated_location().longitude;
                String distance = LocationHelper.calculateDistanceString(LocationHelper.userCurrentLocationLat, LocationHelper.userCurrentLocationLng,
                        bloodProfileLocationLat, bloodProfileLocationLng);
                bloodProfileListData.distance = distance;
            }

            bloodProfileListDataSortedSet.add(bloodProfileListData);
        }
        dataWrapper.bloodProfileListDataSortedSet = bloodProfileListDataSortedSet;
        dataWrapper.showTitle = true;
       return dataWrapper;
    }

    private BloodRequestDataWrapper getBloodRequestDataWrapperFromSearchResponse(SearchResponse searchResponse) {
        BloodRequestDataWrapper dataWrapper = new BloodRequestDataWrapper();
        Set<BloodRequestListData> bloodProfileListDatas = new HashSet<BloodRequestListData>();
        for(BloodRequestSearchResponse bloodRequestSearchResponse:searchResponse.getSet_blood_requests()){
            BloodRequestListData bloodRequestListData = new BloodRequestListData();

            bloodRequestListData.b_r_id = bloodRequestSearchResponse.getB_r_id();
            bloodRequestListData.title = bloodRequestSearchResponse.getTitle();
            bloodRequestListData.venueStr = bloodRequestSearchResponse.getVenue();
            bloodRequestListData.bloodGroupsStr = bloodRequestSearchResponse.getBlood_grps_requested_str();

            if(bloodRequestSearchResponse.getlocation() != null && LocationHelper.userCurrentLocationLat != null && LocationHelper.userCurrentLocationLng != null) {
                Double bloodRequestLocationLat = bloodRequestSearchResponse.getlocation().latitude;
                Double bloodRequestLocationLng = bloodRequestSearchResponse.getlocation().longitude;
                String distance = LocationHelper.calculateDistanceString(LocationHelper.userCurrentLocationLat, LocationHelper.userCurrentLocationLng,
                        bloodRequestLocationLat, bloodRequestLocationLng);
                bloodRequestListData.distance = distance;
            }

            bloodProfileListDatas.add(bloodRequestListData);
        }
        dataWrapper.bloodRequestListDataList = bloodProfileListDatas;
        dataWrapper.showTitle = true;
        return dataWrapper;
    }

    private EventDataWrapper getEventDataWrapperFromSearchResponse(SearchResponse searchResponse) {
        EventDataWrapper eventDataWrapper = new EventDataWrapper();
        Set<EventListData> eventListDataSet = new HashSet<EventListData>();
        for(EventSearchResponse eventSearchResponse:searchResponse.getSet_events()){
            EventListData eventListData = new EventListData();

            eventListData.e_id = eventSearchResponse.getE_id();
            eventListData.title = eventSearchResponse.getTitle();
            eventListData.venue = eventSearchResponse.getVenue();
            eventListData.scheduled_date = eventSearchResponse.getScheduled_date_time();

            if(eventSearchResponse.getLast_updated_location() != null && LocationHelper.userCurrentLocationLat != null && LocationHelper.userCurrentLocationLng != null) {
                Double eventLocationLat = eventSearchResponse.getLast_updated_location().latitude;
                Double eventLocationLng = eventSearchResponse.getLast_updated_location().longitude;
                String distance = LocationHelper.calculateDistanceString(LocationHelper.userCurrentLocationLat, LocationHelper.userCurrentLocationLng,
                        eventLocationLat, eventLocationLng);
                eventListData.distance = distance;
            }

            eventListDataSet.add(eventListData);
        }
        eventDataWrapper.eventListDataSet = eventListDataSet;
        eventDataWrapper.showTitle = true;
        return eventDataWrapper;
    }

}
