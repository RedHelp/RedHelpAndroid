package org.redhelp.fagment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;

import org.redhelp.app.R;
import org.redhelp.app.SplashScreenActivity;
import org.redhelp.common.GetUserAccountResponse;
import org.redhelp.common.UserProfileCommonFields;
import org.redhelp.session.SessionManager;
import org.redhelp.task.GetUserAccountDetailsAsyncTask;

import java.io.ByteArrayInputStream;

/**
 * Created by harshis on 7/30/14.
 */
public class MyAccountFragment extends ProgressFragment implements
        GetUserAccountDetailsAsyncTask.IGetUserAccountDetailsListener{
    private final String TAG = "RedHelp:MyAccountFragment";

    private static final String BUNDLE_U_ID = "u_id";
    public static MyAccountFragment getMyAccountFragmentInstance(Long u_id) {
        MyAccountFragment myAccountFragment =  new MyAccountFragment();
        Bundle content = new Bundle();
        content.putLong(BUNDLE_U_ID, u_id);
        myAccountFragment.setArguments(content);
        return myAccountFragment;
    }

    private View fragmentContent;


    private TextView tv_name;
    private TextView tv_email;
    private TextView tv_contact_number;
    private ImageView iv_profile_pic;

    private Button bt_logout_button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.fragment_myaccount_layout, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseViews();

        Bundle data_received = getArguments();
        if(data_received != null) {
            Long u_id = data_received.getLong(BUNDLE_U_ID);
            fetchAndShowData(u_id);
        } else {
            //TODO show error activity or retry.
        }
    }

    private void initialiseViews() {
        setContentView(fragmentContent);
        tv_name = (TextView) getActivity().findViewById(R.id.tv_name_myaccount_layout);
        tv_email = (TextView) getActivity().findViewById(R.id.tv_email_myaccount_layout);
        tv_contact_number = (TextView) getActivity().findViewById(R.id.tv_phone_number_myaccount_layout);
        iv_profile_pic = (ImageView) getActivity().findViewById(R.id.iv_profile_pic_myaccount_layout);
        bt_logout_button = (Button) getActivity().findViewById(R.id.bt_logout_myaccount_layout);
        bt_logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager manager = SessionManager.getSessionManager(getActivity());
                manager.logoutUser();
                Intent intent_splash = new Intent(getActivity(), SplashScreenActivity.class);
                intent_splash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_splash);
            }
        });
    }

    private void fetchAndShowData(Long u_id) {
        setContentShown(false);
        GetUserAccountDetailsAsyncTask getUserAccountDetailsAsyncTask = new GetUserAccountDetailsAsyncTask(this, getActivity());
        getUserAccountDetailsAsyncTask.execute(u_id);
    }



    @Override
    public void handleUserAccountResult(GetUserAccountResponse userAccountResponse) {
        UserProfileCommonFields commonFields = userAccountResponse.getUserProfileCommonFields();
        String name = commonFields.getName();
        String email = commonFields.getEmail();
        String phone_number= commonFields.getPhone_number();

        byte[] profile_pic = commonFields.getUser_image();

        tv_name.setText(name);
        tv_email.setText(email);
        tv_contact_number.setText(phone_number);

        if(profile_pic != null) {
            Log.e(TAG, "Showing pic...");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profile_pic);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if(bitmap == null)
                Log.e(TAG, "bitmap is null");
            iv_profile_pic.setImageBitmap(bitmap);
        }
        setContentShown(true);

    }

    @Override
    public void handleUserAccountError(Exception e) {

    }
}
