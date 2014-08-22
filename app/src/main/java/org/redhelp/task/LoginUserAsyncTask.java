package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.LoginRequest;
import org.redhelp.common.LoginResponse;
import org.redhelp.network.RestClientCall;


/**
 * Created by harshis on 5/20/14.
 */
public class LoginUserAsyncTask extends AsyncTask<LoginRequest, Void, String> {
    private static final String TAG = "RedHelp:LoginUserAsyncTask";

    public interface ILoginUserAsyncTaskListener {
        void handleLoginError();
        void handleLoginResponse(LoginResponse response);
    }

    private Context ctx;
    private ILoginUserAsyncTaskListener listener;

    private Exception e;
    public LoginUserAsyncTask(Context context, ILoginUserAsyncTaskListener listener) {
        this.ctx = context;
        this.listener = listener;
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
        Toast toast = Toast.makeText(ctx,"", Toast.LENGTH_SHORT);
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
            listener.handleLoginResponse(response);
        }
    }
}
