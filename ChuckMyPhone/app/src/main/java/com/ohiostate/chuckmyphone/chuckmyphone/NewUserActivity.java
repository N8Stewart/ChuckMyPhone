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

    private FirebaseHelper firebaseHelper;

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

        firebaseHelper = FirebaseHelper.getInstance();
        Log.d(TAG, "onCreate() called");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_new_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New User");

        initializeViews();
    }

    //connect all views to view instances
    private void initializeViews() {
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
        switch (v.getId()) {
            case R.id.new_user_facebook_button:
                //Facebook login logic goes here

                break;
            case R.id.new_user_sign_up_button:
                if (isReadyToCreateAccount()) {
                    //Account creation works asynchronously
                    //accountWasCreated() or accountWasNotCreated() will be called when the account is done being created
                    createUserData();
                }
                break;
            case R.id.new_user_cancel_button:
                startActivity(new Intent(getApplication(), LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    //ensures user has all necessary fields filled, that the passwords match, and that the terms of service box is checked
    private boolean isReadyToCreateAccount() {
        boolean isReady = false;
        if (necessaryFieldsAreFull()) {
            if (passwordEditText.getText().toString().equals(passwordConfirmationEditText.getText().toString())) {
                if (termsOfServiceCheckBox.isChecked()) {
                    isReady = true;
                } else {
                    Toast.makeText(this.getApplicationContext(), "Please read the terms of service and check the box saying you agree to them", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Your passwords don't match, please re-enter them both", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "You need to fill out all four fields", Toast.LENGTH_LONG).show();
        }
        return isReady;
    }

    //call firebase to create the user data (works asynchronously)
    private void createUserData() {
        //Deal with Firebase user creation
        firebaseHelper.createUserWithoutFacebook(emailEditText.getText().toString(), passwordEditText.getText().toString(), usernameEditText.getText().toString(), this);
    }

    //checks if all 4 user input fields have at least 1 character entered
    private boolean necessaryFieldsAreFull() {
        boolean fieldsAreFull = !(usernameEditText.getText().toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(passwordEditText.getText().toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(passwordConfirmationEditText.toString().equals(""));
        fieldsAreFull = fieldsAreFull && !(emailEditText.getText().toString().equals(""));
        return fieldsAreFull;
    }

    //called by Firebase helper when an account is successfully created. Don't call from anywhere else
    protected void accountWasCreated() {
        //update shared preferences
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        sharedPreferencesHelper.clearSharedData();
        sharedPreferencesHelper.setSharedPreferencesData(emailEditText.getText().toString(), passwordConfirmationEditText.getText().toString());

        //save the username to the current user instance
        CurrentUser.getInstance().updateUsername(usernameEditText.getText().toString());

        //account was created successfully, navigate back to login page
        Toast.makeText(this.getApplicationContext(), "Account was successfully created, logging in now!", Toast.LENGTH_SHORT).show();
        this.startActivity(new Intent(this.getApplication(), LoginActivity.class));
    }

    //called by Firebase helper when an account is not successfully created. Don't call from anywhere else
    protected void accountWasNotCreated(String error) {
        Toast.makeText(this.getApplicationContext(), "Account was not successfully created: " + error, Toast.LENGTH_LONG).show();
    }

}