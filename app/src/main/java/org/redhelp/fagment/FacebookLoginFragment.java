package org.redhelp.fagment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.commons.lang3.RandomStringUtils;
import org.redhelp.app.R;
import org.redhelp.common.RegisterRequest;
import org.redhelp.task.GetImageAsyncTask;
import org.redhelp.task.RegisterUserAsyncTask;
import org.redhelp.util.RequestBuilder;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by harshis on 5/20/14.
 */
public class FacebookLoginFragment  extends Fragment{
    private static final String TAG = "FacebookLoginFragment";

    private void fetchInfoAndcallRegister(Session session) {
        if(session != null) {
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        String email = (String) user.asMap().get("email");
                        String name = user.getName();
                        String externalId = user.getId();
                        String password = RandomStringUtils.randomAlphabetic(7);

                        GetImageAsyncTask getImageAsyncTask = new GetImageAsyncTask(getActivity().getApplicationContext());

                        byte[] image = new byte[0];
                        try {
                            image = getImageAsyncTask.execute(externalId).get(1000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            Log.e("FB", "Interruptied exception");
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch(TimeoutException e){
                            e.printStackTrace();
                        }
                         Log.e("FB_IMAGE", "image fetched:"+image);
                        RegisterRequest registerRequest = RequestBuilder.createRegisterRequest(email,
                                name, password, null, externalId, 0l, image);

                        RegisterUserAsyncTask task = new RegisterUserAsyncTask(getActivity().getApplicationContext());
                        task.execute(registerRequest);
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
        Log.e("FB", "came in onSessionStateChange, e= ");
        if(session.isOpened()) {
            if (exception != null)
                Log.e("FB", "Exception: " + exception.toString());
            if (state.isOpened()) {
                Log.e(TAG, "Logged in via FB");
                fetchInfoAndcallRegister(session);
            } else if (state.isClosed()) {
                //TODO add logout logic.
                Log.e(TAG, "Logged out...");
            }
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
}
