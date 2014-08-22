package org.redhelp.fagment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.redhelp.app.R;

/**
 * Created by harshis on 7/26/14.
 */
public class ViewImageFragment extends Fragment {

    private static final String BUNDLE_IMAGE = "Image";
    private static final String TAG = "RedHelp:ViewImageFragment";
    private ImageView iv_image;

    public static ViewImageFragment createViewImageFragmentInstance(int image_resource) {
        ViewImageFragment viewImageFragment = new ViewImageFragment();
        Bundle content = new Bundle();
        content.putInt(BUNDLE_IMAGE, image_resource);
        viewImageFragment.setArguments(content);
        return viewImageFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            iv_image = new ImageView(getActivity());
            iv_image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            layout.setGravity(Gravity.CENTER);
            layout.addView(iv_image);
            fetchAndShowImage();

        return layout;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void fetchAndShowImage() {
        Bundle data_received = getArguments();
        try {
            int image_resource = data_received.getInt(BUNDLE_IMAGE);
            iv_image.setImageResource(image_resource);
        } catch (Exception e) {
            Log.e(TAG, "Unable to show image, Exception :" + e.toString());
        }
    }

    private void initializeViews(View view) {
        iv_image = (ImageView) view.findViewById(R.id.iv_image_view_image_layout);
    }
}
