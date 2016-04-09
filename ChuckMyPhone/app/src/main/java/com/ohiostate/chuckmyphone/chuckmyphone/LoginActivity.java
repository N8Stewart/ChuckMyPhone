package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private Button newUserButton;
    private Button loginButton;

    private boolean actionPending;

    private TextView forgotPasswordTextView;

    private EditText passwordEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate() called");

        actionPending = false;

        if (SharedPreferencesHelper.hasSharedData(getApplicationContext())) {
            attemptLogin(SharedPreferencesHelper.getEmail(getApplicationContext()),
                    SharedPreferencesHelper.getPassword(getApplicationContext()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        initializeViews();
    }

    //connect all views to view instances
    public void initializeViews() {
        newUserButton = (Button) findViewById(R.id.login_new_user_button);
        newUserButton.setOnClickListener(this);

        forgotPasswordTextView = (TextView) findViewById(R.id.login_forgot_password_textview);
        forgotPasswordTextView.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        emailEditText = (EditText) findViewById(R.id.login_email_edit_text);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        if (!actionPending) {
            switch (v.getId()) {
                case R.id.login_login_button:
                    if (FirebaseHelper.getInstance().hasLoadedInitialSnapshot) {
                        attemptLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
                    } else {
                        Toast.makeText(this.getApplicationContext(), "Still loading, please try again in a second\nIf this persists for more than a minute, check your internet connection", Toast.LENGTH_LONG).show();
                    }
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
        } else {
            Toast.makeText(this.getApplicationContext(), "Loading your previous request, please wait", Toast.LENGTH_LONG).show();
        }
    }

    public boolean attemptLogin(String email, String password) {
        boolean loginSuccessful = false;
        if (isNetworkAvailable()) {
            if (!email.equals("")) {
                if (!password.equals("")) {
                    actionPending = true;
                    Toast.makeText(this.getApplicationContext(), "Logging in, please wait...", Toast.LENGTH_SHORT).show();
                    boolean firebaseWasLoaded = FirebaseHelper.getInstance().login(email, password, this);
                    if (!firebaseWasLoaded) {
                        actionPending = false;
                        Toast.makeText(this.getApplicationContext(), "App is still loading, please try to login again in a second", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Please enter your username", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "You have no internet, please try again when you get internet", Toast.LENGTH_LONG).show();
        }
        return loginSuccessful;
    }

    //called by firebase when login is successfully performed. Don't call from anywhere else
    protected void onSuccessfulLogin(String email, String password, String userID) {

        Toast.makeText(this.getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();

        if (CurrentUser.getInstance().getUsername().equals("USERNAME NOT ASSIGNED")) {
            CurrentUser.getInstance().assignUsername(FirebaseHelper.getInstance().getUsername(userID));
        }

        CurrentUser.getInstance().loadUserBadgeData();

        if(!SharedPreferencesHelper.hasSharedData(getApplicationContext())){
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

        actionPending = false;

        this.startActivity(new Intent(this.getApplication(), MainActivity.class));
        finish();
    }

    //called by firebase when login is not successfully performed. Don't call from anywhere else
    protected void onUnsuccessfulLogin(String error) {
        Toast.makeText(this.getApplicationContext(), "Login Unsuccessful: " + error, Toast.LENGTH_LONG).show();
        actionPending = false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}