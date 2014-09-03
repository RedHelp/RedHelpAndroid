package org.redhelp.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.redhelp.data.SearchPrefData;
import org.redhelp.fagment.HomeFragment;
import org.redhelp.fagment.HomeSearchPrefFragment;
import org.redhelp.fagment.SlidingMenuFragment;
import org.redhelp.fagment.ViewBloodProfileFragment;
import org.redhelp.fagment.ViewBloodRequestFragment;
import org.redhelp.session.SessionManager;
import org.redhelp.types.Constants;

/**
 * Created by harshis on 5/24/14.
 */
public class HomeScreenActivity extends SlidingFragmentActivity {
    private static final String TAG = "RedHelp:HomeScreenActivity";
    public static final String HOMESCREEN_FRAGMENT = "home_screen_fragment";
    public static final String BUNDLE_B_R_ID = "bundle_b_r_id";
    public static final String BUNDLE_B_P_ID = "bundle_b_p_id";

    private Fragment mContent;
    public SearchPrefData searchPrefData;
    public MenuItem filterButton;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() == 0) {
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLoggedIn = SessionManager.getSessionManager(this).isLoggedIn();
        SessionManager.getSessionManager(this).sessionManagerDump();
        if(!isLoggedIn) {

            Intent intentToLoginOptionsScreen = new Intent(getApplicationContext(), LoginOptionsActivity.class);
            intentToLoginOptionsScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentToLoginOptionsScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentToLoginOptionsScreen);
        }
        setTitle(R.string.app_name);
        getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
        setContentView(R.layout.activity_home_screen_frame_layout);

        if (findViewById(R.id.menu_frame_main_screen_slidingmenu) == null) {
            setBehindContentView(R.layout.menu_frame_slidingmenu_mainscreen);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            // show home as up so we can toggle
            //TODO See suppport for older device here.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        if (findViewById(R.id.menu_filters_frame_main_screen_slidingmenu) == null) {
            getSlidingMenu().setSecondaryMenu(R.layout.menu_filters_frame_slidingmenu_mainscreen);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        Bundle data_received = getIntent().getExtras();
        if(data_received != null) {
            Log.d(TAG, "data_received is not null"+data_received.toString());
            String fragmentToStart = data_received.getString(HOMESCREEN_FRAGMENT);


            if(fragmentToStart != null) {
                if(fragmentToStart.equals("VIEW_BLOOD_REQUEST")) {
                    Long b_r_id = data_received.getLong(BUNDLE_B_R_ID);
                    Bundle data_to_pass = new Bundle();
                    data_to_pass.putLong(Constants.BUNDLE_B_R_ID, b_r_id);
                    ViewBloodRequestFragment viewBloodRequestFragment = new ViewBloodRequestFragment();
                    viewBloodRequestFragment.setArguments(data_to_pass);
                    mContent = viewBloodRequestFragment;
                } else if(fragmentToStart.equals("VIEW_BLOOD_PROFILE")) {
                    Long creator_b_p_id = data_received.getLong(BUNDLE_B_P_ID);
                    Long own_b_p_id = SessionManager.getSessionManager(getApplicationContext()).getBPId();
                    ViewBloodProfileFragment viewBloodProfileFragment = ViewBloodProfileFragment.
                            createViewBloodProfileFragmentInstance(own_b_p_id, creator_b_p_id);
                    mContent = viewBloodProfileFragment;
                }
            }
        }else if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");

        if (mContent == null)
            mContent = new HomeFragment();

        //Set above fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame_main_screen, mContent).commit();

        //Set left menu fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_main_screen_slidingmenu, new SlidingMenuFragment()).commit();

        //Set right menu(Filters) fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_filters_frame_main_screen_slidingmenu, new HomeSearchPrefFragment()).commit();

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shape_shadow_sliding_menu);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);


        searchPrefData = SearchPrefData.getDefaultSearchPrefData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_options_screen, menu);
        MenuItem menuItem_Info = menu.add(0, R.id.menuid_filter, 0, "Filter");
        menuItem_Info.setIcon(android.R.drawable.ic_menu_sort_by_size);
        MenuItemCompat.setShowAsAction(menuItem_Info,
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem_Info.setVisible(false);
        filterButton = menuItem_Info;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;

            case R.id.menuid_filter:
                getSlidingMenu().showSecondaryMenu(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void switchContent(final Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame_main_screen, fragment)
                .commit();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                getSlidingMenu().showContent();
            }
        }, 50);
    }

    public void toggleSliddingMenu() {
        getSlidingMenu().showContent();
    }





}
