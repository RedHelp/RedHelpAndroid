package org.redhelp.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.AddEventRequest;
import org.redhelp.common.AddEventResponse;
import org.redhelp.common.types.AddEventResponseType;
import org.redhelp.dialogs.SlotsDialogFragment;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 7/7/14.
 */
public class AttendAsyncTask extends AsyncTask<AddEventRequest, Void, String> {

    public interface IAttendAsyncTaskHandler {
        public void handleAttendResult(AddEventResponse addEventResponse);
    }

    private static String TAG = "AttendAsyncTask";

    SlotsDialogFragment slotsDialogFragment;

    Exception e;
    AddEventRequest addEventRequest;

    public AttendAsyncTask(SlotsDialogFragment slotsDialogFragment) {
        this.slotsDialogFragment = slotsDialogFragment;
    }

    @Override
    protected String doInBackground(AddEventRequest... addEventRequest) {
        this.addEventRequest = addEventRequest[0];

        String json_response = null;
        Gson gson = new Gson();
        try{
            String json_request = gson.toJson(addEventRequest[0]);
            json_response = RestClientCall.postCall("/event/addEvent", json_request);
        }catch(Exception e) {
            this.e = e;
        }
        return json_response;
    }

    @Override
    protected void onPostExecute(String json_response) {
        Toast toast = Toast.makeText(slotsDialogFragment.getActivity(),"", Toast.LENGTH_SHORT);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Gson gson = new Gson();
            AddEventResponse response = null;
            try {
                response = gson.fromJson(json_response, AddEventResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            handleResponse(response, toast);
        }

    }


    private void handleResponse(AddEventResponse response, Toast toast) {
        if(response == null) {
            toast.setText(R.string.toast_server_error);
        }
        Log.e(TAG, response.toString());

        if(response.getResponse_types().equals(AddEventResponseType.SUCCESSFUL)) {
            toast.setText(R.string.toast_register_successful);
        } else if(response.getResponse_types().equals(AddEventResponseType.ALREADY_DONE)) {
            toast.setText(R.string.toast_already_registered);
        } else if(response.getResponse_types().equals(AddEventResponseType.MAX_LIMIT_REACHED)) {
            toast.setText(R.string.toast_too_many_users);
        } else {
            toast.setText(R.string.toast_server_error);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        if(slotsDialogFragment instanceof IAttendAsyncTaskHandler) {
            ((IAttendAsyncTaskHandler)slotsDialogFragment).handleAttendResult(response);
        }

    }
}
