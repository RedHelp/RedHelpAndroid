package org.redhelp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.redhelp.adapter.items.TabsItem;

/**
 * Created by harshis on 5/30/14.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private TabsItem tabsToShow;

    public TabsPagerAdapter(FragmentManager fm, TabsItem tabs) {
        super(fm);
        this.tabsToShow = tabs;
    }

    @Override
    public Fragment getItem(int index) {
        //TODO add null checks
        return tabsToShow.tabs.get(index).fragment;
       /* switch (index) {
            case 0:
                // Top Rated fragment activity
                return new FacebookLoginFragment();

            case 1:
                // Games fragment activity
                return new HomeMapFragment();

            case 2:
                // Movies fragment activity
                return new SlidingMenuFragment();
            case 3:
                // Movies fragment activity
                Fragment fragment =  new LastKnownLocationMapTabFragment();
                Bundle content = new Bundle();
                content.putDouble(Constants.LOCATION_LAT, 17.4642329);
                content.putDouble(Constants.LOCATION_LONG, 78.3686539);
                fragment.setArguments(content);
                return fragment;
          //  case 4:
                // Movies fragment activity
    //                return new SlidingMenuFragment();


        }*/

    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return tabsToShow.tabs.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}