package org.redhelp.fagment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.progressfragment.ProgressFragment;

import org.redhelp.app.R;
import org.redhelp.task.GetFbImageAsyncTask;

import java.io.ByteArrayInputStream;

/**
 * Created by harshis on 8/1/14.
 */

//TODO remove this Class
public class TestFbFragment extends ProgressFragment implements GetFbImageAsyncTask.IGetImageAsyncTaskListner {

    private static final String TAG = "TestFbFragment";
    private View fragmentContent;
    ImageView iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContent = inflater.inflate(R.layout.test_fb_image_fragment, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseViews();
        fetchAndShowData("810249062319068");
    }

    private void initialiseViews() {
        setContentView(fragmentContent);
        iv = (ImageView) getActivity().findViewById(R.id.imageView_test_fb);
    }

    private void fetchAndShowData(String id) {
        setContentShown(false);
        GetFbImageAsyncTask getFbImageAsyncTask = new GetFbImageAsyncTask(getActivity(), this);
        getFbImageAsyncTask.execute(id);
    }


    @Override
    public void handleError() {

    }

    @Override
    public void handleFbImageResponse(byte[] image_array) {
        if(image_array != null) {
            Log.e(TAG, "Showing pic...");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(image_array);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if(bitmap == null)
                Log.e(TAG, "bitmap is null");
            iv.setImageBitmap(bitmap);
        }
        setContentShown(true);
    }

}
