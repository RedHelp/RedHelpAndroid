package org.redhelp.fagment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.commons.lang3.RandomStringUtils;
import org.redhelp.app.CreateBloodProfileActivity;
import org.redhelp.app.HomeScreenActivity;
import org.redhelp.app.R;
import org.redhelp.common.GetBloodProfileResponse;
import org.redhelp.common.RegisterRequest;
import org.redhelp.common.RegisterResponse;
import org.redhelp.common.types.RegisterResponseTypes;
import org.redhelp.interfaces.ProgressDialogInterface;
import org.redhelp.session.SessionManager;
import org.redhelp.task.GetBloodProfileAsyncTask;
import org.redhelp.task.GetFbImageAsyncTask;
import org.redhelp.task.RegisterUserAsyncTask;
import org.redhelp.util.RequestBuilder;

import java.util.Arrays;


/**
 * Created by harshis on 5/20/14.
 */
public class FacebookLoginFragment  extends Fragment
        implements GetFbImageAsyncTask.IGetImageAsyncTaskListner, RegisterUserAsyncTask.IRegisterUserAsyncTaskListener,
        GetBloodProfileAsyncTask.IGetBloodProfileListener, ProgressDialogInterface {
    private static final String TAG = "RedHelp:FacebookLoginFragment";

    private String email;
    private String name;
    private String externalId;
    private String password;
    private Context ctx;

    private Long u_id;
    public ProgressDialog progressDialog;

    private void callGetFbImageTask(String externalProfileId) {
        GetFbImageAsyncTask getFbImageAsyncTask = new GetFbImageAsyncTask(getActivity(), this);
        getFbImageAsyncTask.execute(externalProfileId);
    }


    // This method fetches fb info
    // It starts callGetImageTask to fetch image from FB
    // Later handleResponse handles Fb image callback
    private void fetchFbInfo(Session session) {
        if(session != null) {
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        email = (String) user.asMap().get("email");
                        name = user.getName();
                        externalId = user.getId();
                        password = RandomStringUtils.randomAlphabetic(7);

                        callGetFbImageTask(externalId);

                    } else {
                        Log.e("FB", "user is null");
                        //TODO take appropriate rollback action
                    }
                }
            }).executeAsync();
        } else {
            Log.e("FB", "session is null");
            //TODO take appropriate rollback action
        }

    }
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.d("FB", "came in onSessionStateChange, Session state:"+state.name());
        if(session.isOpened()) {
            if (exception != null)
                Log.e(TAG, "Exception while logging in via FB: " + exception.toString());
            if (state.isOpened() &&  SessionManager.getSessionManager(getActivity()).isLoggedIn() == false) {
                Log.d(TAG, "Logged in via FB");
                showProgressDialog("Loading...", "Please wait while we register you.");

                fetchFbInfo(session);
            } else if (state.isClosed()) {
                //TODO add logout logic.
                Log.d(TAG, "Logged out...");
            }
        } else {
            Log.d(TAG, "session is not opened !");
        }
    }
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private UiLifecycleHelper uiHelper;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fb_login_layout, container, false);


        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.bt_fbAuthButton);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        authButton.setFragment(this);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.ctx = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void handleError() {

    }

    @Override
    public void handleResponse(RegisterResponse response) {
        if(ctx == null)
            return;

        Toast toast = Toast.makeText(ctx,"", Toast.LENGTH_SHORT);
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //TODO update_fb different flow.
        if(response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_NEW) ||
                response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_FB_NEW)) {
            SessionManager sessionManager = SessionManager.getSessionManager(ctx);
            boolean loginResponse = sessionManager.updateLoginState(1, response.getU_id(), null);

            if(loginResponse == true) {
                toast.setText(R.string.toast_register_successful);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                Intent intentToCreateBloodProfile = new Intent(ctx, CreateBloodProfileActivity.class);
                intentToCreateBloodProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentToCreateBloodProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_FB_NEW)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(CreateBloodProfileActivity.BUNDLE_IS_NEW_FB, true);
                    intentToCreateBloodProfile.putExtras(bundle);
                }
                startActivity(intentToCreateBloodProfile);
            }
        } else if(response.getRegisterResponseType().equals(RegisterResponseTypes.UPDATED_FB)) {
            this.u_id = response.getU_id();
            GetBloodProfileAsyncTask getBloodProfileAsyncTask = new GetBloodProfileAsyncTask(this, ctx, GetBloodProfileAsyncTask.Request_VIA.U_ID);
            getBloodProfileAsyncTask.execute(response.getU_id());

        }
        else if(response.getRegisterResponseType().equals(RegisterResponseTypes.DUPLICATE_EMAIL)) {
            toast.setText(R.string.toast_duplicate_email);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }

    @Override
    public void handleFbImageResponse(byte[] image_array) {
        RegisterRequest registerRequest = RequestBuilder.createRegisterRequest(email,
                name, password, null, externalId, 0l, image_array);
        Log.d(TAG, "Came in handle response of image");
        RegisterUserAsyncTask task = new RegisterUserAsyncTask(getActivity().getApplicationContext(), this);
        task.execute(registerRequest);
    }

    @Override
    public void handleGetBloodProfileResponse(GetBloodProfileResponse response) {
        SessionManager sessionManager = SessionManager.getSessionManager(ctx);
        boolean loginResponse = sessionManager.updateLoginState(2, u_id, response.getB_p_id());
        hideProgressDialog();
        if(loginResponse == true) {

            Intent home_screen_intent = new Intent(ctx, HomeScreenActivity.class);
            home_screen_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            home_screen_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(home_screen_intent);
        }
    }

    @Override
    public void handleGetBloodProfileError(Exception e) {

    }

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
}
