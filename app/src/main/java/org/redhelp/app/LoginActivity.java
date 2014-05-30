package org.redhelp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.redhelp.common.LoginRequest;
import org.redhelp.network.NetworkChecker;
import org.redhelp.task.LoginUserAsyncTask;

/**
 * Created by harshis on 5/19/14.
 */
public class LoginActivity extends Activity {

    private EditText et_email;
    private EditText et_password;

    private Button bt_login;

    private void setViewObjects() {
        et_email = (EditText) findViewById(R.id.et_emailid_loginform);
        et_password = (EditText) findViewById(R.id.et_password_loginform);

        bt_login = (Button) findViewById(R.id.bt_login_loginform);
    }

    private boolean isValidLoginForm(String email, String password) {
        return true;
    }

//-Dhttp.proxyHost=proxy.domain.company.com -Dhttp.proxyPort=8090 -Dhttp.proxyUser=atiris -Dhttp.proxyPassword=mysecretpassword

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        setViewObjects();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If internet is not available show toast and do nothing.
                if(NetworkChecker.isNetworkAvailable(getApplicationContext()) == false) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.toast_server_error, Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                //TODO validate Login form
                if(!isValidLoginForm(email, password)) {
                   return;
                }

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(email);
                loginRequest.setPassword(password);

                LoginUserAsyncTask loginUserAsyncTask = new LoginUserAsyncTask(getApplicationContext());
                loginUserAsyncTask.execute(loginRequest);
            }
        });
    }
}

