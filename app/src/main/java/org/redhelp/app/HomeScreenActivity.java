package org.redhelp.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.redhelp.fagment.HomeMapFragment;
import org.redhelp.fagment.SlidingMenuFragment;

/**
 * Created by harshis on 5/24/14.
 */
public class HomeScreenActivity extends SlidingFragmentActivity {
    private Fragment mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

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
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null)
            mContent = new HomeMapFragment();

        //Set above fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame_main_screen, mContent).commit();

        //Set behind fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_main_screen_slidingmenu, new SlidingMenuFragment()).commit();


        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shape_shadow_sliding_menu);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);
    }
}
