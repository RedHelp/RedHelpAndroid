package org.redhelp.fagment;

/**
 * Created by harshis on 6/15/14.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;

import org.redhelp.adapter.items.TabItem;
import org.redhelp.adapter.items.TabsItem;
import org.redhelp.app.R;
import org.redhelp.common.AcceptBloodRequestRequest;
import org.redhelp.common.AcceptBloodRequestResponse;
import org.redhelp.common.GetBloodRequestResponse;
import org.redhelp.common.UserProfileCommonFields;
import org.redhelp.common.types.BloodGroupType;
import org.redhelp.common.types.BloodRequestType;
import org.redhelp.common.types.BloodRequirementType;
import org.redhelp.data.BloodProfileDataWrapper;
import org.redhelp.data.BloodProfileListData;
import org.redhelp.session.SessionManager;
import org.redhelp.task.AcceptBloodRequestTask;
import org.redhelp.task.GetBloodRequestAsyncTask;
import org.redhelp.types.Constants;
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
        AcceptBloodRequestTask.IAcceptBloodRequestTaskListener, ErrorHandlerFragment.IErrorHandlerFragment {

    private static final String TAG = "RedHelp:ViewBloodRequestFragment";
    private View fragmentContent;

    private ImageView iv_profile_pic;

    private TextView tv_required_by;
    private TextView tv_blood_groups;
    private TextView tv_patient_name;
    private TextView tv_description;
    private TextView tv_requirement_type;
    private TextView tv_creation_date;

    private Button bt_accept_blood_request;

    private TabsItem tabs;

    private Long cached_b_p_id;
    private Long cached_b_r_id;


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

        bt_accept_blood_request = (Button) getActivity().findViewById(R.id.bt_accept_blood_request_fragment);
        bt_accept_blood_request.setVisibility(View.GONE);


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

        Long b_r_id = response.getB_r_id();
        String patient_name = response.getPatient_name();
        String phone_number = response.getPhone_number();
        String description = response.getDescription();
        String units = response.getUnits();
        String blood_grps_str = response.getBlood_groups_str();
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

        showUpperData(b_r_id,
                    profile_pic,
                required_by_str, blood_grps_str,
                patient_name, description,
                blood_requirement_type_str,
                creation_date_str,
                phone_number, units, bloodGroupTypes, blood_request_receivers_profiles, bloodRequestType,  tabs);
        setContentShown(true);

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
                         String phone_number,
                         String units,
                         Set<BloodGroupType> bloodGroupTypeSet,
                         Set<UserProfileCommonFields> userProfileCommonFieldes,
                         BloodRequestType bloodRequestType,
                         TabsItem tabs) {
        if(profile_pic != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profile_pic);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if(bitmap == null)
                Log.e(TAG, "bitmap is null");
            iv_profile_pic.setImageBitmap(bitmap);
        }

        tv_required_by.setText(required_by_str);
        tv_blood_groups.setText(blood_grps_str);
        tv_patient_name.setText(patient_name);
        tv_description.setText(description);
        tv_requirement_type.setText(requirement_type_str);
        tv_creation_date.setText(creation_date_str);


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

        TabsFragmentNew tabsFragment = TabsFragmentNew.createTabsFragmentInstance(this);
        transaction.add(R.id.fl_blood_request_tabs_view_request_layout, tabsFragment);
        transaction.commit();
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
            BloodProfileDataWrapper dataWrapper = getDataWrapper(response.getBlood_request_receivers_profiles());
            Fragment bloodProfileListFragment = BloodProfileListFragment.createBloodProfileListFragmentInstance(dataWrapper);


            TabItem bloodRequestTab = new TabItem("Request sent", 1, bloodProfileListFragment, 12);
            tabsItemList.add(bloodRequestTab);
        }

        Fragment lastKnownLocationMapTabFragment = null;
        if(response.getGps_location_lat()!= null && response.getGps_location_long()!=null) {
            lastKnownLocationMapTabFragment = LastKnownLocationMapTabFragment.
                    createLastKnownLocationMapTabFragmentInstance(response.getGps_location_lat(),
                            response.getGps_location_long());
        } else {
            //TODO show empty fragment here.
        }

        TabItem lastKnownLocationTab = new TabItem(getResources().getString
                (R.string.location_blood_profile_tabs), 1, lastKnownLocationMapTabFragment, 12);


        tabsItemList.add(lastKnownLocationTab);
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
}
