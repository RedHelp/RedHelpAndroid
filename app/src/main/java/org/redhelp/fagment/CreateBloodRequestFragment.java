package org.redhelp.fagment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.redhelp.adapter.items.PlacesAutoCompleteItem;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.SaveBloodRequestRequest;
import org.redhelp.common.SaveBloodRequestResponse;
import org.redhelp.common.types.BloodGroupType;
import org.redhelp.common.types.BloodRequirementType;
import org.redhelp.customviews.CustomAutoCompleteTextView;
import org.redhelp.data.PlacesSearchData;
import org.redhelp.interfaces.ProgressDialogInterface;
import org.redhelp.location.LocationUtil;
import org.redhelp.session.SessionManager;
import org.redhelp.task.CreateBloodRequestAsyncTask;
import org.redhelp.task.PlacesAsyncTask;
import org.redhelp.task.PlacesDetailAsyncTask;
import org.redhelp.task.PlacesDetailJsonParserAsyncTask;
import org.redhelp.types.Constants;
import org.redhelp.types.PlacesDetailRequest;
import org.redhelp.util.AndroidVersion;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by harshis on 6/8/14.
 */
public class CreateBloodRequestFragment extends Fragment implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks, ProgressDialogInterface,
        CreateBloodRequestAsyncTask.ICreateBloodRequestAsyncTaskListener, PlacesDetailJsonParserAsyncTask.IPlacesResponseHandler {

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    private EditText et_patient_name;
    private EditText et_description;
    private EditText et_phone_number;
    private EditText et_units;

    private RadioGroup rg_blood_requirement_type;
    private final int RADIO_RBC = R.id.rb_rbc_create_blood_request_layout;
    private final int RADIO_WHOLEBLOOD = R.id.rb_wholeblood_create_blood_request_layout;
    private final int RADIO_PLASMA = R.id.rb_plasma_create_blood_request_layout;
    private final int RADIO_PLATELETES = R.id.rb_platelets_create_blood_request_layout;

    private ToggleButton tb_a;
    private ToggleButton tb_a_;
    private ToggleButton tb_b;
    private ToggleButton tb_b_;
    private ToggleButton tb_ab;
    private ToggleButton tb_ab_;
    private ToggleButton tb_o;
    private ToggleButton tb_o_;

    public ProgressDialog progressDialog;
    public ProgressBar pbHospital;
    private PlacesDetailJsonParserAsyncTask.IPlacesResponseHandler currentFragmentReference;

    private Button bt_create;
    private AutoCompleteTextView atv_location;
    private PlacesAsyncTask placesTask;



    @Override
    public void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }


    private void initializeViews() {
        Activity view = getActivity();
        currentFragmentReference = this;
        mLocationClient = new LocationClient(view, this, this);
        et_patient_name = (EditText) view.findViewById(R.id.et_patient_name_create_blood_request_layout);
        et_description = (EditText) view.findViewById(R.id.et_description_create_blood_request_layout);
        et_phone_number = (EditText) view.findViewById(R.id.et_phoneno_create_blood_request_layout);
        et_units = (EditText) view.findViewById(R.id.et_units_create_blood_request_layout);

        rg_blood_requirement_type = (RadioGroup) view.findViewById(R.id.rg_blood_req_type_create_blood_request_layout);
        tb_a = (ToggleButton) view.findViewById(R.id.tb_a_create_blood_request_form);
        tb_a_ = (ToggleButton) view.findViewById(R.id.tb_a__create_blood_request_form);
        tb_b = (ToggleButton) view.findViewById(R.id.tb_b_create_blood_request_form);
        tb_b_ = (ToggleButton) view.findViewById(R.id.tb_b__create_blood_request_form);
        tb_ab = (ToggleButton) view.findViewById(R.id.tb_ab_create_blood_request_form);
        tb_ab_ = (ToggleButton) view.findViewById(R.id.tb_ab__create_blood_request_form);
        tb_o = (ToggleButton) view.findViewById(R.id.tb_o_create_blood_request_form);
        tb_o_ = (ToggleButton) view.findViewById(R.id.tb_o__create_blood_request_form);


        pbHospital = (ProgressBar) view.findViewById(R.id.pb_hospital_create_bp_layout);
        pbHospital.setIndeterminate(true);
        pbHospital.setVisibility(View.INVISIBLE);

        bt_create = (Button) view.findViewById(R.id.bt_create_blood_request_layout);

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacesDetailRequest request = new PlacesDetailRequest();
                request.setKey(getResources().getString(R.string.google_places_key));
                PlacesAutoCompleteItem item = ((CustomAutoCompleteTextView)atv_location).itemSelected;
                if(item != null) {
                    request.setReference(((CustomAutoCompleteTextView) atv_location).itemSelected.reference);
                    request.setSensor(false);
                    PlacesDetailAsyncTask task = new PlacesDetailAsyncTask(currentFragmentReference);
                    task.execute(request);
                } else {
                    Toast toast = Toast.makeText(getActivity(),"", Toast.LENGTH_LONG);
                    toast.setText("Please select an item from dropdown menu, for hospital field.");
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                    //handleCreateButtonOnClick(0d,0d);
                }
            }
        });


        atv_location = (AutoCompleteTextView) view.findViewById(R.id.atv_location_create_blood_request_layout);
        atv_location.setThreshold(1);

        atv_location.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                pbHospital.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                PlacesSearchData searchData = new PlacesSearchData();
                searchData.type = "establishment";
                searchData.atvPlaces = atv_location;
                searchData.searchStr = s.toString();
                searchData.progressBar = pbHospital;

                placesTask = new PlacesAsyncTask(getActivity());
                placesTask.execute(searchData);
            }
        });
    }

    private SaveBloodRequestRequest createRequest(String patient_name,
                                                  String phone_number,
                                                  String description,
                                                  String units,
                                                  Set<BloodGroupType> blood_groups_set,
                                                  BloodRequirementType blood_requirement_type,
                                                  String place_string,
                                                  Double gps_location_lat,
                                                  Double gps_location_long,
                                                  Double place_location_lat,
                                                  Double place_location_long
                                                  ) {
        SaveBloodRequestRequest request = new SaveBloodRequestRequest();

        request.setB_p_id(SessionManager.getSessionManager(getActivity()).getBPId());
        request.setDescription(description);
        request.setPhone_number(phone_number);
        request.setPatient_name(patient_name);
        request.setList_blood_group(blood_groups_set);
        request.setBlood_requirement_type(blood_requirement_type);
        request.setUnits(units);
        request.setPlace_string(place_string);
        request.setGps_location_lat(gps_location_lat);
        request.setGps_location_long(gps_location_long);
        request.setPlace_location_lat(place_location_lat);
        request.setPlace_location_long(place_location_long);

        return request;
    }
    public void handleCreateButtonOnClick(Double place_lat, Double place_long) {

    }

    private boolean validateRequestAndShowToast(SaveBloodRequestRequest request) {
        boolean isCorrect = true;
        String toast_message="Please correct following error: ";
        if(request.getPatient_name() == null || request.getPatient_name().length() < 1) {
            isCorrect = false;
            toast_message += "\n Please enter a valid Patient name";
        }
        if(request.getList_blood_group() == null || request.getList_blood_group().size() < 1) {
            isCorrect = false;
            toast_message += "\n Please select atleast one blood group";
        }
        if(request.getBlood_requirement_type() == null) {
            isCorrect = false;
            toast_message += "\n Please select blood requirement type";
        }
        if(request.getUnits() == null) {
            isCorrect = false;
            toast_message += "\n Please enter valid units";
        }
        if(request.getPhone_number() == null || request.getPhone_number().length() > 11 || request.getPhone_number().length() < 10) {
            isCorrect = false;
            toast_message += "\n Please enter valid 10-digit phone number";
        }
        Toast toast = Toast.makeText(getActivity(),"", Toast.LENGTH_LONG);
        toast.setText(toast_message);
        toast.setDuration(Toast.LENGTH_LONG);
        if(!isCorrect)
            toast.show();

        return isCorrect;
    }

    private BloodRequirementType getBloodRequrirementType(int id) {
        switch (id) {
            case RADIO_RBC:
                return BloodRequirementType.RBC;
            case RADIO_WHOLEBLOOD:
                return BloodRequirementType.WHOLE_BLOOD;
            case RADIO_PLASMA:
                return BloodRequirementType.PLASMA;
            case RADIO_PLATELETES:
                return BloodRequirementType.PLATELETS;
            default:
                return null;
        }
    }
    private Set<BloodGroupType> getSelectedBloodGroups() {
        Set<BloodGroupType> bloodGroupsSelected = new HashSet<BloodGroupType>();

        if(tb_a.isChecked())
            bloodGroupsSelected.add(BloodGroupType.A);
        if(tb_a_.isChecked())
            bloodGroupsSelected.add(BloodGroupType.A_);
        if(tb_b.isChecked())
            bloodGroupsSelected.add(BloodGroupType.B);
        if(tb_b_.isChecked())
            bloodGroupsSelected.add(BloodGroupType.B_);
        if(tb_ab.isChecked())
            bloodGroupsSelected.add(BloodGroupType.AB);
        if(tb_ab_.isChecked())
            bloodGroupsSelected.add(BloodGroupType.AB_);
        if(tb_o.isChecked())
            bloodGroupsSelected.add(BloodGroupType.O);
        if(tb_o_.isChecked())
            bloodGroupsSelected.add(BloodGroupType.O_);

        return bloodGroupsSelected;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_blood_request_layout, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();

        // Disable filter button
        setHasOptionsMenu(true);
        if(getActivity() instanceof HomeScreenActivity){
            ((HomeScreenActivity)getActivity()).showFilterMenu(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.menuid_filter);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }


    public Location getLocation() {
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


    @Override
    public void handlePreExecute() {
        showProgressDialog("Loading", "Please wait..");

    }

    @Override
    public void handleCreateBloodRequestError() {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        if(!AndroidVersion.isBeforeHoneycomb())
            getActivity().getActionBar()
                .setTitle("Create Blood Request");
    }

    @Override
    public void handleCreateBloodRequestResponse(SaveBloodRequestResponse response) {
        hideProgressDialog();
        Long b_r_id = response.getB_r_id();
        Long b_p_id = response.getB_p_id();

        Bundle data_to_pass = new Bundle();
        data_to_pass.putLong(Constants.BUNDLE_B_R_ID, b_r_id);
        ViewBloodRequestFragment viewBloodRequestFragment = new ViewBloodRequestFragment();
        viewBloodRequestFragment.setArguments(data_to_pass);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.content_frame_main_screen, viewBloodRequestFragment);
        transaction.commit();
    }

    @Override
    public void handleResponse(Double lat, Double lng) {

        String patient_name = et_patient_name.getText().toString();
        Set<BloodGroupType> bloodGroupsList = getSelectedBloodGroups();
        String phone_number = et_phone_number.getText().toString();
        String description = et_description.getText().toString();
        String units = et_units.getText().toString();
        int selectedRadioButton = rg_blood_requirement_type.getCheckedRadioButtonId();
        BloodRequirementType blood_requirement_type = getBloodRequrirementType(selectedRadioButton);
        String place_string = atv_location.getText().toString();
        Double gps_lat = 0d;
        Double gps_long = 0d;
        Location location = getLocation();
        if(location!=null) {
            gps_lat = location.getLatitude();
            gps_long = location.getLongitude();
        }

        SaveBloodRequestRequest request = createRequest(patient_name,phone_number, description, units,
                bloodGroupsList,blood_requirement_type, place_string,gps_lat,gps_long, lat, lng );


        boolean isCorrect = validateRequestAndShowToast(request);
        if(!isCorrect)
            return;

        CreateBloodRequestAsyncTask task = new CreateBloodRequestAsyncTask(getActivity(), this);

        task.execute(request);

    }
}
