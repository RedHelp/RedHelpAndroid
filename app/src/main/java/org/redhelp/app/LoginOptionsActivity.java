package org.redhelp.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import org.redhelp.adapter.ScreenShotsPagerAdapter;
import org.redhelp.customviews.CustomCircleIndicator;
import org.redhelp.session.SessionManager;
import org.redhelp.types.Constants;


/**
 * Created by harshis on 5/19/14.
 */
public class LoginOptionsActivity extends FragmentActivity {
    private static final String TAG = "RedHelp:LoginOptionsActivity";
    private Button bt_register;
    private Button bt_login;

    public ViewPager vp_screenshots;
    public ScreenShotsPagerAdapter screenShotsPagerAdapter;
    public CustomCircleIndicator cpi_circle_indicator;

    private int position = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (position >= screenShotsPagerAdapter.getCount()) {
                position = 0;
            } else {
                position = position + 1;
            }
            vp_screenshots.setCurrentItem(position, true);
            handler.postDelayed(runnable, Constants.SCREENSHOTS_IMAGES_DISPLAY_LENGTH);
        }
    };

    private void setUpScreenShots() {

        vp_screenshots = (ViewPager) findViewById(R.id.vp_pager_login_options_layout);
        screenShotsPagerAdapter = new ScreenShotsPagerAdapter(getSupportFragmentManager());
        vp_screenshots.setAdapter(screenShotsPagerAdapter);
        vp_screenshots.setPageTransformer(true,
                new ZoomOutPageTransformer());

        cpi_circle_indicator = (CustomCircleIndicator) findViewById(R.id.cpi_circle_indicator_login_options_layout);
        cpi_circle_indicator.setViewPager(vp_screenshots);
        cpi_circle_indicator.setFillColor(0xFF000000);
        cpi_circle_indicator.setStrokeColor(0xFF000000);
        final float density = getResources().getDisplayMetrics().density;
        cpi_circle_indicator.setStrokeWidth(density);
    }
    private void initializeViews() {
        bt_register = (Button) findViewById(R.id.bt_register_loginoptionsscreen);

        bt_register.setOnClickListener(new View.OnClickListener()  {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        bt_login = (Button) findViewById(R.id.bt_login_loginoptionsscreen);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

            }
        });

        setUpScreenShots();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager.getSessionManager(this).sessionManagerDump();
        setContentView(R.layout.activity_loginoptions_layout);
        initializeViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, Constants.SCREENSHOTS_IMAGES_DISPLAY_LENGTH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }




    public int i = 1;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as
                // well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}