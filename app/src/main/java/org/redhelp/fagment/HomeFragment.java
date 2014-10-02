package org.redhelp.fagment;

import android.annotation.TargetApi;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import org.redhelp.adapter.items.TabItem;
import org.redhelp.adapter.items.TabsItem;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.SearchRequest;
import org.redhelp.common.SearchResponse;
import org.redhelp.common.types.Location;
import org.redhelp.common.types.SearchRequestType;
import org.redhelp.data.SearchPrefData;
import org.redhelp.location.LocationUtil;
import org.redhelp.session.SessionManager;
import org.redhelp.task.SearchAsyncTask;
import org.redhelp.util.AndroidVersion;
import org.redhelp.util.LocationHelper;

import java.util.LinkedList;

/**
 * Created by harshis on 7/11/14.
 */
public class HomeFragment extends ProgressFragment implements TabsFragmentNew.ITabsFragment,
        SearchAsyncTask.ISearchAsyncTaskCaller, ErrorHandlerFragment.IErrorHandlerFragment,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private View fragmentContent;
    public static final String HOME_TAG = "HomeFragment";

    // Get current location related stuff.
    private LocationClient mLocationClient;
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    TabsItem tabs;
    TabsFragmentNew tabsFragment;
    private SearchAsyncTask searchAsyncTask;

    public static SearchResponse cachedSearchResponse;
    public static LatLng cachedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_home_layout, container, false);

        return super.onCreateView(inflater, container, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(fragmentContent);
        if(cachedSearchResponse != null && cachedLocation != null) {
            handleSearchResult(cachedSearchResponse, cachedLocation);
        } else {
            mLocationClient = new LocationClient(getActivity(), this, this);
            mLocationClient.connect();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        // Enable filter button
        setHasOptionsMenu(true);
        if(getActivity() instanceof HomeScreenActivity){
            ((HomeScreenActivity)getActivity()).showFilterMenu(true);
        }

        if(!AndroidVersion.isBeforeHoneycomb())
            getActivity().getActionBar()
                    .setTitle("RedHelp");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.menuid_filter);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mLocationClient != null)
            mLocationClient.disconnect();
    }

    @Override
    public TabsItem getTabs() {
        return tabs;
    }

    private void createAndExecuteSearchAsyncTask() {
        SearchPrefData prefData = null;

        if (getActivity() instanceof HomeScreenActivity) {
            prefData = ((HomeScreenActivity) getActivity()).searchPrefData;
        } else {
            Log.e("HomeFragment", "Activity must implement HomeScreenActivity interface");
            return;
        }
        LatLng currentUsersLatLng = null;

        android.location.Location location = getLocation();
        if(location != null) {
            currentUsersLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        if(!SearchRequestType.ALL.equals(prefData.getSearchRequestType()))
            prefData.updateLocation(currentUsersLatLng);

        Location northEast = null;
        Location southWest = null;
        if(SearchRequestType.BOUNDS_BASED.equals(prefData.getSearchRequestType())) {
            northEast = new Location();
            southWest = new Location();
            if(prefData != null && prefData.getNorthEastLocation() != null
                    && prefData.getSouthWestLocation() != null) {
                northEast.latitude = prefData.getNorthEastLocation().latitude;
                northEast.longitude = prefData.getNorthEastLocation().longitude;

                southWest.latitude = prefData.getSouthWestLocation().latitude;
                southWest.longitude = prefData.getSouthWestLocation().longitude;
            }
        }


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setSearchRequestType(prefData.getSearchRequestType());
        searchRequest.setSearchItems(prefData.getSearchItemTypes());
        searchRequest.setNorthEastLocation(northEast);
        searchRequest.setSouthWestLocation(southWest);


        android.location.Location currentLocation = getLocation();
        if(currentLocation == null) {
            searchRequest.setRequire_user_location(true);
        }
        Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
        searchRequest.setB_p_id(b_p_id);
        searchAsyncTask = new SearchAsyncTask(this, getActivity());
        searchAsyncTask.execute(searchRequest);
    }

    @Override
    public void handleSearchResult(SearchResponse searchResponse) {
        if(searchResponse == null)
            return;


        Double locationToUseLat = null, locationToUseLong = null;
        android.location.Location currentLocation = getLocation();

        cachedSearchResponse = searchResponse;

        if(currentLocation != null) {
            // Use currentLocation if available
            locationToUseLat = currentLocation.getLatitude();
            locationToUseLong = currentLocation.getLongitude();
        } else if(searchResponse.getUser_location_saved() != null) {
            // Use lastKnownlocation if returned by server
            locationToUseLat = searchResponse.getUser_location_saved().latitude;
            locationToUseLong = searchResponse.getUser_location_saved().longitude;
        } else {
            // Show toast to change filter if nothing is shown.
            Toast toast = Toast.makeText(getActivity(), "Your location couldn't be fetched ! Try any other filter ", Toast.LENGTH_LONG);
            toast.show();

        }
        cachedLocation = new LatLng(locationToUseLat,locationToUseLong);

        LocationHelper.userCurrentLocationLat = locationToUseLat;
        LocationHelper.userCurrentLocationLng = locationToUseLong;

        HomeListFragment homeListFragment = HomeListFragment.createHomeListFragmentInstance(searchResponse);

        HomeMapFragment homeMapFragment = HomeMapFragment.createHomeMapFragmentInstance(searchResponse, locationToUseLat, locationToUseLong);


        LinkedList<TabItem> tabsItemList = new LinkedList<TabItem>();
        TabItem list_tab = new TabItem("Home", 1, homeListFragment, 12);
        TabItem home_map_tab = new TabItem("Map View", 2, homeMapFragment, 12);

        tabsItemList.add(list_tab);
        tabsItemList.add(home_map_tab);


        TabsItem tabs = new TabsItem();
        tabs.tabs = tabsItemList;
        this.tabs = tabs;
        showTabs();
    }


    public void handleSearchResult(SearchResponse searchResponse, LatLng cachedLocation) {
        if(searchResponse == null)
            return;

        Double locationToUseLat = null, locationToUseLong = null;

        cachedSearchResponse = searchResponse;

        if(cachedLocation != null) {
            // Use currentLocation if available
            locationToUseLat = cachedLocation.latitude;
            locationToUseLong = cachedLocation.longitude;
        } else if(searchResponse.getUser_location_saved() != null) {
            // Use lastKnownlocation if returned by server
            locationToUseLat = searchResponse.getUser_location_saved().latitude;
            locationToUseLong = searchResponse.getUser_location_saved().longitude;
        } else {
            // Show toast to change filter if nothing is shown.
            Toast toast = Toast.makeText(getActivity(), "Your location couldn't be fetched ! Try any other filter ", Toast.LENGTH_LONG);
            toast.show();

        }

        LocationHelper.userCurrentLocationLat = locationToUseLat;
        LocationHelper.userCurrentLocationLng = locationToUseLong;

        HomeListFragment homeListFragment = HomeListFragment.createHomeListFragmentInstance(searchResponse);

        HomeMapFragment homeMapFragment = HomeMapFragment.createHomeMapFragmentInstance(searchResponse, locationToUseLat, locationToUseLong);


        LinkedList<TabItem> tabsItemList = new LinkedList<TabItem>();
        TabItem list_tab = new TabItem("Home", 1, homeListFragment, 12);
        TabItem home_map_tab = new TabItem("Map View", 2, homeMapFragment, 12);

        tabsItemList.add(list_tab);
        tabsItemList.add(home_map_tab);


        TabsItem tabs = new TabsItem();
        tabs.tabs = tabsItemList;
        this.tabs = tabs;
        showTabs();
    }

    private void showTabs() {

        if(this.isDetached())
            return;

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        tabsFragment = TabsFragmentNew.createTabsFragmentInstance(this);
        transaction.add(R.id.fl_content_home_layout, tabsFragment);
        transaction.commit();

        setContentShown(true);
    }

    @Override
    public void handleError(Exception e) {
        try {
            ErrorHandlerFragment errorHandlerFragment = ErrorHandlerFragment.createErrorHandlerFragmentInstance("", this);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame_main_screen, errorHandlerFragment);
            transaction.commit();
        } catch (Exception exp){

        }
    }

    @Override
    public void onRefreshButtonClickHandler() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame_main_screen, homeFragment);
        transaction.commit();

    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
        createAndExecuteSearchAsyncTask();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
         /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            createAndExecuteSearchAsyncTask();
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //LocationUtil.showErrorDialog(connectionResult.getErrorCode());
        }

    }

    /////////////////////////////////////////////////
    // Getting current location related stuff below//
    /////////////////////////////////////////////////
    private android.location.Location getLocation() {
        // If Google Play Services is available
        if (LocationUtil.servicesConnected(getActivity())) {
            // Get the current location
            android.location.Location currentLocation = mLocationClient.getLastLocation();
            return currentLocation;
        }
        return null;
    }

}