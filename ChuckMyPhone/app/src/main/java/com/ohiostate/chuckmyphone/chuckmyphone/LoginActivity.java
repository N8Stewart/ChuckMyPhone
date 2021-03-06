package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private boolean loggingInWasCancelled;

    // Views
    private Button newUserButton;
    private Button loginButton;

    private EditText passwordEditText;
    private EditText emailEditText;

    private TextView forgotPasswordTextView;

    private ProgressDialog loggingInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate() called");

        initializeViews();

        if (SharedPreferencesHelper.hasSharedData(getApplicationContext())) {
            emailEditText.setText(SharedPreferencesHelper.getEmail(getApplicationContext()));
            passwordEditText.setText(SharedPreferencesHelper.getPassword(getApplicationContext()));
            attemptLogin(SharedPreferencesHelper.getEmail(getApplicationContext()),
                    SharedPreferencesHelper.getPassword(getApplicationContext()));
        }

        MiscHelperMethods.setupUI(findViewById(android.R.id.content), this);
    }

    //connect all views to view instances
    private void initializeViews() {
        newUserButton = (Button) findViewById(R.id.login_new_user_button);
        newUserButton.setOnClickListener(this);

        forgotPasswordTextView = (TextView) findViewById(R.id.login_forgot_password_textview);
        forgotPasswordTextView.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        emailEditText = (EditText) findViewById(R.id.login_email_edit_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
    }

    @Override
    public void onResume() {
        super.onResume();
        loggingInWasCancelled = false;
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_login_button:
                //remove spaces from email
                emailEditText.setText(emailEditText.getText().toString().replace(" ", ""));
                emailEditText.setText(emailEditText.getText().toString().replace("\n", ""));
                attemptLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
                break;
            case R.id.login_forgot_password_textview:
                intent = new Intent(getApplication(), ForgotPasswordActivity.class);
                startActivity(intent);

                break;
            default: //new user button case
                intent = new Intent(getApplication(), NewUserActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean attemptLogin(String email, String password) {
        boolean loginSuccessful = false;
        if (MiscHelperMethods.isNetworkAvailable(this)) {
            if (!email.equals("")) {
                if (!password.equals("")) {
                    loggingInDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in, please wait...\nIf this takes a while, check your internet connection and try again", true);

                    boolean firebaseWasLoaded = FirebaseHelper.getInstance().login(email, password, this);
                    if (!firebaseWasLoaded) {
                        Toast.makeText(this.getApplicationContext(), "App is still loading, please try to login again in a second", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "You have no internet, please try again when you get internet", Toast.LENGTH_LONG).show();
        }
        return loginSuccessful;
    }

    //called by firebase when login is successfully performed. Don't call from anywhere else
    void onSuccessfulLogin(String email, String password, String userID) {
        if (CurrentUser.getInstance().getUsername().equals("USERNAME NOT ASSIGNED")) {
            CurrentUser.getInstance().assignUsername(FirebaseHelper.getInstance().getUsername(userID));
        }
        CurrentUser.getInstance().loadUserBadgeData();

        if (!SharedPreferencesHelper.hasSharedData(getApplicationContext())) {
            SharedPreferencesHelper.createSharedPreferencesData(getApplicationContext(),
                    email, password, CurrentUser.getInstance().getUsername());
        } else {
            SharedPreferencesHelper.setSharedPreferencesData(getApplicationContext(),
                    email, password, CurrentUser.getInstance().getUsername());
        }

        CurrentUser.getInstance().loadUserSettings(SharedPreferencesHelper.getTutorialMessages(getApplicationContext()),
                SharedPreferencesHelper.getSoundEnabled(getApplicationContext()), SharedPreferencesHelper.getBadgeNotifications(getApplicationContext()),
                SharedPreferencesHelper.getGoofySoundEnabled(getApplicationContext()));

        CurrentUser.getInstance().loadUserLocation(SharedPreferencesHelper.getLatitude(getApplicationContext()),
                SharedPreferencesHelper.getLongitude(getApplicationContext()));

        if (loggingInDialog != null && loggingInDialog.isShowing()) {
            loggingInDialog.cancel();
        }

        if (!loggingInWasCancelled) {
            this.startActivity(new Intent(this.getApplication(), MainActivity.class));
            finish();
        }
    }

    //called by firebase when login is not successfully performed. Don't call from anywhere else
    void onUnsuccessfulLogin(Exception e) {

        if (e.getMessage().equals("DataSnapshot was not loaded yet")) {
            //we want to try again to login without user realizing it
            Log.d("tag", "relogging in after failure tied to firebase taking too long");
            FirebaseHelper.getInstance().login(emailEditText.getText().toString(), passwordEditText.getText().toString(), this);
        } else {
            loggingInDialog.cancel();
            SharedPreferencesHelper.clearSharedData(getApplicationContext());

            String messageToUser = e.getMessage();

            if (e.getMessage().contains("the password is invalid")) {
                messageToUser = "Incorrect password, please try a different one";
            } else if (e.getMessage().contains("There is no user record corresponding to this identifier")) {
                messageToUser = "Incorrect email, please try a different one";
            }

            Toast.makeText(this.getApplicationContext(), "Login Unsuccessful: " + messageToUser, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        loggingInWasCancelled = true;
        Log.d("onUserLeaveHint","login was cancelled");

        //if user presses home button during logging in, it should try to cancel logging in process
        if (loggingInDialog != null && loggingInDialog.isShowing()) {
            loggingInDialog.cancel();
            Toast.makeText(this.getApplicationContext(), "Login cancelled", Toast.LENGTH_LONG).show();
        }
    }
}