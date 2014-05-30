package org.redhelp.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.MainActivity;
import org.redhelp.app.R;
import org.redhelp.common.LoginRequest;
import org.redhelp.common.LoginResponse;
import org.redhelp.common.types.LoginReponseTypes;
import org.redhelp.network.RestClientCall;
import org.redhelp.session.SessionManager;

/**
 * Created by harshis on 5/20/14.
 */
public class LoginUserAsyncTask extends AsyncTask<LoginRequest, Void, String> {

    private static final String TAG = "LoginUserAsyncTask";

    private Context context;
    private Exception e;
    public LoginUserAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(LoginRequest... loginRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(loginRequests[0]);
            json_response = RestClientCall.postCall("/userAccount/loginUser", json_request);
        }catch(Exception e) {
            this.e = e;
            return null;
        }
        return json_response;
    }

    @Override
    protected void onPostExecute(String json_response) {
        Toast toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Gson gson = new Gson();
            LoginResponse response = null;
            try {
                response = gson.fromJson(json_response, LoginResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            handleResponse(response, toast);
        }
    }
    private void handleResponse(LoginResponse response, Toast toast) {
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if(response.getLoginResponseType().equals(LoginReponseTypes.LOGIN_SUCESSFULL)) {
            SessionManager sessionManager = SessionManager.getSessionManager(context);
            boolean isLoggedIn = sessionManager.createLoginSession(response.getName(), response.getEmail(),
                    response.getU_id(), response.getPhone_number(), response.getUaa_id());

            toast.setText(R.string.toast_register_successful);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            if(isLoggedIn == true) {
                Intent intentToMainScreen = new Intent(context, MainActivity.class);
                // context.startActivity(intentToMainScreen);
            }
        } else if(response.getLoginResponseType().equals(LoginReponseTypes.WRONG_EMAIL_ID) ||
                response.getLoginResponseType().equals(LoginReponseTypes.WRONG_PASSWORD)) {
            toast.setText(R.string.toast_invalid_login);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }
}
