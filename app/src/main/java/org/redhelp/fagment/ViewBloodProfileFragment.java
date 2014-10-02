package org.redhelp.fagment;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.redhelp.common.GetBloodProfileAccessRequest;
import org.redhelp.common.GetBloodProfileAccessResponse;
import org.redhelp.common.GetBloodProfileAccessResponseRequest;
import org.redhelp.common.GetBloodProfileAccessResponseResponse;
import org.redhelp.common.GetBloodProfileRequest;
import org.redhelp.common.GetBloodProfileResponse;
import org.redhelp.common.GetBloodRequestResponse;
import org.redhelp.common.GetEventResponse;
import org.redhelp.common.types.GetBloodProfileAccessResponseType;
import org.redhelp.common.types.GetBloodProfileType;
import org.redhelp.data.BloodRequestDataWrapper;
import org.redhelp.data.BloodRequestListData;
import org.redhelp.data.EventDataWrapper;
import org.redhelp.data.EventListData;
import org.redhelp.session.SessionManager;
import org.redhelp.task.AcceptBloodProfileRequestAsyncTask;
import org.redhelp.task.RequestBloodProfileAccessTask;
import org.redhelp.task.ViewBloodProfileAsyncTask;
import org.redhelp.util.AndroidVersion;
import org.redhelp.util.LocationHelper;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by harshis on 5/31/14.
 */
public class ViewBloodProfileFragment extends ProgressFragment
        implements TabsFragmentNew.ITabsFragment, ViewBloodProfileAsyncTask.IViewBloodProfileAsyncTaskListener,
        RequestBloodProfileAccessTask.IRequestBloodProfileAccessTaskListener, AcceptBloodProfileRequestAsyncTask.IAcceptBloodProfileRequestAsyncTaskListener{
    private static final String TAG = "RedHelp:ViewBloodProfileFragment";

    public static final String VIEW_BP_TAG = "ViewBpTag";
    private static final String BUNDLE_B_P_ID = "b_p_id";
    private static final String BUNDLE_REQUESTER_B_P_ID = "requester_b_p_id";

    private View fragmentContent;

    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_contact_number;
    private TextView tv_blood_group;
    private ImageView iv_profile_pic;
    private LinearLayout ll_description;
    private ScrollView sv_blood_request;

    private Button bt_request_blood;

    private TabsItem tabs;

    public static ViewBloodProfileFragment createViewBloodProfileFragmentInstance(Long requester_b_p_id,
                                                                                  Long b_p_id) {
        ViewBloodProfileFragment viewBloodProfileFragment =  new ViewBloodProfileFragment();
        Bundle content = new Bundle();
        content.putLong(BUNDLE_B_P_ID, b_p_id);
        content.putLong(BUNDLE_REQUESTER_B_P_ID, requester_b_p_id);
        viewBloodProfileFragment.setArguments(content);
        return viewBloodProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_bloodprofile_layout, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        if(!AndroidVersion.isBeforeHoneycomb())
            getActivity().getActionBar()
                    .setTitle("Donor Details");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseViews();

        Bundle data_received = getArguments();
        if(data_received != null) {

            Long b_p_id = data_received.getLong(BUNDLE_B_P_ID);
            Long requester_b_p_id = data_received.getLong(BUNDLE_REQUESTER_B_P_ID);

            fetchAndShowData(b_p_id, requester_b_p_id);
        }

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


    private void initialiseViews() {
        setContentView(fragmentContent);
        tv_name = (TextView) getActivity().findViewById(R.id.tv_name_blood_profile_layout);
        tv_email = (TextView) getActivity().findViewById(R.id.tv_email_blood_profile_layout);
        tv_contact_number = (TextView) getActivity().findViewById(R.id.tv_phone_number_blood_profile_layout);
        tv_blood_group = (TextView) getActivity().findViewById(R.id.tv_blood_group_blood_profile_layout);

        iv_profile_pic = (ImageView) getActivity().findViewById(R.id.iv_profile_pic_blood_profile_layout);
        bt_request_blood = (Button) getActivity().findViewById(R.id.bt_request_blood_bloodprofile);
        ll_description = (LinearLayout) getActivity().findViewById(R.id.ll_email_contact_blood_profile);
        ll_description.setVisibility(View.GONE);

        bt_request_blood.setVisibility(View.GONE);

        sv_blood_request = (ScrollView) getActivity().findViewById(R.id.sv_bood_profile_layout);
    }


    private void fetchAndShowData(Long b_p_id, Long requester_b_p_id) {
        setContentShown(false);

        GetBloodProfileRequest getBloodProfileRequest = new GetBloodProfileRequest();
        getBloodProfileRequest.setB_p_id(b_p_id);
        getBloodProfileRequest.setRequester_b_p_id(requester_b_p_id);

        ViewBloodProfileAsyncTask viewBloodProfileAsyncTask = new ViewBloodProfileAsyncTask(getActivity(), this);

        viewBloodProfileAsyncTask.execute(getBloodProfileRequest);

    }

    public void showData(String name, String email, String phone_number, String blood_group, TabsItem tabs, byte[] profile_pic) {
        tv_name.setText(name);
        tv_email.setText(email);
        tv_contact_number.setText(phone_number);
        tv_blood_group.setText(blood_group);

        if(profile_pic != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profile_pic);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            iv_profile_pic.setImageBitmap(bitmap);
        }

        if(!AndroidVersion.isBeforeHoneycomb()) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            this.tabs = tabs;

            TabsFragmentNew tabsFragment = TabsFragmentNew.createTabsFragmentInstance(this);
            transaction.add(R.id.fl_blood_profile_tabs_blood_profile_layout, tabsFragment);
            transaction.commit();
        }
        sv_blood_request.fullScroll(ScrollView.FOCUS_UP);
    }

    @Override
    public TabsItem getTabs() {
        return tabs;
    }


    // This function creates tabs shown in Blood Profile
    // 1.) Events attending.
    // 2.) Blood Requests.
    // 3.) Last Known location.
    private TabsItem createTabs(GetBloodProfileResponse response) {
        LinkedList<TabItem> tabsItemList = new LinkedList<TabItem>();

        GetBloodProfileType responseType = response.getResponse_type();
        if(responseType == null)
            return null;

        // Events Attending is visible to every response type

        EventDataWrapper eventDataWrapper = getEventWrapperFromEventList(response.getEvent_response());
        EventsListFragment eventsListFragment = EventsListFragment.createEventsListFragmentInstance(eventDataWrapper);

        TabItem eventsAttendingTab = new TabItem("Events Attending", 1, eventsListFragment, 12);
        tabsItemList.add(eventsAttendingTab);

        // Blood Request tab is visible to every response type.
        BloodRequestDataWrapper dataWrapper = getBloodRequestWrapperFromList(response.getBlood_requests());
        BloodRequestListFragment bloodRequestListFragment = BloodRequestListFragment.createBloodRequestListFragmentInstance(dataWrapper);

        TabItem bloodRequestTab = new TabItem(getResources().getString
                (R.string.bloodrequests_blood_profile_tabs), 2, bloodRequestListFragment, 12);
        tabsItemList.add(bloodRequestTab);

        // Last known location is visible only to Person.
        if(responseType.equals(GetBloodProfileType.OWN)) {
            Fragment lastKnownLocationMapTabFragment = null;
            if (response.getLast_known_location_lat() != null && response.getLast_known_location_long() != null) {
                lastKnownLocationMapTabFragment = LastKnownLocationMapTabFragment.
                        createLastKnownLocationMapTabFragmentInstance(response.getLast_known_location_lat(),
                                response.getLast_known_location_long());
                TabItem lastKnownLocationTab = new TabItem(getResources().getString
                        (R.string.location_blood_profile_tabs), 3, lastKnownLocationMapTabFragment, 12);
                tabsItemList.add(lastKnownLocationTab);
            } else {
                //TODO show empty fragment here.
            }
        }
        TabsItem tabs = new TabsItem();
        tabs.tabs = tabsItemList;
        return (tabs);
    }

    @Override
    public void handleViewBloodProfileError() {

    }

    @Override
    public void handleViewBloodProfileResponse(GetBloodProfileResponse response) {
        if(response == null)
            return;

        GetBloodProfileType responseType = response.getResponse_type();
        if(GetBloodProfileType.PUBLIC.equals(responseType)) {
            ll_description.setVisibility(View.GONE);
            showRequestButtonRequestAction(response.getB_p_id());
        }
        else if(GetBloodProfileType.PUBLIC_VIEW_PROFILE_PENDING.equals(responseType) ||
                GetBloodProfileType.PUBLIC_BLOOD_REQUEST_PENDING.equals(responseType))
            showRequestButtonDisabled("Blood Requested");
        else if(GetBloodProfileType.PRIVATE.equals(responseType)) {
            ll_description.setVisibility(View.VISIBLE);
            showRequestButtonDisabled("Blood Request Accepted");
        }
        else if(GetBloodProfileType.PUBLIC_VIEW_PROFILE_REQUESTEE.equals(responseType))
            showRequestButtonResponseAction(response.getB_p_id());

        String name = response.getName();
        String email = response.getEmail();
        String phone_number = response.getPhone_number();
        String blood_group = response.getBlood_group_type().toString();
        byte[] user_image = response.getUser_image();


        TabsItem tabs = createTabs(response);

        showData(name, email, phone_number, blood_group, tabs, user_image);
        setContentShown(true);
    }

    private void showRequestButtonRequestAction(Long b_p_id) {
        Long requester_b_p_id  = SessionManager.getSessionManager(getActivity()).getBPId();
        final RequestBloodProfileAccessTask requestBloodProfileAccessTask = new RequestBloodProfileAccessTask(getActivity(), this);
        final GetBloodProfileAccessRequest request = new GetBloodProfileAccessRequest();
        request.setRequester_b_p_id(requester_b_p_id);
        request.setReceiver_b_p_id(b_p_id);
        bt_request_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               requestBloodProfileAccessTask.execute(request);
            }
        });
        bt_request_blood.setVisibility(View.VISIBLE);
    }

    private void showRequestButtonResponseAction(Long b_p_id) {
        Long requestee_b_p_id  = SessionManager.getSessionManager(getActivity()).getBPId();
        final AcceptBloodProfileRequestAsyncTask acceptBloodProfileRequestAsyncTask = new AcceptBloodProfileRequestAsyncTask(getActivity(), this);
        final GetBloodProfileAccessResponseRequest request = new GetBloodProfileAccessResponseRequest();
        request.setRequester_b_p_id(b_p_id);
        request.setRequestee_b_p_id(requestee_b_p_id);
        bt_request_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptBloodProfileRequestAsyncTask.execute(request);
            }
        });
        bt_request_blood.setText("Approve Request");
        bt_request_blood.setVisibility(View.VISIBLE);
    }


    private void showRequestButtonDisabled(String msg) {
        bt_request_blood.setText(msg);
        bt_request_blood.setVisibility(View.VISIBLE);
        bt_request_blood.setEnabled(false);
    }

    @Override
    public void handleRequestBloodProfileAccessError() {

    }

    @Override
    public void handleBeforeBloodProfileResponse() {
        showRequestButtonDisabled("Requesting...");
    }


    @Override
    public void handleRequestBloodProfileAccessResponse(GetBloodProfileAccessResponse response) {
        if(GetBloodProfileAccessResponseType.REQUEST_POSTED_SUCCESSFULLY.equals(response.getAccessResponseType()))
            showRequestButtonDisabled("Blood Requested");
    }

    @Override
    public void handleAcceptBloodProfileRequestAccessError() {

    }

    @Override
    public void handleAcceptBloodProfileRequestResponse() {

    }

    @Override
    public void handleAcceptBloodProfileRequestResponse(GetBloodProfileAccessResponseResponse response) {
        if(response.isDone()) {
            showRequestButtonDisabled("Accepted, Please refresh");
        }
    }


    private BloodRequestDataWrapper getBloodRequestWrapperFromList(LinkedList<GetBloodRequestResponse> bloodRequestResponses) {
        if(bloodRequestResponses == null)
            return null;
        BloodRequestDataWrapper bloodRequestDataWrapper = new BloodRequestDataWrapper();
        Set<BloodRequestListData> bloodRequestListDataSet = new HashSet<BloodRequestListData>();
        for(GetBloodRequestResponse bloodRequest : bloodRequestResponses) {
            if(bloodRequest == null)
                continue;

            BloodRequestListData bloodRequestListData = new BloodRequestListData();
            bloodRequestListData.b_r_id = bloodRequest.getB_r_id();
            bloodRequestListData.title = bloodRequest.getDescription();
            bloodRequestListData.venueStr = "-";
            bloodRequestListData.bloodGroupsStr = bloodRequest.getBlood_groups_str();

            if(bloodRequest.getPlace_location_lat() != null && bloodRequest.getPlace_location_long() != null &&
                    LocationHelper.userCurrentLocationLat != null && LocationHelper.userCurrentLocationLng != null) {
                Double bloodRequestLocationLat = bloodRequest.getPlace_location_lat();
                Double bloodRequestLocationLng = bloodRequest.getPlace_location_long();
                String distance = LocationHelper.calculateDistanceString(LocationHelper.userCurrentLocationLat, LocationHelper.userCurrentLocationLng,
                        bloodRequestLocationLat, bloodRequestLocationLng);
                bloodRequestListData.distance = distance;
            }

            bloodRequestListDataSet.add(bloodRequestListData);
        }
        bloodRequestDataWrapper.bloodRequestListDataList = bloodRequestListDataSet;
        return bloodRequestDataWrapper;
    }


    private EventDataWrapper getEventWrapperFromEventList(LinkedList<GetEventResponse> eventResponseLinkedList) {
        if(eventResponseLinkedList == null)
            return null;
        EventDataWrapper eventDataWrapper = new EventDataWrapper();
        Set<EventListData> eventListDataSet = new HashSet<EventListData>();
        for(GetEventResponse eventResponse : eventResponseLinkedList) {
            if(eventResponse == null)
                continue;

            EventListData eventListData = new EventListData();

            eventListData.e_id = eventResponse.getE_id();
            eventListData.title = eventResponse.getName();
            eventListData.venue = eventResponse.getLocation_address();
            eventListData.scheduled_date = eventResponse.getCreation_datetime();

            if(eventResponse.getLocation_lat() != null && eventResponse.getLocation_long() != null &&
                    LocationHelper.userCurrentLocationLat != null && LocationHelper.userCurrentLocationLng != null) {
                Double eventLocationLat = eventResponse.getLocation_lat();
                Double eventLocationLng = eventResponse.getLocation_long();
                String distance = LocationHelper.calculateDistanceString(LocationHelper.userCurrentLocationLat, LocationHelper.userCurrentLocationLng,
                        eventLocationLat, eventLocationLng);
                eventListData.distance = distance;
            }

            eventListDataSet.add(eventListData);
        }
        eventDataWrapper.eventListDataSet = eventListDataSet;
        return eventDataWrapper;
    }
}
