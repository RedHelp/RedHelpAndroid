package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.types.SearchItemTypes;
import org.redhelp.common.types.SearchRequestType;
import org.redhelp.data.SearchPrefData;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by harshis on 7/17/14.
 */
public class HomeSearchPrefFragment extends Fragment {


    private RadioGroup rg_search_type;
    private final int RADIO_LOCATION_BASED = R.id.rb_location_based_home_search_pref_layout;
    private final int RADIO_EVERYWHERE = R.id.rb_everywhere_home_search_pref_layout;

    private RadioGroup rg_distance;
    private final int RADIO_25_KM = R.id.rb_25km_search_pref_layout;
    private final int RADIO_50_KM = R.id.rb_50km_search_pref_layout;
    private final int RADIO_100_KM = R.id.rb_100km_search_pref_layout;

    private CheckBox cb_events;
    private CheckBox cb_blood_requests;
    private CheckBox cb_donors;

    private Button bt_done;
    
    
    private void initializeViews() {
        rg_search_type = (RadioGroup) getActivity().findViewById(R.id.rg_search_type_home_search_pref_layout);
        rg_distance = (RadioGroup) getActivity().findViewById(R.id.rg_distance_home_search_pref_layout);

        bt_done = (Button) getActivity().findViewById(R.id.bt_done_home_search_pref_layout);

        cb_events = (CheckBox) getActivity().findViewById(R.id.cb_events_home_search_pref_layout);
        cb_blood_requests = (CheckBox) getActivity().findViewById(R.id.cb_blood_requests_home_search_pref_layout);
        cb_donors = (CheckBox) getActivity().findViewById(R.id.cb_donors_home_search_pref_layout);


        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDoneButtonClick();
            }
        });
    }

    private void handleDoneButtonClick() {
        SearchRequestType searchRequestType = null;
        int checked_seach_type = rg_search_type.getCheckedRadioButtonId();

        if(checked_seach_type == RADIO_LOCATION_BASED)
            searchRequestType = SearchRequestType.BOUNDS_BASED;
        else if(checked_seach_type == RADIO_EVERYWHERE)
            searchRequestType = SearchRequestType.ALL;

        Double distanceFactor = 0.22;
        int checked_distance = rg_distance.getCheckedRadioButtonId();

        if(checked_distance == RADIO_50_KM)
            distanceFactor = 0.44;
        else if(checked_distance == RADIO_100_KM)
            distanceFactor = 0.88;
        else if(checked_distance == RADIO_25_KM)
            distanceFactor = 0.22;



        Set<SearchItemTypes> searchItemTypes = new HashSet<SearchItemTypes>();

        if(cb_events.isChecked())
            searchItemTypes.add(SearchItemTypes.EVENTS);
        if(cb_blood_requests.isChecked())
            searchItemTypes.add(SearchItemTypes.BLOOD_REQUEST);
        if(cb_donors.isChecked())
            searchItemTypes.add(SearchItemTypes.BLOOD_PROFILE);

        if (getActivity() instanceof HomeScreenActivity) {
            SearchPrefData searchPrefData = ((HomeScreenActivity) getActivity()).searchPrefData;
            searchPrefData.setSearchRequestType(searchRequestType);
            searchPrefData.setSearchItemTypes(searchItemTypes);
            searchPrefData.setDistanceFactor(distanceFactor);
            HomeFragment.cachedSearchResponse = null;
            switchFragment(new HomeFragment());
        } else {
            Log.e("HomeFragment", "Activity must implement HomeScreenActivity interface");
            return;
        }


    }

    // the meat of switching the above fragment
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof HomeScreenActivity) {
            HomeScreenActivity ra = (HomeScreenActivity) getActivity();
            ra.switchContent(fragment, HomeFragment.HOME_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_search_pref_layout, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViews();
    }
}
