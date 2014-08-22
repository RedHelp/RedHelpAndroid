package org.redhelp.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.redhelp.alarmmanger.NotificationAlarmManager;
import org.redhelp.common.LoginRequest;
import org.redhelp.common.LoginResponse;
import org.redhelp.common.types.LoginReponseTypes;
import org.redhelp.network.NetworkChecker;
import org.redhelp.session.SessionManager;
import org.redhelp.task.LoginUserAsyncTask;
import org.redhelp.util.ValidationHelper;

/**
 * Created by harshis on 5/19/14.
 */
public class LoginActivity extends Activity implements LoginUserAsyncTask.ILoginUserAsyncTaskListener{

    private EditText et_email;
    private EditText et_password;

    private Button bt_login;

    private ImageView iv_email_validation_status;
    private ImageView iv_password_validation_status;


    private boolean[] statuses_boolean = new boolean[2];
    private void activateLoginButton() {
        if (statuses_boolean[0] & statuses_boolean[1] == true) {
            bt_login.setEnabled(true);
        } else {
            bt_login.setEnabled(false);
        }
    }

    private void setViewObjects() {
        et_email = (EditText) findViewById(R.id.et_emailid_loginform);
        et_password = (EditText) findViewById(R.id.et_password_loginform);

        bt_login = (Button) findViewById(R.id.bt_login_loginform);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginButtonClick();
            }
        });

        iv_email_validation_status = (ImageView) findViewById(R.id.iv_email_validation_status_login);
        iv_password_validation_status = (ImageView) findViewById(R.id.iv_password_validation_status_login);

        iv_email_validation_status.setVisibility(View.GONE);
        iv_password_validation_status.setVisibility(View.GONE);



        addTextChangerListners();
    }

    private void addTextChangerListners() {

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                bt_login.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ValidationHelper.isValidEmail(editable.toString())) {
                    iv_email_validation_status.setImageResource(R.drawable.checktick);
                    statuses_boolean[0] = true;
                } else {
                    iv_email_validation_status.setImageResource(R.drawable.letter_x_icon);
                    statuses_boolean[0] = false;
                }

                activateLoginButton();
                iv_email_validation_status.setVisibility(View.VISIBLE);

            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                bt_login.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ValidationHelper.isValidPassword(editable.toString())) {
                    iv_password_validation_status.setImageResource(R.drawable.checktick);
                    statuses_boolean[1] = true;
                } else {
                    iv_password_validation_status.setImageResource(R.drawable.letter_x_icon);
                    statuses_boolean[1] = false;
                }

                activateLoginButton();
                iv_password_validation_status.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        setViewObjects();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go to LoginOptionsScreen
                Intent intent = new Intent(this, LoginOptionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleLoginButtonClick() {
        //If internet is not available show toast and do nothing.
        if(NetworkChecker.isNetworkAvailable(getApplicationContext()) == false) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.toast_server_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        String email = et_email.getText().toString();
        String password = et_password.getText().toString();



        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        LoginUserAsyncTask loginUserAsyncTask = new LoginUserAsyncTask(getApplicationContext(), this);
        loginUserAsyncTask.execute(loginRequest);
    }

    @Override
    public void handleLoginError() {
        //TODO handle
    }

    @Override
    public void handleLoginResponse(LoginResponse response) {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT);
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if(response.getLoginResponseType().equals(LoginReponseTypes.LOGIN_SUCESSFULL)) {
            SessionManager sessionManager = SessionManager.getSessionManager(getApplicationContext());
            boolean isLoggedIn = sessionManager.updateLoginState(2, response.getU_id(), response.getB_p_id());

            if(isLoggedIn == true) {
                toast.setText(R.string.toast_register_successful);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();

                // First set Notification Alarm manager.


                //Open up main screen
                Intent intentToMainScreen = new Intent(getApplicationContext(), HomeScreenActivity.class);
                intentToMainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentToMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentToMainScreen);
            }
        } else if(response.getLoginResponseType().equals(LoginReponseTypes.WRONG_EMAIL_ID) ||
                response.getLoginResponseType().equals(LoginReponseTypes.WRONG_PASSWORD)) {
            toast.setText(R.string.toast_invalid_login);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }

    }
}

