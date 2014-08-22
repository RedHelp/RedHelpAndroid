package org.redhelp.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.redhelp.session.SessionManager;
import org.redhelp.types.Constants;


/**
 * Created by harshis on 5/30/14.
 *
 * Doc: First activity called.
 * - Shows spash image to user.
 * - Decides whether to show loginOptions screen or mainScreen, based on user logged in state.
 */
public class SplashScreenActivity extends Activity{

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_spash_screen_layout);

        final SessionManager sm = SessionManager
                .getSessionManager(SplashScreenActivity.this);
		/* New Handler to start the Menu-Activity
		 * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
				/* Create an Intent that will start the Menu-Activity. */
                Intent furtherIntent;
                int currentLoginState = sm.getCurrentLoginState();
                if(currentLoginState == 1) {
                    furtherIntent = new Intent(SplashScreenActivity.this, CreateBloodProfileActivity.class);
                } else if(sm.isLoggedIn() && sm.getBPId() != null){
                    furtherIntent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                } else{
                    furtherIntent = new Intent(SplashScreenActivity.this, LoginOptionsActivity.class);
                }
                SplashScreenActivity.this.startActivity(furtherIntent);
                overridePendingTransition(R.anim.animation, R.anim.animation1);
                SplashScreenActivity.this.finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH);
    }

}
