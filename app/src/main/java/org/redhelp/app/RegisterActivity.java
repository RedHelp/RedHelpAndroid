package org.redhelp.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.redhelp.common.RegisterRequest;
import org.redhelp.common.RegisterResponse;
import org.redhelp.common.types.RegisterResponseTypes;
import org.redhelp.network.NetworkChecker;
import org.redhelp.session.SessionManager;
import org.redhelp.task.RegisterUserAsyncTask;
import org.redhelp.util.RequestBuilder;
import org.redhelp.util.ValidationHelper;

/**
 * Created by harshis on 5/19/14.
 */
public class RegisterActivity extends Activity
        implements RegisterUserAsyncTask.IRegisterUserAsyncTaskListener{

    private EditText et_email;
    private EditText et_name;
    private EditText et_password;
    private EditText et_phone_number;


    private Button bt_register;


    private ImageView iv_name_validation_status;
    private ImageView iv_email_validation_status;
    private ImageView iv_password_validation_status;
    private ImageView iv_phonenumber_validation_status;

    private boolean[] statuses_boolean = new boolean[4];

    private void activateRegisterButton() {
        if (statuses_boolean[0] & statuses_boolean[1] & statuses_boolean[2] == true) {
            bt_register.setEnabled(true);
        } else {
            bt_register.setEnabled(false);
        }
    }


    private void setViewObjects() {
        et_email = (EditText) findViewById(R.id.et_emailid_registerform);
        et_name = (EditText) findViewById(R.id.et_name_registerform);
        et_password = (EditText) findViewById(R.id.et_password_registerform);
        et_phone_number = (EditText) findViewById(R.id.et_phoneno_registerform);

        bt_register = (Button) findViewById(R.id.bt_submit_registerForm);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegisterButtonClick();
            }
        });
        bt_register.setEnabled(false);


        iv_name_validation_status = (ImageView) findViewById(R.id.iv_name_validation_status_register);
        iv_email_validation_status = (ImageView) findViewById(R.id.iv_email_validation_status_register);
        iv_password_validation_status = (ImageView) findViewById(R.id.iv_password_validation_status_register);
        iv_phonenumber_validation_status = (ImageView) findViewById(R.id.iv_phonenumber_validation_status_register);

        iv_name_validation_status.setVisibility(View.GONE);
        iv_email_validation_status.setVisibility(View.GONE);
        iv_password_validation_status.setVisibility(View.GONE);
        iv_phonenumber_validation_status.setVisibility(View.GONE);


        addTextChangerListners();

    }

    private void addTextChangerListners() {

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                bt_register.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationHelper.isValidName(editable.toString())) {
                    iv_name_validation_status.setImageResource(R.drawable.checktick);
                    statuses_boolean[0] = true;
                } else {
                    iv_name_validation_status.setImageResource(R.drawable.letter_x_icon);
                    statuses_boolean[0] = false;
                }

                activateRegisterButton();

                iv_name_validation_status.setVisibility(View.VISIBLE);

            }
        });

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                bt_register.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationHelper.isValidEmail(editable.toString())) {
                    iv_email_validation_status.setImageResource(R.drawable.checktick);
                    statuses_boolean[1] = true;
                } else {
                    iv_email_validation_status.setImageResource(R.drawable.letter_x_icon);
                    statuses_boolean[1] = false;
                }

                activateRegisterButton();
                iv_email_validation_status.setVisibility(View.VISIBLE);

            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                bt_register.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationHelper.isValidPassword(editable.toString())) {
                    iv_password_validation_status.setImageResource(R.drawable.checktick);
                    statuses_boolean[2] = true;
                } else {
                    iv_password_validation_status.setImageResource(R.drawable.letter_x_icon);
                    statuses_boolean[2] = false;
                }

                activateRegisterButton();
                iv_password_validation_status.setVisibility(View.VISIBLE);
            }
        });


        et_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ValidationHelper.isValidPhonenumber(editable.toString())) {
                    iv_phonenumber_validation_status.setImageResource(R.drawable.checktick);
                } else {
                    iv_phonenumber_validation_status.setImageResource(R.drawable.letter_x_icon);
                }

                iv_phonenumber_validation_status.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

        setViewObjects();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go LoginOptionsScreen
                Intent intent = new Intent(this, LoginOptionsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleRegisterButtonClick() {
        // Remove Keyboard if active.
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(et_email.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(et_phone_number.getWindowToken(), 0);

        // If internet is not available show toast and do nothing.
        if(NetworkChecker.isNetworkAvailable(getApplicationContext()) == false) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.toast_network_error, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // Get data from fields.
        String email = et_email.getText().toString();
        String name = et_name.getText().toString();
        String password = et_password.getText().toString();
        String phone_number = et_phone_number.getText().toString();


        RegisterRequest registerRequest = RequestBuilder.createRegisterRequest(email, name, password, phone_number, null, null, null);

        RegisterUserAsyncTask task = new RegisterUserAsyncTask(getApplicationContext(), this);
        task.execute(registerRequest);
    }

    @Override
    public void handleError() {
        //TODO think what to do here

    }

    @Override
    public void handleResponse(RegisterResponse response) {
        Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT);
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //TODO update_fb different flow.
        if(response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_NEW) ||
                response.getRegisterResponseType().equals(RegisterResponseTypes.SUCCESSFUL_FB_NEW) ||
                response.getRegisterResponseType().equals(RegisterResponseTypes.UPDATED_FB)) {
            SessionManager sessionManager = SessionManager.getSessionManager(getApplicationContext());
            boolean loginResponse = sessionManager.updateLoginState(1, response.getU_id(), null);

            if(loginResponse == true) {
                toast.setText(R.string.toast_register_successful);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                Intent intentToCreateBloodProfile = new Intent(getApplicationContext(), CreateBloodProfileActivity.class);
                intentToCreateBloodProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentToCreateBloodProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentToCreateBloodProfile);
            }
        } else if(response.getRegisterResponseType().equals(RegisterResponseTypes.DUPLICATE_EMAIL)) {
            toast.setText(R.string.toast_duplicate_email);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }
}
