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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button newUserButton;
    private Button loginButton;
    private Button fbButton;
    private TextView forgotPasswordTextView;
    private EditText passwordEditText;
    private EditText usernameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate() called");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(toolbar);

        newUserButton = (Button) findViewById(R.id.login_new_user_button);
        newUserButton.setOnClickListener(this);

        forgotPasswordTextView = (TextView) findViewById(R.id.login_forgot_password_textview);
        forgotPasswordTextView.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(this);

        fbButton = (Button) findViewById(R.id.login_facebook_button);
        fbButton.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        usernameEditText = (EditText) findViewById(R.id.login_username_edit_text);

        getSupportActionBar().setTitle("Login");
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
                break;
            case R.id.login_login_button:
                intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.login_forgot_password_textview:
                intent = new Intent(getApplication(), ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                intent = new Intent(getApplication(), NewUserActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}