package org.redhelp.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.redhelp.adapter.items.PlacesAutoCompleteItem;
import org.redhelp.common.EditUserAccountRequest;
import org.redhelp.common.EditUserAccountResponse;
import org.redhelp.common.SaveBloodProfileRequest;
import org.redhelp.common.SaveBloodProfileResponse;
import org.redhelp.common.types.BloodGroupType;
import org.redhelp.common.types.CreateBloodProfileResponseTypes;
import org.redhelp.common.types.Gender;
import org.redhelp.common.types.JodaTimeFormatters;
import org.redhelp.customviews.CustomAutoCompleteTextView;
import org.redhelp.data.PlacesSearchData;
import org.redhelp.fagment.DatePickerFragment;
import org.redhelp.helpers.DateTimeHelper;
import org.redhelp.location.LocationUtil;
import org.redhelp.network.NetworkChecker;
import org.redhelp.session.SessionManager;
import org.redhelp.task.BloodProfileAsyncTask;
import org.redhelp.task.EditUserAccountTask;
import org.redhelp.task.PlacesAsyncTask;
import org.redhelp.task.PlacesDetailAsyncTask;
import org.redhelp.task.PlacesDetailJsonParserAsyncTask;
import org.redhelp.types.PlacesDetailRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by harshis on 5/21/14.
 */
public class CreateBloodProfileActivity extends FragmentActivity
        implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks, BloodProfileAsyncTask.IBloodProfileAsyncTaskListener,
        EditUserAccountTask.IEditUserAccountTaskListener, PlacesDetailJsonParserAsyncTask.IPlacesResponseHandler
{
    public static final String BUNDLE_IS_NEW_FB = "is_fb_new";
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;


    private Button bt_birthdate;
    private RadioGroup rgb_sex;
    private RadioButton rb_sex;
    private Spinner sp_bloodgroup;
    private Button bt_done;

    private AutoCompleteTextView atv_location;
    private PlacesAsyncTask placesTask;
    private PlacesDetailJsonParserAsyncTask.IPlacesResponseHandler currentFragmentReference;

    private EditText et_phone_no;
    private LinearLayout ll_phone_no;

    private boolean is_phone_no_visible;

    private final int RADIO_MALE = R.id.rb_male_createbloodprofile;
    private final int RADIO_FEMALE = R.id.rb_female_createbloodprofile;
    private String city = null;

    private void setViewObjects() {
        currentFragmentReference = this;

        bt_birthdate = (Button) findViewById(R.id.bt_birthdate_createbloodprofile);
        rgb_sex = (RadioGroup) findViewById(R.id.rgb_sex_createbloodprofile);
        rb_sex = (RadioButton) findViewById(R.id.rb_male_createbloodprofile);
        sp_bloodgroup  = (Spinner) findViewById(R.id.sp_bloodtype_createbloodprofile);

        bt_done = (Button) findViewById(R.id.btn_Done_after_screen);


        bt_birthdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(df.parse(button.getText().toString()));
                } catch (ParseException e) {
                    // Do nothing. Calendar defaults to the current date. Use that instead.
                }

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                final DatePickerFragment newFragment = DatePickerFragment.newInstance(button.getId(), year, month, day);
                newFragment.show(getSupportFragmentManager(), "");

            }
        });

        atv_location = (AutoCompleteTextView) findViewById(R.id.atv_location_createbloodprofile);
        atv_location.setThreshold(1);
        atv_location.setDropDownBackgroundResource(R.color.REDHELP_BLUE);

        atv_location.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                PlacesSearchData searchData = new PlacesSearchData();
                searchData.type = "(cities)";
                searchData.atvPlaces = atv_location;
                searchData.searchStr = s.toString();

                placesTask = new PlacesAsyncTask(getApplicationContext());
                placesTask.execute(searchData);
            }
        });
        ll_phone_no = (LinearLayout) findViewById(R.id.ll_phone_no_create_bp);
        et_phone_no = (EditText) findViewById(R.id.et_phoneno_createbloodprofile);
        ll_phone_no.setVisibility(View.GONE);
        is_phone_no_visible = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            boolean is_new_fb = bundle.getBoolean(BUNDLE_IS_NEW_FB);
            if(is_new_fb) {
                ll_phone_no.setVisibility(View.VISIBLE);
                is_phone_no_visible = true;
            }
        }
    }

    private SaveBloodProfileRequest createBloodRequest(String city, Long u_id, Double lat, Double lng)
    {
        int selectedId = rgb_sex.getCheckedRadioButtonId();
        int selectBloodGroupPosition = sp_bloodgroup.getSelectedItemPosition();

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

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, DatePickerFragment._year);
        cal.set(Calendar.MONTH, DatePickerFragment._month);
        cal.set(Calendar.DAY_OF_MONTH, DatePickerFragment._day);
        Date mBirthDateDateTime = cal.getTime();

        SaveBloodProfileRequest request = new SaveBloodProfileRequest();
        request.setCity(city);
        request.setBlood_group_type(bloodGroupType);
        request.setGender(gender);
        request.setU_id(u_id);
        String birthDateStr = DateTimeHelper.convertJavaDateToString(mBirthDateDateTime, JodaTimeFormatters.dateFormatter);
        request.setBirth_date(birthDateStr);


        request.setLast_known_location_lat(lat);
        request.setLast_known_location_long(lng);
        return request;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bp_layout);
        setViewObjects();
        mLocationClient = new LocationClient(this, this, this);


        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDoneButtonClick();
            }
        });
    }
    private void handleDoneButtonClick() {
        //If internet is not available show toast and do nothing.
        if(NetworkChecker.isNetworkAvailable(getApplicationContext()) == false) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.toast_server_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        PlacesAutoCompleteItem item = ((CustomAutoCompleteTextView)atv_location).itemSelected;
        if(item != null) {
            city = ((CustomAutoCompleteTextView) atv_location).itemSelected.description;
            PlacesDetailRequest request = new PlacesDetailRequest();
            request.setKey(getResources().getString(R.string.google_places_key));
            request.setReference(((CustomAutoCompleteTextView) atv_location).itemSelected.reference);
            request.setSensor(false);
            PlacesDetailAsyncTask task = new PlacesDetailAsyncTask(currentFragmentReference);
            task.execute(request);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_LONG);
            toast.setText("Please select an item from dropdown menu, for city field.");
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            //handleCreateButtonOnClick(0d,0d);
        }

    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * The view object associated with this method, in this case a Button.
     */
    public Location getLocation() {
        // If Google Play Services is available
        if(getLastKnownLocation() != null)
            Log.e("Location via getLastKnonwLocation:", getLastKnownLocation().toString());
        if (LocationUtil.servicesConnected(this)) {
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

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);

        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            String log = String.format("last known location, provider: %s, location: %s", provider,
                    l);
            Log.d("locaiton,",log);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                String log2 = String.format("found best last known location: %s", l);
                Log.d("location,", log2);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
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
        Log.e("location-service", "came in onConnected");

    }

    @Override
    public void onDisconnected() {
        Log.e("location-service", "came in onDisconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("location-service", "came in onConnectionFailed");

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

    @Override
    public void handleCreateBloodProfileError() {
        //TODO handle error case
    }

    @Override
    public void handleCreateBloodProfileResponse(SaveBloodProfileResponse response) {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT);
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if(response.getResponse_type().equals(CreateBloodProfileResponseTypes.SUCCESSFUL)) {
            toast.setText(R.string.toast_bloodprofile_updated);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();

            SessionManager sessionManager = SessionManager.getSessionManager(getApplicationContext());
            boolean isDone = sessionManager.updateLoginState(2, null, response.getB_p_id());
            if(isDone == true && sessionManager.isLoggedIn()) {
                // Open MainScreen.
                Intent home_screen_intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                home_screen_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home_screen_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home_screen_intent);
            }
        } else  {
            toast.setText(R.string.toast_invalid_login);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }

    @Override
    public void handleLoginError() {

    }

    @Override
    public void handleEditUserAccountResponse(EditUserAccountResponse response) {
        Log.e("edit_account_listener", "resposne:"+response.toString());

    }

    @Override
    public void handleResponse(Double lat, Double lng) {
        Log.d("Location-log", "lat-"+lat+"long-"+lng );
        Long u_id = SessionManager.getSessionManager(getApplicationContext()).getUid();
        if(u_id == null || u_id == 0){
            //TODO handle null u_id, while creating blood profile here
            return;
        }
        Location location = getLocation();
        Double lat_to_use = null,lng_to_use = null;
        if(location!= null) {
            lat_to_use = location.getLatitude();
            lng_to_use  = location.getLongitude();
        } else {
            lat_to_use = lat;
            lng_to_use = lng;
        }

        SaveBloodProfileRequest request  = createBloodRequest(city, u_id, lat_to_use, lng_to_use);
        BloodProfileAsyncTask bloodProfileAsyncTask = new BloodProfileAsyncTask(getApplicationContext(), this);
        bloodProfileAsyncTask.execute(request);

        if(is_phone_no_visible) {
            String phone_number = et_phone_no.getText().toString();
            EditUserAccountRequest editRequest = new EditUserAccountRequest();
            editRequest.setPhoneNo(phone_number);
            editRequest.setU_id(u_id);
            EditUserAccountTask editUserAccountTask = new EditUserAccountTask(getApplicationContext(), this);
            editUserAccountTask.execute(editRequest);
        }

    }
}
