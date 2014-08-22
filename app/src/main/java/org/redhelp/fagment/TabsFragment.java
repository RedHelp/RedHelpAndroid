package org.redhelp.fagment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import org.redhelp.adapter.TabsPagerAdapter;
import org.redhelp.adapter.items.TabItem;
import org.redhelp.adapter.items.TabsItem;
import org.redhelp.app.R;

/**
 * Created by harshis on 5/30/14.
 */
public class TabsFragment extends Fragment
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    public static final String TABS_BUNDLE = "tabs";
    public static TabsFragment createBloodProfileTabsFragmentInstance(TabsItem tabs) {
        TabsFragment tabsFragment = new TabsFragment();
        Bundle data_to_pass = new Bundle();
        data_to_pass.putSerializable(TABS_BUNDLE, tabs);
        tabsFragment.setArguments(data_to_pass);
        return tabsFragment;
    }

    public interface ITabsFragment {
        public TabsItem getTabs();
    }


    private TabsPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blood_profile_tabs_layout, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);

        Bundle data_received = getArguments();
        TabsItem tabs = (TabsItem) data_received.getSerializable(TABS_BUNDLE);
        // Tab Initialization
        mAdapter = new TabsPagerAdapter(getActivity().getSupportFragmentManager(), tabs);
        initialiseTabHost(v, tabs);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        return v;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String s) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    // Tabs Creation
    private void initialiseTabHost(View view, TabsItem tabs_object) {
        mTabHost = (TabHost) view.findViewById(R.id.tab_host_tabs_layout);
        mTabHost.setup();

        for(TabItem tabItem:tabs_object.tabs) {
            AddTab(this.mTabHost, this.mTabHost.newTabSpec(tabItem.name).setIndicator(tabItem.name));
        }
       /* // TODO Put here your Tabs
        AddTab(this.mTabHost, this.mTabHost.newTabSpec("Blood requests").setIndicator("Blood requests"));
        AddTab(this.mTabHost, this.mTabHost.newTabSpec("Messages").setIndicator("Messages"));
        //AddTab(this.mTabHost, this.mTabHost.newTabSpec("Events attending").setIndicator("Events"));
        AddTab(this.mTabHost, this.mTabHost.newTabSpec("Awards").setIndicator("Awards"));
        AddTab(this.mTabHost, this.mTabHost.newTabSpec("Last known location").setIndicator("Location"));
*/

        mTabHost.setOnTabChangedListener(this);
        for(int i =0; i < mAdapter.getCount(); i++) {
            TextView x = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            /*if(i == 1)
                x.setTextSize(9);
            else
                x.setTextSize(13);*/
            x.setAllCaps(false);
        }
    }

    // Method to add a TabHost
    private void AddTab( TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(this.getActivity().getApplicationContext()));
        tabHost.addTab(tabSpec);
    }

    private class MyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public MyTabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }
}
