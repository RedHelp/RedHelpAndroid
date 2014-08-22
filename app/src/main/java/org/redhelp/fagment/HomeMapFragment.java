package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options;

import org.redhelp.app.R;
import org.redhelp.clusterkarf.ClusterkarfHelper;
import org.redhelp.clusterkarf.MarkerData;
import org.redhelp.clusterkarf.ToastedOnMarkerClickDownstreamListener;
import org.redhelp.common.BloodProfileSearchResponse;
import org.redhelp.common.BloodRequestSearchResponse;
import org.redhelp.common.EventSearchResponse;
import org.redhelp.common.SearchResponse;
import org.redhelp.common.types.SearchItemTypes;
import org.redhelp.session.SessionManager;
import org.redhelp.types.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by harshis on 5/25/14.
 */
public class HomeMapFragment extends Fragment implements
        GoogleMap.OnCameraChangeListener,
        Serializable, ToastedOnMarkerClickDownstreamListener.IToastedOnMarkerClickDownstreamListener
{

    public static final String BUNDLE_SEARCH_DATA = "SearchResult";
    public static final String BUNDLE_CURRENT_LOCATION_LAT = "currentloc_lat";
    public static final String BUNDLE_CURRENT_LOCATION_LONG = "currentloc_long";



    public static HomeMapFragment createHomeMapFragmentInstance(SearchResponse searchResponse,
                                                                android.location.Location location) {
        HomeMapFragment homeMapFragment =  new HomeMapFragment();
        Bundle content = new Bundle();
        content.putSerializable(BUNDLE_SEARCH_DATA, searchResponse);
        if(location!= null ) {
            content.putDouble(BUNDLE_CURRENT_LOCATION_LAT, location.getLatitude());
            content.putDouble(BUNDLE_CURRENT_LOCATION_LONG, location.getLongitude());
        }
        homeMapFragment.setArguments(content);
        return homeMapFragment;
    }

    private static View view;

    private static final String KEY_CAMERA_POSITION = "camera position";
    private GoogleMap map;
    private static Clusterkraf clusterkraf;

    private Button bt_post_blood_request;

    private void initializeViews() {
        bt_post_blood_request = (Button) getActivity().findViewById(R.id.bt_post_blood_request_home_map_layout);
        bt_post_blood_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map!= null)
                    Log.e("Bounds", "bounds="+map.getProjection().getVisibleRegion().latLngBounds);


                Fragment createBloodRequestFragment = new CreateBloodRequestFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_main_screen, createBloodRequestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /////////////////////////////
    //Life cycle methods below///
    ////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home_map_layout, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();

        Bundle data_passed = getArguments();
        Double location_lat = null, location_long = null;

        SearchResponse searchResponse = null;
        if(data_passed != null){
            try {
                searchResponse = (SearchResponse) data_passed.getSerializable(BUNDLE_SEARCH_DATA);
                location_lat = data_passed.getDouble(BUNDLE_CURRENT_LOCATION_LAT);
                location_long = data_passed.getDouble(BUNDLE_CURRENT_LOCATION_LONG);
            }catch (Exception e){
                return;
            }
        }
        LatLng latLng = null;
        if(location_lat != null && location_long != null) {
            latLng = new LatLng(location_lat,location_long);
            initMapForFirstTime(latLng);
        }

        handleSearchResult(latLng, searchResponse);
    }
    /*  @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            CameraPosition cameraPosition = map.getCameraPosition();
            if (cameraPosition != null) {
                outState.putParcelable(KEY_CAMERA_POSITION, cameraPosition);
            }
        }
    }*/

    //////////////////////////////////
    //On Camera change handler///////
    ////////////////////////////////

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(map!=null) {
            Set<SearchItemTypes> searchItemTypes = new HashSet<SearchItemTypes>();
            searchItemTypes.add(SearchItemTypes.BLOOD_REQUEST);
            searchItemTypes.add(SearchItemTypes.BLOOD_PROFILE);
            searchItemTypes.add(SearchItemTypes.EVENTS);
            if(map.getProjection() != null &&
                    map.getProjection().getVisibleRegion() != null &&
                    map.getProjection().getVisibleRegion().latLngBounds != null ){}
            //  createAndExecuteSearchAsyncTask(map.getProjection().getVisibleRegion().latLngBounds, searchItemTypes);
        }
    }

    /////////////////////////////////////////////
    //Functions for handling map and clusterkarf//
    /////////////////////////////////////////////
    private void initMap(final Options options,
                         final ArrayList<InputPoint> inputPoints,
                         final LatLng locationToZoom) {
        if(map == null) {
            Log.e("HomeMapFragment", "map is null");
            initMapForFirstTime(locationToZoom);
        } else {
            Log.e("HomeMapFragment", "map is not null");
            moveMapCameraToBoundsAndInitClusterkraf(options, inputPoints);
        }
    }

    private void initMapForFirstTime(final LatLng locationToZoom)
    {
        SupportMapFragment mapFragment = (SupportMapFragment)getActivity().
                getSupportFragmentManager().
                findFragmentById(R.id.map_home_map_layout);
        if (mapFragment != null) {
            map = mapFragment.getMap();
            if (map != null) {
                UiSettings uiSettings = map.getUiSettings();
                uiSettings.setAllGesturesEnabled(false);
                uiSettings.setScrollGesturesEnabled(true);
                uiSettings.setZoomGesturesEnabled(true);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locationToZoom)      // Sets the center of the map to Mountain View
                        .zoom(10)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.setOnCameraChangeListener(this);
            }
        }

    }
    private void moveMapCameraToBoundsAndInitClusterkraf(Options options,
                                                         ArrayList<InputPoint> inputPoints) {
        if(map != null && options != null && inputPoints != null) {

            initClusterkraf(options, inputPoints);

        }

    }
    private void initClusterkraf(Options options,
                                 ArrayList<InputPoint> inputPoints) {
        if(clusterkraf != null) {
            Log.e("HomeMapFragment", "clusterkarf is not null");
            clusterkraf.clear();
        }
        if (map != null && inputPoints != null && inputPoints.size() > 0 ) {
            clusterkraf = new Clusterkraf(map, options, inputPoints);
        }
    }


    public void handleSearchResult(LatLng latLng, SearchResponse searchResponse) {
        if(map == null)
            return;

        ArrayList<InputPoint> inputPoints = new ArrayList<InputPoint>();
        Set<BloodRequestSearchResponse> set_blood_requests = searchResponse.getSet_blood_requests();
        if(set_blood_requests != null) {
            for(BloodRequestSearchResponse bloodRequestSearchResponse : set_blood_requests ) {
                InputPoint inputPoint = ClusterkarfHelper.getInputPointFromSearchObject(bloodRequestSearchResponse);
                if(inputPoint != null)
                    inputPoints.add(inputPoint);
            }
        }

        Set<BloodProfileSearchResponse> set_blood_profiles = searchResponse.getSet_blood_profiles();
        if(set_blood_profiles != null) {
            for(BloodProfileSearchResponse bloodProfileSearchResponse : set_blood_profiles ) {
                InputPoint inputPoint = ClusterkarfHelper.getInputPointFromSearchObject(bloodProfileSearchResponse);
                if(inputPoint != null)
                    inputPoints.add(inputPoint);
            }
        }

        Set<EventSearchResponse> set_events = searchResponse.getSet_events();
        if(set_events != null) {
            for(EventSearchResponse eventSearchResponse : set_events ) {
                InputPoint inputPoint = ClusterkarfHelper.getInputPointFromSearchObject(eventSearchResponse);
                if(inputPoint != null)
                    inputPoints.add(inputPoint);
            }
        }
        String currentLocationtitle = "Your Current Location";
        MarkerData currentLocationMarkerData = new MarkerData(latLng, currentLocationtitle, null, null);

        InputPoint currentLocationInputPoint = new InputPoint(latLng, currentLocationMarkerData);
        inputPoints.add(currentLocationInputPoint);
        moveMapCameraToBoundsAndInitClusterkraf(ClusterkarfHelper.getOptionsObject(this, currentLocationInputPoint), inputPoints);

    }

    @Override
    public void onClickHandler(MarkerData data) {
        Fragment newFragment = null;

        if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.EVENTS))
            newFragment =  ViewEventFragment.createViewEventFragmentInstance(data.getId());
        else if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.BLOOD_REQUEST)){
            Bundle data_to_pass = new Bundle();
            data_to_pass.putLong(Constants.BUNDLE_B_R_ID, data.getId());
            newFragment = new ViewBloodRequestFragment();
            newFragment.setArguments(data_to_pass);
        } else if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.BLOOD_PROFILE)) {
            Long b_p_id = data.getId();
            Long requester_b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();

            newFragment = ViewBloodProfileFragment.createViewBloodProfileFragmentInstance(requester_b_p_id, b_p_id);
        }
        if(isDetached())
            return;

        if(newFragment != null){
            Log.e("HomeMapFragment", "newFragment is null");
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame_main_screen, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}