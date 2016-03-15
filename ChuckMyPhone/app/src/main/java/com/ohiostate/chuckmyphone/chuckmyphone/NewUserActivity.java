package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.FirebaseError;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Semaphore;



public class NewUserActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = this.getClass().getSimpleName();

    FirebaseHelper firebaseHelper;

    private Button cancelButton;
    private Button fbButton;
    private Button signUpButton;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmationEditText;
    private EditText emailEditText;

    private CheckBox termsOfServiceCheckBox;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        firebaseHelper = FirebaseHelper.getInstance();
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

        switch (v.getId()) {
            case R.id.new_user_facebook_button:
                intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                createUserData();
                finish();
                break;
            case R.id.new_user_sign_up_button:
                if (necessaryFieldsAreFull()) {
                    if (passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
                        if (termsOfServiceCheckBox.isChecked()) {
                            createUserData();
                        } else {
                            Toast.makeText(this.getApplicationContext(), "Please read the terms of service and check the box saying you agree to them", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this.getApplicationContext(), "Your passwords don't match, please re-enter them both", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(), "You need to fill out all four fields", Toast.LENGTH_LONG).show();
                }

                break;
            default: // cancel button case
                startActivity(new Intent(getApplication(), LoginActivity.class));
                finish();
                break;
        }
    }

    private void createUserData() {
        //Deal with Firebase user creation
        firebaseHelper.createUserWithoutFacebook(emailEditText.getText().toString(), passwordEditText.getText().toString(), this);
    }

    private boolean necessaryFieldsAreFull() {
        boolean fieldsAreFull = !(usernameEditText.getText().toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(passwordEditText.getText().toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(passwordConfirmationEditText.toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(emailEditText.getText().toString().equals(""));
        return fieldsAreFull;
    }

    //called by Firebase helper when an account is successfully created
    public static void accountWasCreated(NewUserActivity activity) {
        //account was created successfully, navigate back to login page
        Toast.makeText(activity.getApplicationContext(), "Account was successfully created, try logging in!", Toast.LENGTH_LONG).show();
        activity.startActivity(new Intent(activity.getApplication(), LoginActivity.class));
    }

    //called by Firebase helper when an account is not successfully created
    public static void accountWasNotCreated(String error, NewUserActivity activity) {
        Toast.makeText(activity.getApplicationContext(), "Account was not successfully created: " + error, Toast.LENGTH_LONG).show();
    }

}