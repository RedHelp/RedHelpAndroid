package org.redhelp.fagment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
 * Created by harshis on 7/12/14.
 */
public class TabsFragmentNew extends Fragment
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    public static TabsFragmentNew createTabsFragmentInstance(ITabsFragment tabHostFragment) {
            TabsFragmentNew fragment = new TabsFragmentNew();
            fragment.setHostFragment(tabHostFragment);
        return fragment;
    }

    public interface ITabsFragment {
        public TabsItem getTabs();
    }

    public TabsFragmentNew(){}

    public void setHostFragment(ITabsFragment hostFragment) {
        this.hostFragment = hostFragment;
    }
    private ITabsFragment hostFragment;
    private TabsPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_blood_profile_tabs_layout, container, false);
        if(mViewPager!=null)
            Log.e("TabsFragmentNew", "mViewPager is not null");
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        Bundle data_received = getArguments();
        TabsItem tabs;
        if(hostFragment == null)
            return view;
        tabs = hostFragment.getTabs();
        // Tab Initialization
        mAdapter = new TabsPagerAdapter(getActivity().getSupportFragmentManager(), tabs);
        initialiseTabHost(view, tabs);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        return view;
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
        mTabHost = (TabHost) view.findViewById(R.id.tab_host_tabs_layout);
        mTabHost.setup();

        for(TabItem tabItem:tabs_object.tabs) {
            AddTab(this.mTabHost, this.mTabHost.newTabSpec(tabItem.name).setIndicator(tabItem.name));
        }

        mTabHost.setOnTabChangedListener(this);
        for(int i =0; i < mAdapter.getCount(); i++) {
            TextView x = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
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
            v.setTag(tag);
            return v;
        }
    }
}
