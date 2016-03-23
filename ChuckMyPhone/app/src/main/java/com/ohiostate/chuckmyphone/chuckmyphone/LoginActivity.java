package com.ohiostate.chuckmyphone.chuckmyphone;

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

import com.firebase.client.Firebase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button newUserButton;
    private Button loginButton;
    private Button fbButton;

    private TextView forgotPasswordTextView;

    private EditText passwordEditText;
    private EditText emailEditText;

    private static SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate() called");

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        if (sharedPreferencesHelper.hasSharedData()) {
            //user has login credentials saved, skip this and login for them
            attemptLogin(sharedPreferencesHelper.getEmail(), sharedPreferencesHelper.getPassword());
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

        fbButton = (Button) findViewById(R.id.login_facebook_button);
        fbButton.setOnClickListener(this);

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

        switch(v.getId()){
            case R.id.login_facebook_button:
                intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.login_login_button:
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

    public boolean attemptLogin(String email, String password) {
        boolean loginSuccessful = false;
        if (!email.equals("")) {
            if (!password.equals("")) {
                Toast.makeText(this.getApplicationContext(), "Logging in, please wait...", Toast.LENGTH_SHORT).show();
                FirebaseHelper.getInstance().loginWithoutFacebook(email, password, this);
            } else {
                Toast.makeText(this.getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Please enter your username", Toast.LENGTH_LONG).show();
        }
        return loginSuccessful;
    }

    //called by firebase when login is successfully performed. Don't call from anywhere else
    protected void onSuccessfulLogin(String email, String password, String userID) {
        Toast.makeText(this.getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();

        sharedPreferencesHelper.setSharedPreferencesData(email, password);

        if (CurrentUser.getInstance().getUsername().equals("USERNAME NOT ASSIGNED")) {
            CurrentUser.getInstance().assignUsername(FirebaseHelper.getInstance().getUsername(userID));
        }

        this.startActivity(new Intent(this.getApplication(), MainActivity.class));
    }

    //called by firebase when login is not successfully performed. Don't call from anywhere else
    protected void onUnsuccessfulLogin(String error) {
        Toast.makeText(this.getApplicationContext(), "Login Unsuccessful: " + error, Toast.LENGTH_LONG).show();
    }
}