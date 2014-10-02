package org.redhelp.fagment;

/**
 * Created by harshis on 6/15/14.
 */


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;

import org.redhelp.adapter.items.TabItem;
import org.redhelp.adapter.items.TabsItem;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.AcceptBloodRequestRequest;
import org.redhelp.common.AcceptBloodRequestResponse;
import org.redhelp.common.GetBloodRequestResponse;
import org.redhelp.common.UserProfileCommonFields;
import org.redhelp.common.types.BloodGroupType;
import org.redhelp.common.types.BloodRequestType;
import org.redhelp.common.types.BloodRequirementType;
import org.redhelp.common.types.UpdateBloodRequestState;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;
import org.redhelp.dialogs.UpdateBRStateDialogFragment;
import org.redhelp.session.SessionManager;
import org.redhelp.task.AcceptBloodRequestTask;
import org.redhelp.task.GetBloodRequestAsyncTask;
import org.redhelp.types.Constants;
import org.redhelp.util.AndroidVersion;
import org.redhelp.util.DateHelper;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by harshis on 5/31/14.
 */
public class ViewBloodRequestFragment extends ProgressFragment
        implements TabsFragmentNew.ITabsFragment, GetBloodRequestAsyncTask.IBloodRequestListener,
        AcceptBloodRequestTask.IAcceptBloodRequestTaskListener, ErrorHandlerFragment.IErrorHandlerFragment, View.OnClickListener {

    private static final String TAG = "RedHelp:ViewBloodRequestFragment";
    private View fragmentContent;

    private ImageView iv_profile_pic;

    private TextView tv_required_by;
    private TextView tv_blood_groups;
    private TextView tv_patient_name;
    private TextView tv_description;
    private TextView tv_requirement_type;
    private TextView tv_creation_date;
    private TextView tv_hospital_location;
    private ScrollView sv_blood_request;

    private Button bt_accept_blood_request;
    private Button bt_call;
    private Button bt_navigate;
    private LinearLayout ll_call_navigate;

    private TabsItem tabs;

    private Long cached_b_p_id;
    private Long cached_b_r_id;

    private String cached_phone_number;
    private Double cached_place_gps_lat;
    private Double cached_place_gps_lng;

    private Button bt_update_state_inactive;

    private LinearLayout ll_inactive_layout;
    private GetBloodRequestResponse cachedRequestResponse;

    private Button bt_update_state_active;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_view_blood_request_layout, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseViews();

        Bundle data_received = getArguments();
        Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
        cached_b_p_id = b_p_id;

        if(data_received != null && b_p_id != null) {
            Long b_r_id = data_received.getLong(Constants.BUNDLE_B_R_ID);

            fetchAndShowData(b_r_id, b_p_id);
        }

        // Disable filter button
        setHasOptionsMenu(true);
        if(getActivity() instanceof HomeScreenActivity){
            ((HomeScreenActivity)getActivity()).showFilterMenu(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        if(!AndroidVersion.isBeforeHoneycomb())
            getActivity().getActionBar()
                    .setTitle("Blood Request");
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.menuid_filter);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Long b_r_id = data.getLongExtra("b_r_id", 0);

        if (b_r_id != null && b_r_id != 0) {
            Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
            fetchAndShowData(b_r_id, b_p_id);
        }
    }

    private void initialiseViews() {
        setContentView(fragmentContent);
        iv_profile_pic = (ImageView) getActivity().findViewById(R.id.iv_profile_pic_blood_request);


        tv_patient_name = (TextView) getActivity().findViewById(R.id.tv_patient_name_value_view_request);
        tv_description = (TextView) getActivity().findViewById(R.id.tv_description_view_request);
        tv_requirement_type = (TextView) getActivity().findViewById(R.id.tv_requirement_type_view_request);
        tv_creation_date = (TextView) getActivity().findViewById(R.id.tv_creation_date_view_request);
        tv_required_by = (TextView) getActivity().findViewById(R.id.tv_required_by_view_request);
        tv_blood_groups = (TextView) getActivity().findViewById(R.id.tv_blood_group_view_request);
        tv_hospital_location = (TextView) getActivity().findViewById(R.id.tv_hospital_location_blood_request);
        sv_blood_request  = (ScrollView) getActivity().findViewById(R.id.sv_blood_request_layout);

        bt_accept_blood_request = (Button) getActivity().findViewById(R.id.bt_accept_blood_request_fragment);
        bt_accept_blood_request.setVisibility(View.GONE);

        bt_call = (Button) getActivity().findViewById(R.id.bt_call_blood_request_layout);
        bt_navigate = (Button) getActivity().findViewById(R.id.bt_navigate_blood_request_layout);
        bt_call.setOnClickListener(this);
        bt_navigate.setOnClickListener(this);

        ll_call_navigate = (LinearLayout) getActivity().findViewById(R.id.ll_call_navigate_layout);

        ll_inactive_layout = (LinearLayout) getActivity().findViewById(R.id.ll_inactive_view_request_fragment);
        bt_update_state_inactive = (Button) getActivity().findViewById(R.id.bt_inactive_update_state_view_request_layout);
        bt_update_state_inactive.setOnClickListener(this);
        ll_inactive_layout.setVisibility(View.INVISIBLE);


        bt_update_state_active = (Button) getActivity().findViewById(R.id.bt_update_visible_blood_request_fragment);
        bt_update_state_active.setOnClickListener(this);
        bt_update_state_active.setVisibility(View.GONE);
    }


    private void fetchAndShowData(Long b_r_id, Long b_p_id) {
        setContentShown(false);
        cached_b_r_id = b_r_id;
        GetBloodRequestAsyncTask getBloodRequestAsyncTask = new GetBloodRequestAsyncTask(getActivity(), this);
        getBloodRequestAsyncTask.execute(b_r_id, b_p_id);
    }



    @Override
    public TabsItem getTabs() {
        return tabs;
    }

    @Override
    public void handleGetBloodRequestError(Exception e) {
        try {
            ErrorHandlerFragment errorHandlerFragment = ErrorHandlerFragment.createErrorHandlerFragmentInstance("", this);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame_main_screen, errorHandlerFragment);
            transaction.commit();
        } catch (Exception exp){

        }

    }

    @Override
    public void handleGetBloodRequestResponse(GetBloodRequestResponse response) {
        cachedRequestResponse = response;

        Long b_r_id = response.getB_r_id();
        String patient_name = response.getPatient_name();
        String phone_number = response.getPhone_number();
        String description = response.getDescription();
        String units = response.getUnits();
        String blood_grps_str = response.getBlood_groups_str();
        String hospitalLocaiton = response.getPlace_string();
        BloodRequirementType requirement_type = response.getBlood_requirement_type();
        BloodRequestType bloodRequestType = response.getBloodRequestType();

        String blood_requirement_type_str = "-";
        if(requirement_type != null )
            blood_requirement_type_str = requirement_type.toString();
        String creation_date_str = response.getCreation_datetime();
        creation_date_str = DateHelper.getISTTime(creation_date_str);


        UserProfileCommonFields creator_profile = response.getCreator_profile();
        String creator_name = null;
        byte[] profile_pic = null;
        if(creator_profile!= null) {
            creator_name = creator_profile.getName();
            profile_pic = creator_profile.getUser_image();
        }
        String required_by_str = "-";
        if(creator_name != null)
            required_by_str = String.format("%s", creator_name);

        boolean active = response.isActive();

        Set<UserProfileCommonFields> blood_request_receivers_profiles = response.getBlood_request_receivers_profiles();
        Set<BloodGroupType> bloodGroupTypes = response.getSet_blood_group();

        Double gps_lat = response.getGps_location_lat();
        Double gps_long = response.getGps_location_long();
        Double place_lat = response.getPlace_location_lat();
        Double place_long = response.getPlace_location_long();
        String place_string = response.getPlace_string();

        TabsItem tabs = createTabs(response);

        UpdateBloodRequestState bloodRequestState = response.getBloodRequestState();

        handleBloodRequestState(bloodRequestState);

        showUpperData(b_r_id,
                    profile_pic,
                required_by_str, blood_grps_str,
                patient_name, description,
                blood_requirement_type_str,
                creation_date_str, hospitalLocaiton,
                phone_number, units, bloodGroupTypes, bloodRequestType, place_lat, place_long,  tabs);
        setContentShown(true);

    }

    private void handleBloodRequestState(UpdateBloodRequestState bloodRequestState) {

        if(bloodRequestState != null && (bloodRequestState.equals(UpdateBloodRequestState.ACTIVE)
                || bloodRequestState.equals(UpdateBloodRequestState.EXTENDED))) {
            ll_inactive_layout.setVisibility(View.INVISIBLE);
        } else {
            ll_inactive_layout.setVisibility(View.VISIBLE);
        }
    }

    public void showUpperData(
                         final Long b_r_id,
                         byte[] profile_pic,
                         String required_by_str,
                         String blood_grps_str,
                         String patient_name,
                         String description,
                         String requirement_type_str,
                         String creation_date_str,
                         String hospitalLocaiton,
                         String phone_number,
                         String units,
                         Set<BloodGroupType> bloodGroupTypeSet,
                         BloodRequestType bloodRequestType,
                         Double place_lat,
                         Double place_lng,
                         TabsItem tabs) {
        if(profile_pic != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profile_pic);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            iv_profile_pic.setImageBitmap(bitmap);
        }

        if(phone_number != null) {
            cached_phone_number = phone_number;
        }

        if(place_lat != null && place_lng != null) {
            cached_place_gps_lat = place_lat;
            cached_place_gps_lng = place_lng;
        }


        tv_required_by.setText(required_by_str);
        tv_blood_groups.setText(blood_grps_str);
        tv_patient_name.setText(patient_name);
        tv_description.setText(description);
        tv_requirement_type.setText(requirement_type_str);
        tv_creation_date.setText(creation_date_str);
        tv_hospital_location.setText(hospitalLocaiton);


        if(BloodRequestType.REQUESTED.equals(bloodRequestType)) {
            bt_accept_blood_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Long b_p_id = SessionManager.getSessionManager(getActivity()).getBPId();
                    accessRequestButtonHandler(b_p_id, b_r_id);

                }
            });
            bt_accept_blood_request.setVisibility(View.VISIBLE);
        } else if(BloodRequestType.ACCEPTED.equals(bloodRequestType)) {
            setBloodRequestButtonAccepted();
        }


        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        this.tabs = tabs;

        if(!AndroidVersion.isBeforeHoneycomb()) {
            TabsFragmentNew tabsFragment = TabsFragmentNew.createTabsFragmentInstance(this);
            transaction.add(R.id.fl_blood_request_tabs_view_request_layout, tabsFragment);
            transaction.commit();
        }
        sv_blood_request.fullScroll(ScrollView.FOCUS_UP);
    }



    private void accessRequestButtonHandler(Long b_p_id, Long b_r_id) {
        AcceptBloodRequestRequest acceptRequest = new AcceptBloodRequestRequest();
        acceptRequest.setB_p_id(b_p_id);
        acceptRequest.setB_r_id(b_r_id);

        AcceptBloodRequestTask acceptBloodRequestTask = new AcceptBloodRequestTask(getActivity(), this);
        acceptBloodRequestTask.execute(acceptRequest);
    }

    private void setBloodRequestButtonAccepted() {
        bt_accept_blood_request.setText("Accepted");
        bt_accept_blood_request.setEnabled(false);
        bt_accept_blood_request.setVisibility(View.VISIBLE);
    }

    private TabsItem createTabs(GetBloodRequestResponse response) {
        LinkedList<TabItem> tabsItemList = new LinkedList<TabItem>();

        if(BloodRequestType.CREATOR.equals(response.getBloodRequestType())) {
            ll_call_navigate.setVisibility(View.INVISIBLE);
            bt_update_state_active.setVisibility(View.VISIBLE);

            BloodProfileDataWrapper dataWrapper = getDataWrapper(response.getBlood_request_receivers_profiles());
            Fragment bloodProfileListFragment = BloodProfileListFragment.createBloodProfileListFragmentInstance(dataWrapper);
            TabItem bloodRequestTab = new TabItem("Request sent", 1, bloodProfileListFragment, 12);
            tabsItemList.add(bloodRequestTab);
        }


            Fragment lastKnownLocationMapTabFragment = null;
            if (response.getGps_location_lat() != null && response.getGps_location_long() != null) {
                lastKnownLocationMapTabFragment = LastKnownLocationMapTabFragment.
                        createLastKnownLocationMapTabFragmentInstance(response.getPlace_location_lat(),
                                response.getPlace_location_long());
                TabItem lastKnownLocationTab = new TabItem(getResources().getString
                        (R.string.location_blood_profile_tabs), 1, lastKnownLocationMapTabFragment, 12);


                tabsItemList.add(lastKnownLocationTab);
            } else {
                //TODO show empty fragment here.
            }



        TabsItem tabs = new TabsItem();
        tabs.tabs = tabsItemList;
        return (tabs);
    }

    private BloodProfileDataWrapper getDataWrapper(Set<UserProfileCommonFields> blood_request_receivers_profiles) {
        BloodProfileDataWrapper dataWrapper = new BloodProfileDataWrapper();
        Set<BloodProfileListData> bloodProfileListDataSortedSet = new HashSet<BloodProfileListData>();
        for(UserProfileCommonFields userProfileCommonFields:blood_request_receivers_profiles){
            BloodProfileListData bloodProfileListData = new BloodProfileListData();
            bloodProfileListData.b_p_id = userProfileCommonFields.getB_p_id();
            bloodProfileListData.profile_pic = userProfileCommonFields.getUser_image();
            bloodProfileListData.blood_grp = userProfileCommonFields.getBlood_group_type().toString();
            if(userProfileCommonFields.getIsRequestAccepted() != null)
                bloodProfileListData.isRequestAccepted = userProfileCommonFields.getIsRequestAccepted();
            bloodProfileListData.title = userProfileCommonFields.getName();

            bloodProfileListDataSortedSet.add(bloodProfileListData);
        }
        dataWrapper.bloodProfileListDataSortedSet = bloodProfileListDataSortedSet;
        return dataWrapper;
    }


    @Override
    public void handleAcceptBloodRequestError() {


    }

    @Override
    public void handleAcceptBloodRequestResponse(AcceptBloodRequestResponse response) {
        if(response.isResponse()) {
            setBloodRequestButtonAccepted();
        }
    }

    @Override
    public void onRefreshButtonClickHandler() {
        GetBloodRequestAsyncTask getBloodRequestAsyncTask = new GetBloodRequestAsyncTask(getActivity(), this);
        getBloodRequestAsyncTask.execute(cached_b_r_id, cached_b_p_id);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bt_call_blood_request_layout :
                if(cached_phone_number == null)
                    break;
                String uri = "tel:" + cached_phone_number.trim() ;
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                break;
            case R.id.bt_navigate_blood_request_layout :
                if(cached_place_gps_lat == null || cached_place_gps_lng == null)
                    break;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
                        +cached_place_gps_lat
                        +","+cached_place_gps_lng));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.bt_inactive_update_state_view_request_layout :
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("UpdateRequestDialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                if(cachedRequestResponse != null) {
                    UpdateBRStateDialogFragment updateBRStateDialogFragment =
                            UpdateBRStateDialogFragment.getInstance(cachedRequestResponse);
                    updateBRStateDialogFragment.setTargetFragment(this, 1);
                    updateBRStateDialogFragment.show(ft, "UpdateRequestDialog");
                }
                break;


            case R.id.bt_update_visible_blood_request_fragment :
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                Fragment prev1 = getFragmentManager().findFragmentByTag("UpdateRequestDialog");
                if (prev1 != null) {
                    ft1.remove(prev1);
                }
                ft1.addToBackStack(null);

                // Create and show the dialog.
                if(cachedRequestResponse != null) {

                    UpdateBRStateDialogFragment updateBRStateDialogFragment =
                            UpdateBRStateDialogFragment.getInstance(cachedRequestResponse);
                    updateBRStateDialogFragment.setTargetFragment(this, 2);
                    updateBRStateDialogFragment.show(ft1, "UpdateRequestDialog");
                }
                break;
        }
    }
}