package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.RegisterRequest;
import org.redhelp.common.RegisterResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 5/19/14.
 */
public class RegisterUserAsyncTask extends AsyncTask<RegisterRequest, Void, Void> {
    private static final String TAG = "RedHelp:RegisterUserAsyncTask";

    public interface IRegisterUserAsyncTaskListener {
        void handleError();
        void handleResponse(RegisterResponse response);
    }

    private Context ctx;
    private IRegisterUserAsyncTaskListener listener;

    String json_response;

    Exception e;
    RegisterRequest registerRequest;

    public RegisterUserAsyncTask(Context ctx, IRegisterUserAsyncTaskListener listener) {
        this.ctx = ctx;
        this.listener = listener;
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
        Toast toast = Toast.makeText(ctx,"", Toast.LENGTH_SHORT);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            listener.handleError();
        }
        else {
            Gson gson = new Gson();
            RegisterResponse response = null;
            try {
                response = gson.fromJson(json_response, RegisterResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleResponse(response);
        }

    }
}