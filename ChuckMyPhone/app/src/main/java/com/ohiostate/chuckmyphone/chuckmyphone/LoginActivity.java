package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button button_new_user;
    Button button_login;
    Button button_fb;
    TextView button_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
}