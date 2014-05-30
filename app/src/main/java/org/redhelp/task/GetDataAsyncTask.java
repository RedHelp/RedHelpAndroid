package org.redhelp.task;

import android.os.AsyncTask;
import android.widget.TextView;

import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 5/19/14.
 */
public  class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {

    String data;
    TextView tv;
    Exception e;
    public GetDataAsyncTask(TextView tv) {
        this.tv = tv;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            data = RestClientCall.getCall("users/test");
        }catch(Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(e != null)
            tv.setText(e.toString());
        else
            tv.setText(data);
    }
}

