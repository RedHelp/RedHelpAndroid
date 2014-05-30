package org.redhelp.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Created by harshis on 5/23/14.
 */
public class GetImageAsyncTask extends AsyncTask<String, Void, byte[]> {
    private static final String TAG = "GetImageAsyncTask";
    private  ProgressDialog dialog;

    public GetImageAsyncTask(Context context) {
        dialog = new ProgressDialog(context);

    }
    /**
     * Function loads the users facebook profile pic
     *
     * @param userID
     */
    public byte[] getUserPic(String userID) {
        String imageURL;
        Bitmap bitmap = null;
        ByteBuffer buffer = null;
        byte[] array = null;
        Log.d(TAG, "Loading Picture");
        imageURL = "http://graph.facebook.com/"+userID+"/picture?type=small";
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
            int bytes = bitmap.getByteCount();
            buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

            array = buffer.array(); //Get the underlying array containing the data.
        } catch (Exception e) {
            Log.d("TAG", "Loading Picture FAILED");
            e.printStackTrace();
        }

        return array;
    }


    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Processing...");
        //this.dialog.show();
    }

    @Override
    protected byte[] doInBackground(String... strings) {
        return getUserPic(strings[0]);
    }

    @Override
    protected void onPostExecute(byte[] imageByte) {
      // this.dialog.dismiss();
    }
}
