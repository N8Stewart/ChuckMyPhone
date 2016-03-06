package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    Button button_new_user;
    Button button_login;
    Button button_fb;
    TextView button_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate() called");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_new_user = (Button) findViewById(R.id.loginActivityNewUserButton);
        button_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), NewUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_forgot_password = (TextView) findViewById(R.id.loginActivityForgotPasswordButton);
        button_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        button_login = (Button) findViewById(R.id.loginActivityLoginButton);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_fb = (Button) findViewById(R.id.loginActivityConnectWithFacebookButton);
        button_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });

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
}