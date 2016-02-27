package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    Button button_new_user;

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
            }
        });
        //Set title at top of screen to "Login"
        getSupportActionBar().setTitle("Login");
    }

}
