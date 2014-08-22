package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.EditUserAccountRequest;
import org.redhelp.common.EditUserAccountResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 8/9/14.
 */
public class EditUserAccountTask extends AsyncTask<EditUserAccountRequest, Void, String>{

    private static final String TAG = "RedHelp:EditUserAccountRequest";

    public interface IEditUserAccountTaskListener {
        void handleLoginError();
        void handleEditUserAccountResponse(EditUserAccountResponse response);
    }

    private Context ctx;
    private IEditUserAccountTaskListener listener;

    private Exception e;
    public EditUserAccountTask(Context context, IEditUserAccountTaskListener listener) {
        this.ctx = context;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(EditUserAccountRequest... editRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(editRequests[0]);
            json_response = RestClientCall.postCall("/userAccount/editUserAccount", json_request);
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
            EditUserAccountResponse response = null;
            try {
                response = gson.fromJson(json_response, EditUserAccountResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleEditUserAccountResponse(response);
        }
    }
}
