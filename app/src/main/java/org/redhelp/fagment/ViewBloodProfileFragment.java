package org.redhelp.fagment;

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
import org.redhelp.common.GetBloodProfileAccessRequest;
import org.redhelp.common.GetBloodProfileAccessResponse;
import org.redhelp.common.GetBloodProfileRequest;
import org.redhelp.common.GetBloodProfileResponse;
import org.redhelp.common.types.GetBloodProfileType;
import org.redhelp.session.SessionManager;
import org.redhelp.task.RequestBloodProfileAccessTask;
import org.redhelp.task.ViewBloodProfileAsyncTask;

import java.util.LinkedList;

/**
 * Created by harshis on 5/31/14.
 */
public class ViewBloodProfileFragment extends ProgressFragment
        implements TabsFragmentNew.ITabsFragment, ViewBloodProfileAsyncTask.IViewBloodProfileAsyncTaskListener,
        RequestBloodProfileAccessTask.IRequestBloodProfileAccessTaskListener{

    private static final String TAG = "RedHelp:ViewBloodProfileFragment";
    private static final String BUNDLE_B_P_ID = "b_p_id";
    private static final String BUNDLE_REQUESTER_B_P_ID = "requester_b_p_id";

    private View fragmentContent;

    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_contact_number;
    private TextView tv_blood_group;
    private ImageView iv_profile_pic;

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
    }

    private void initialiseViews() {
        setContentView(fragmentContent);
        tv_name = (TextView) getActivity().findViewById(R.id.tv_name_blood_profile_layout);
        tv_email = (TextView) getActivity().findViewById(R.id.tv_email_blood_profile_layout);
        tv_contact_number = (TextView) getActivity().findViewById(R.id.tv_phone_number_blood_profile_layout);
        tv_blood_group = (TextView) getActivity().findViewById(R.id.tv_blood_group_blood_profile_layout);

        iv_profile_pic = (ImageView) getActivity().findViewById(R.id.iv_profile_pic_blood_profile_layout);
        bt_request_blood = (Button) getActivity().findViewById(R.id.bt_request_blood_bloodprofile);

        bt_request_blood.setVisibility(View.GONE);
    }


    private void fetchAndShowData(Long b_p_id, Long requester_b_p_id) {
        setContentShown(false);

        GetBloodProfileRequest getBloodProfileRequest = new GetBloodProfileRequest();
        getBloodProfileRequest.setB_p_id(b_p_id);
        getBloodProfileRequest.setRequester_b_p_id(requester_b_p_id);

        ViewBloodProfileAsyncTask viewBloodProfileAsyncTask = new ViewBloodProfileAsyncTask(getActivity(), this);

        viewBloodProfileAsyncTask.execute(getBloodProfileRequest);

    }

    public void showData(String name, String email, String phone_number, String blood_group, TabsItem tabs) {
        tv_name.setText(name);
        tv_email.setText(email);
        tv_contact_number.setText(phone_number);
        tv_blood_group.setText(blood_group);


        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        this.tabs = tabs;

        TabsFragmentNew tabsFragment = TabsFragmentNew.createTabsFragmentInstance(this);
        transaction.add(R.id.fl_blood_profile_tabs_blood_profile_layout, tabsFragment);
        transaction.commit();
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
        TabItem eventsAttendingTab = new TabItem("Events Attending", 1, new FacebookLoginFragment(), 12);
        tabsItemList.add(eventsAttendingTab);

        // Blood Request tab is visible to every response type.
        TabItem bloodRequestTab = new TabItem(getResources().getString
                (R.string.bloodrequests_blood_profile_tabs), 2, new FacebookLoginFragment(), 12);
        tabsItemList.add(bloodRequestTab);

        // Last known location is visible only to Person.
        if(responseType.equals(GetBloodProfileType.OWN)) {
            Fragment lastKnownLocationMapTabFragment = null;
            if (response.getLast_known_location_lat() != null && response.getLast_known_location_long() != null) {
                lastKnownLocationMapTabFragment = LastKnownLocationMapTabFragment.
                        createLastKnownLocationMapTabFragmentInstance(response.getLast_known_location_lat(),
                                response.getLast_known_location_long());
            } else {
                //TODO show empty fragment here.
            }

            TabItem lastKnownLocationTab = new TabItem(getResources().getString
                    (R.string.location_blood_profile_tabs), 3, lastKnownLocationMapTabFragment, 12);
            tabsItemList.add(lastKnownLocationTab);
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

        GetBloodProfileType responseType = response.getResponse_type();
        Log.d(TAG, "ResposneType: " + responseType.toString());

        if(responseType != null && responseType.equals(GetBloodProfileType.PUBLIC))
            showRequestButton(response.getB_p_id());

        String name = response.getName();
        String email = response.getEmail();
        String phone_number = response.getPhone_number();
      //  String blood_group = response.getBlood_group_type().toString();

        String blood_group = "AB+";
        TabsItem tabs = createTabs(response);

        showData(name, email, phone_number, blood_group, tabs);
        setContentShown(true);
    }

    private void showRequestButton(Long b_p_id) {
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

    @Override
    public void handleRequestBloodProfileAccessError() {

    }

    @Override
    public void handleRequestBloodProfileAccessResponse(GetBloodProfileAccessResponse response) {
        Log.e(TAG, "handleRequestBloodProfileAccessResponse, response:"+response.toString());
    }
}
