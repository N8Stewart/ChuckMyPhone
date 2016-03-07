package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button cancelButton;
    private Button fbButton;
    private Button signUpButton;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmationEditText;
    private EditText emailEditText;

    private CheckBox termsOfServiceCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Log.d(TAG, "onCreate() called");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_new_user_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("New User");

        cancelButton = (Button) findViewById(R.id.new_user_cancel_button);
        cancelButton.setOnClickListener(this);

        signUpButton = (Button) findViewById(R.id.new_user_sign_up_button);
        signUpButton.setOnClickListener(this);

        fbButton = (Button) findViewById(R.id.new_user_facebook_button);
        fbButton.setOnClickListener(this);

        usernameEditText = (EditText) findViewById(R.id.new_user_username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.new_user_password_edit_text);
        passwordConfirmationEditText = (EditText) findViewById(R.id.new_user_password_confirmation_edit_text);
        emailEditText = (EditText) findViewById(R.id.new_user_email_edit_text);

        termsOfServiceCheckBox = (CheckBox) findViewById(R.id.new_user_terms_of_service_checkbox);
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
            case R.id.new_user_facebook_button:
                intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.new_user_sign_up_button:
                intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                finish();
                break;
        }
    }
}
