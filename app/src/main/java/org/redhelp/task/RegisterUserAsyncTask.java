package org.redhelp.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.MainActivity;
import org.redhelp.app.R;
import org.redhelp.common.RegisterRequest;
import org.redhelp.common.RegisterResponse;
import org.redhelp.common.types.RegisterResponseTypes;
import org.redhelp.network.RestClientCall;
import org.redhelp.session.SessionManager;

/**
 * Created by harshis on 5/19/14.
 */
public class RegisterUserAsyncTask extends AsyncTask<RegisterRequest, Void, Void> {
    private static String TAG = "RegisterUserAsyncTask";

    Context context;
    String json_response;

    Exception e;
    RegisterRequest registerRequest;

    public RegisterUserAsyncTask(Context context) {
        this.context = context;

    }

    @Override
    protected Void doInBackground(RegisterRequest... registerRequest) {
        this.registerRequest = registerRequest[0];
        Gson gson = new Gson();
        try{
            String json_request = gson.toJson(registerRequest[0]);
            json_response = RestClientCall.postCall("/userAccount/registerUser", json_request);
        }catch(Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Gson gson = new Gson();
            RegisterResponse response = null;
            try {
                response = gson.fromJson(json_response, RegisterResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            handleResponse(response, toast);
        }

    }


    private void handleResponse(RegisterResponse response, Toast toast) {
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if(response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_NEW) ||
                response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_FB_NEW) ||
                response.getRegisterResponseType().equals(RegisterResponseTypes.UPDATED_FB)) {
            SessionManager sessionManager = SessionManager.getSessionManager(context);
            boolean isLoggedIn = sessionManager.createLoginSession(registerRequest.getName(), registerRequest.getEmail(),
                response.getU_id(), registerRequest.getPhoneNo(), response.getUaa_id());

            toast.setText(R.string.toast_register_successful);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            if(isLoggedIn == true) {
                Intent intentToMainScreen = new Intent(context, MainActivity.class);
               // context.startActivity(intentToMainScreen);
            }
        } else if(response.getRegisterResponseType().equals(RegisterResponseTypes.DUPLICATE_EMAIL)) {
            toast.setText(R.string.toast_duplicate_email);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }
}