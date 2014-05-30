package org.redhelp.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.redhelp.common.RegisterRequest;
import org.redhelp.network.NetworkChecker;
import org.redhelp.task.RegisterUserAsyncTask;
import org.redhelp.util.RequestBuilder;

/**
 * Created by harshis on 5/19/14.
 */
public class RegisterActivity extends Activity {

    private EditText et_email;
    private EditText et_name;
    private EditText et_password;
    private EditText et_phone_number;

    private Button bt_register;

    private void setViewObjects() {
        et_email = (EditText) findViewById(R.id.et_emailid_registerform);
        et_name = (EditText) findViewById(R.id.et_name_registerform);
        et_password = (EditText) findViewById(R.id.et_password_registerform);
        et_phone_number = (EditText) findViewById(R.id.et_phoneno_registerform);

        bt_register = (Button) findViewById(R.id.bt_submit_registerForm);
    }

    private void validateRegisterForm(String email, String name, String password, String ph_number) {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

        setViewObjects();
        bt_register.setOnClickListener(new View.OnClickListener() {
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
                String name = et_name.getText().toString();
                String password = et_password.getText().toString();
                String phone_number = et_phone_number.getText().toString();

                //TODO Client side validation
                validateRegisterForm(email, name, password, phone_number);

                RegisterRequest registerRequest = RequestBuilder.createRegisterRequest(email, name, password, phone_number, null, null, null);

                RegisterUserAsyncTask task = new RegisterUserAsyncTask(getApplicationContext());
                task.execute(registerRequest);
            }
        });


    }
}
