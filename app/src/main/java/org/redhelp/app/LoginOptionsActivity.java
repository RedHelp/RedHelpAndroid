package org.redhelp.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.dd.processbutton.iml.ActionProcessButton;


/**
 * Created by harshis on 5/19/14.
 */
public class LoginOptionsActivity extends FragmentActivity {

    private Button bt_register;
    private Button bt_login;

    private ActionProcessButton temp_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loginoptions_layout);

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

        temp_button = (ActionProcessButton) findViewById(R.id.btnSignIn);
        temp_button.setMode(ActionProcessButton.Mode.ENDLESS);
        temp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp_button.setProgress(10);
            }
        });
    }

}
