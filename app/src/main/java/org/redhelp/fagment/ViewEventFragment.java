package org.redhelp.fagment;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.redhelp.app.R;
import org.redhelp.common.GetEventResponse;
import org.redhelp.common.SlotsCommonFields;
import org.redhelp.common.types.EventRequestType;
import org.redhelp.dialogs.SlotsDialogFragment;
import org.redhelp.location.LocationUtil;
import org.redhelp.task.GetEventAsyncTask;
import org.redhelp.util.DateHelper;
import org.redhelp.util.LocationHelper;

import java.util.Set;
import java.util.SortedSet;

/**
 * Created by harshis on 7/4/14.
 */
public class ViewEventFragment extends ProgressFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener, GetEventAsyncTask.IGetEventAsyncTaskListner{

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    public static final String BUNDLE_E_ID = "BUNDLE_E_ID";

    public static ViewEventFragment createViewEventFragmentInstance(long e_id) {
        ViewEventFragment viewEventFragment =  new ViewEventFragment();
        Bundle content = new Bundle();
        content.putLong(BUNDLE_E_ID, e_id);
        viewEventFragment.setArguments(content);
        return viewEventFragment;
    }


    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private View fragmentContent;

    private TextView tv_event_name;
    private TextView tv_distance_from_you;
    private TextView tv_organization;
    private TextView tv_date_time;
    private TextView tv_venue;
    private TextView tv_number_of_ppl;

    private Button bt_call;
    private Button bt_navigate;
    private Button bt_attend;
    private Button bt_volunteer;

    private LinearLayout ll_slots_content;

    private String phone_number;
    private Double event_location_lat;
    private Double event_location_long;
    private Set<SlotsCommonFields> slots;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_event_layout, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseViews();

        Bundle data_received = getArguments();
        if(data_received != null) {
            Long e_id = data_received.getLong(BUNDLE_E_ID);
            fetchAndShowData(e_id);
        }
    }

    private void initialiseViews() {
        setContentView(fragmentContent);
        tv_event_name = (TextView) getActivity().findViewById(R.id.tv_event_name_view_event_layout);
        tv_date_time = (TextView) getActivity().findViewById(R.id.tv_date_time_view_event_layout);
        tv_distance_from_you = (TextView) getActivity().findViewById(R.id.tv_distance_from_you_view_event_layout);
        tv_organization = (TextView) getActivity().findViewById(R.id.tv_organization_view_event_layout);
        tv_venue = (TextView) getActivity().findViewById(R.id.tv_venue_view_event_layout);
        tv_number_of_ppl = (TextView) getActivity().findViewById(R.id.tv_number_of_attendees_view_event_layout);

        bt_call = (Button) getActivity().findViewById(R.id.bt_call_view_event_layout);
        bt_navigate = (Button) getActivity().findViewById(R.id.bt_navigate_view_event_layout);
        bt_attend = (Button) getActivity().findViewById(R.id.bt_attend_view_event_layout);
        bt_volunteer = (Button) getActivity().findViewById(R.id.bt_volunteer_view_event_layout);

        bt_call.setOnClickListener(this);
        bt_navigate.setOnClickListener(this);
        bt_attend.setOnClickListener(this);
        bt_volunteer.setOnClickListener(this);

        ll_slots_content = (LinearLayout) getActivity().findViewById(R.id.ll_slots_content_view_event_layout);
        mLocationClient = new LocationClient(getActivity(), this, this);

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.ll_map_view_event_layout, mMapFragment);
        fragmentTransaction.commit();
    }


    private void fetchAndShowData(Long e_id) {
        setContentShown(false);

        GetEventAsyncTask getEventAsyncTask = new GetEventAsyncTask(this);
        getEventAsyncTask.execute(e_id);
    }





    public void showData(String event_name,
                         String organization,
                         String venue,
                         Double event_location_lat,
                         Double event_location_long,
                         String start_datetime,
                         String phone_number,
                         SortedSet<SlotsCommonFields> slots,
                         int number_of_ppl_going) {
        tv_event_name.setText(event_name);
        tv_venue.setText(venue);
        tv_organization.setText(organization);
        tv_number_of_ppl.setText(String.valueOf(number_of_ppl_going));

        String distance_from_you_string = calculateDistanceFromYou(event_location_lat, event_location_long);
        if(distance_from_you_string != null)
            tv_distance_from_you.setText(distance_from_you_string);
        else
            tv_distance_from_you.setText("-");

        if(event_location_lat != null && event_location_long != null && event_location_lat != 0d  && event_location_long != 0d) {
            setUpMapIfNeeded(event_location_lat, event_location_long);
            this.event_location_lat = event_location_lat;
            this.event_location_long = event_location_long;
        } else {
            bt_navigate.setEnabled(false);
        }

        if(start_datetime != null) {
            String newDateFormat = DateHelper.getDateTimeInViewableFormat(start_datetime);
            tv_date_time.setText(newDateFormat);
        } else {
            tv_date_time.setText("-");
        }

        if(slots != null) {
            int i = 0;
            for(SlotsCommonFields slot : slots) {
                i++;
                String slot_string = DateHelper.createSlotString(slot, i);
                TextView tv_slot = new TextView(getActivity());
                tv_slot.setText(slot_string);
                tv_slot.setGravity(Gravity.LEFT);
                ll_slots_content.addView(tv_slot);
            }
            this.slots = slots;

        }
        else{
            bt_attend.setEnabled(false);
            bt_volunteer.setEnabled(false);
        }
        if(phone_number == null)
            bt_call.setEnabled(false);
        else
            this.phone_number = phone_number;

    }



    private String calculateDistanceFromYou(Double event_location_lat, Double event_location_long) {

        if(event_location_lat == null || event_location_long == null)
            return null;

        Location current_location = getLocation();

        if(current_location == null)
            return null;


        Float distance = LocationHelper.calculateDistance(event_location_lat, event_location_long,
                current_location.getLatitude(), current_location.getLongitude());
        if(distance == null)
            return null;

        String distance_string = String.format("%.1f Kms from you", distance);

        return distance_string;
    }

    // Map related stuff below.
    private void setUpMapIfNeeded(double lat, double lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(lat, lng);
            }
        }
    }
    private void setUpMap(double lat, double logitude) {

        LatLng event_location = new LatLng(lat, logitude);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.addMarker(new MarkerOptions()
                .position(event_location)
                .title("Found here !"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(event_location)      // Sets the center of the map to Mountain View
                .zoom(13)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    // Getting current location related stuff below
    private Location getLocation() {
        // If Google Play Services is available
        if (LocationUtil.servicesConnected(getActivity())) {
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
            if(currentLocation!=null)
                Log.e("Location", currentLocation.getLatitude() + ":" + currentLocation.getLongitude());
            else
                Log.e("Location", "currentLocation is null");
            return currentLocation;
        }
        return null;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();

    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {

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
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //LocationUtil.showErrorDialog(connectionResult.getErrorCode());
        }

    }

    //OnClick handler.
    @Override
    public void onClick(View view) {

        Log.e("ViewEventFragment","onClick handler :"+view.getId());
        Intent intent;
        switch (view.getId()) {
            case R.id.bt_call_view_event_layout :
                if(phone_number == null)
                    break;
                String uri = "tel:" + phone_number.trim() ;
                 intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                break;
            case R.id.bt_navigate_view_event_layout :
                if(event_location_lat == null || event_location_long == null)
                    break;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
                        +event_location_lat
                        +","+event_location_long));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.bt_attend_view_event_layout:
                if(slots == null) {
                    break;
                }
                showDialog(slots, EventRequestType.ATTEND);
                break;
            case R.id.bt_volunteer_view_event_layout:
                if(slots == null){
                    break;
                }
                showDialog(slots, EventRequestType.VOLUNTEER);
                break;
        }
    }


    //Show dialogs

    void showDialog(Set<SlotsCommonFields> slots, EventRequestType requestType) {
        //mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("SlotsDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        SlotsDialogFragment newFragment = SlotsDialogFragment.newInstance(slots, requestType);
        newFragment.show(ft, "SlotsDialog");
    }

    @Override
    public void handleResponse(GetEventResponse response) {

        String event_name = response.getName();
        String phone_number = response.getPhone_number();
        String organization = response.getOrganization();
        String venue = response.getLocation_address();

        Double location_lat = response.getLocation_lat();
        Double location_long = response.getLocation_long();

        String start_datetime_string = response.getMaster_start_datetime();

        int number_of_ppl_attending = 0;

        SortedSet<SlotsCommonFields> slots = response.getSlots();

        for(SlotsCommonFields slot : slots ) {
            number_of_ppl_attending += slot.getCurrent_attendees() + slot.getCurrent_volunteers();
        }

        showData(event_name, organization,venue, location_lat, location_long, start_datetime_string,
                phone_number, slots, number_of_ppl_attending);
        setContentShown(true);
    }
}
