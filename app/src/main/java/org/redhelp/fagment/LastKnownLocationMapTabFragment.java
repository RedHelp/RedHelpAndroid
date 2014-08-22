package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.redhelp.app.R;
import org.redhelp.types.Constants;

/**
 * Created by harshis on 6/3/14.
 */
public class LastKnownLocationMapTabFragment extends Fragment {

    public static Fragment createLastKnownLocationMapTabFragmentInstance(Double location_lat, Double location_long) {
        Fragment lastKnownLocationMapTabFragment =  new LastKnownLocationMapTabFragment();
        Bundle content = new Bundle();
        content.putDouble(Constants.LOCATION_LAT, location_lat);
        content.putDouble(Constants.LOCATION_LONG, location_long);
        lastKnownLocationMapTabFragment.setArguments(content);
        return lastKnownLocationMapTabFragment;
    }


    private GoogleMap mMap;

    private void setUpMapIfNeeded(double lat, double lng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fg_map_view_last_known_location))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(lat, lng);
            }
        }
    }


    private void setUpMap(double lat, double logitude) {
        mMap.clear();
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



    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_last_known_location_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        Bundle data_passed = getArguments();
        Double location_lat = data_passed.getDouble(Constants.LOCATION_LAT);
        Double location_long = data_passed.getDouble(Constants.LOCATION_LONG);
        if(location_lat != null && location_long != null && location_lat != 0d  && location_long != 0d) {
            setUpMapIfNeeded(location_lat, location_long);
        }
    }
}
