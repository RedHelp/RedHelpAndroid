package org.redhelp.adapter;

/**
 * Created by harshis on 7/26/14.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.redhelp.app.R;
import org.redhelp.fagment.ViewImageFragment;

import java.util.ArrayList;
import java.util.List;

public class ScreenShotsPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "RedHelp:ScreenShotsPagerAdapter";
    public static List<Integer> listScreenShotsResource;

    FragmentManager fm;
    static {
        listScreenShotsResource = new ArrayList<Integer>();
        listScreenShotsResource.add(R.drawable.home_screen_screenshot);
        listScreenShotsResource.add(R.drawable.event_view_screenshot);
        listScreenShotsResource.add(R.drawable.map_view_screenshot);
        listScreenShotsResource.add(R.drawable.slidder_screenshot);
        Log.d(TAG, listScreenShotsResource.toString());
    }

    public ScreenShotsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int index) {
        Log.d(TAG, "getItem"+index);
        Fragment fragment = null;
        try {
            Integer imageResource = listScreenShotsResource.get(index);
            Log.d(TAG, "imageResouce :"+imageResource.toString());
            fragment = ViewImageFragment.createViewImageFragmentInstance(imageResource);

        } catch (Exception e) {
            Log.e(TAG, "Problem while fetching fragment from list, e=" + e.toString());
        }

        return fragment;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listScreenShotsResource.size();
    }
}
