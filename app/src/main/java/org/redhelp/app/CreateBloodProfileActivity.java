package org.redhelp.app;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.redhelp.common.SaveBloodProfileRequest;
import org.redhelp.common.types.BloodGroupType;
import org.redhelp.common.types.Gender;
import org.redhelp.location.LocationUtil;
import org.redhelp.network.NetworkChecker;
import org.redhelp.session.SessionManager;
import org.redhelp.task.BloodProfileAsyncTask;

/**
 * Created by harshis on 5/21/14.
 */
public class CreateBloodProfileActivity extends Activity implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks{
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;


    private Button bt_birthdate;
    private RadioGroup rgb_sex;
    private RadioButton rb_sex;
    private Spinner sp_bloodgroup;
    private Spinner sp_city;
 //   private Spinner mDonationRateSpinner;
    private Button bt_done;


    private final int RADIO_MALE = R.id.rb_male_createbloodprofile;
    private final int RADIO_FEMALE = R.id.rb_female_createbloodprofile;

    private void setViewObjects() {
        bt_birthdate = (Button) findViewById(R.id.bt_birthdate_createbloodprofile);
        rgb_sex = (RadioGroup) findViewById(R.id.rgb_sex_createbloodprofile);
        rb_sex = (RadioButton) findViewById(R.id.rb_male_createbloodprofile);
        sp_bloodgroup  = (Spinner) findViewById(R.id.sp_bloodtype_createbloodprofile);
        sp_city = (Spinner) findViewById(R.id.sp_states_createbloodprofile);

        bt_done = (Button) findViewById(R.id.btn_Done_after_screen);
    }

    private SaveBloodProfileRequest createBloodRequest()
    {
        int selectedId = rgb_sex.getCheckedRadioButtonId();
        int selectBloodGroupPosition = sp_bloodgroup.getSelectedItemPosition();
        int selectCityPosition = sp_city.getSelectedItemPosition();

        Gender gender = (selectedId == RADIO_MALE) ? Gender.MALE : Gender.FEMALE;
        BloodGroupType bloodGroupType = null;
        if(selectBloodGroupPosition == 0){
            bloodGroupType = BloodGroupType.A;
        } else if(selectBloodGroupPosition == 1) {
            bloodGroupType = BloodGroupType.A_;
        }  else if(selectBloodGroupPosition == 2) {
            bloodGroupType = BloodGroupType.B;
        } else if(selectBloodGroupPosition == 3) {
            bloodGroupType = BloodGroupType.B_;
        } else if(selectBloodGroupPosition == 4) {
            bloodGroupType = BloodGroupType.O;
        } else if(selectBloodGroupPosition == 5) {
            bloodGroupType = BloodGroupType.O_;
        } else if(selectBloodGroupPosition == 6) {
            bloodGroupType = BloodGroupType.AB;
        } else if(selectBloodGroupPosition == 7) {
            bloodGroupType = BloodGroupType.AB_;
        }
        String city;
        String[] city_array = getResources().getStringArray(R.array.states_array);
        city = city_array[selectCityPosition];

        Long u_id = SessionManager.getSessionManager(getApplicationContext()).getUid();
        if(u_id == null || u_id== 0){
            //TODO handle null u_id, while creating blood profile here
        }



        SaveBloodProfileRequest request = new SaveBloodProfileRequest();
        request.setCity(city);
        request.setBlood_group_type(bloodGroupType);
        request.setGender(gender);
        request.setU_id(u_id);

        return request;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blood_profile_layout);
        setViewObjects();
        mLocationClient = new LocationClient(this, this, this);


        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                //If internet is not available show toast and do nothing.
                if(NetworkChecker.isNetworkAvailable(getApplicationContext()) == false) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.toast_server_error, Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                SaveBloodProfileRequest request  = createBloodRequest();

                BloodProfileAsyncTask bloodProfileAsyncTask = new BloodProfileAsyncTask(getApplicationContext(), null);
                bloodProfileAsyncTask.execute(request);
            }
        });
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * The view object associated with this method, in this case a Button.
     */
    public void getLocation() {

        // If Google Play Services is available
        if (LocationUtil.servicesConnected(this)) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
            if(currentLocation!=null)
                Log.e("Location", currentLocation.getLatitude() + ":" + currentLocation.getLongitude());
            else
                Log.e("Location", "currentLocation is null");

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();

    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
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
                        this,
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
}
