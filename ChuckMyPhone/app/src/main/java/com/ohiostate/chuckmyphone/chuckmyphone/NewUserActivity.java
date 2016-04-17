package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



public class NewUserActivity extends AppCompatActivity implements View.OnClickListener{

    public final int USERNAME_LENGTH_MIN = 3;
    public final int USERNAME_lENGTH_MAX = 13;

    private final String TAG = this.getClass().getSimpleName();

    private boolean actionPending;

    FirebaseHelper firebaseHelper;

    private Button cancelButton;
    private Button fbButton;
    private Button signUpButton;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmationEditText;
    private EditText emailEditText;

    private Toast creatingAccountToast;

    private CheckBox termsOfServiceCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        actionPending = false;

        creatingAccountToast = Toast.makeText(this.getApplicationContext(), "Creating Account...", Toast.LENGTH_SHORT);

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

        usernameEditText = (EditText) findViewById(R.id.new_user_username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.new_user_password_edit_text);
        passwordConfirmationEditText = (EditText) findViewById(R.id.new_user_password_confirmation_edit_text);
        emailEditText = (EditText) findViewById(R.id.new_user_email_edit_text);

        termsOfServiceCheckBox = (CheckBox) findViewById(R.id.new_user_terms_of_service_checkbox);
    }

    @Override
    public void onClick(View v) {
        if (!actionPending) {
            switch (v.getId()) {
                case R.id.new_user_sign_up_button:
                    if (isReadyToCreateAccount()) {
                        //Account creation works asynchronously
                        //accountWasCreated() or accountWasNotCreated() will be called when the account is done being created
                        actionPending = true;
                        createUserData();
                    }
                    break;
                case R.id.new_user_cancel_button:
                    startActivity(new Intent(getApplication(), LoginActivity.class));
                    finish();
                    break;
                case R.id.new_user_terms_of_service_text :
                    Uri uri = Uri.parse(this.getApplicationContext().getString(R.string.terms_of_service));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                default:
                    break;
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Loading your previous request, please wait", Toast.LENGTH_SHORT).show();
        }
    }

    //ensures user has all necessary fields filled, that the passwords match, and that the terms of service box is checked
    private boolean isReadyToCreateAccount() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordVerification = passwordConfirmationEditText.getText().toString();
        String email = emailEditText.getText().toString();

        Toast toast = null;
        boolean isReady = false;

        // Ensure username length is restricted
        if (username.length() < USERNAME_LENGTH_MIN || username.length() > USERNAME_lENGTH_MAX) {
            Toast.makeText(this.getApplicationContext(), String.format("Username must be between %d and %d characters.", USERNAME_LENGTH_MIN, USERNAME_lENGTH_MAX), Toast.LENGTH_SHORT).show();
        } else if (!isValidUsername(username)) {
            Toast.makeText(this.getApplicationContext(), "Only digits and letters are allowed in a username", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), "Please enter a password.", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordVerification)) {
            Toast.makeText(this.getApplicationContext(), String.format("Passwords do not match"), Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), "Enter a valid email to create an account.", Toast.LENGTH_SHORT).show();
        } else if (!termsOfServiceCheckBox.isChecked()) {
            Toast.makeText(this.getApplicationContext(), "Please read the terms of service and check the box saying you agree to them", Toast.LENGTH_SHORT).show();
        } else if (!isNetworkAvailable()) {
            Toast.makeText(this.getApplicationContext(), "You have no internet, please try again when you get internet", Toast.LENGTH_SHORT).show();
        } else if (!FirebaseHelper.getInstance().isUsernameAvailable(username)) {
            Toast.makeText(this.getApplicationContext(), "That username is taken, please try another", Toast.LENGTH_SHORT).show();
        } else {
            isReady = true;
        }

        return isReady;
    }

    //call firebase to create the user data (works asynchronously)
    private void createUserData() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();

        creatingAccountToast.show();

        //Deal with Firebase user creation
        firebaseHelper.createUser(email, password, username, this);
    }

    //called by Firebase helper when an account is successfully created. Don't call from anywhere else
    protected void accountWasCreated() {
        creatingAccountToast.cancel();

        //update shared preferences
        SharedPreferencesHelper.clearSharedData(getApplicationContext());
        SharedPreferencesHelper.createSharedPreferencesData(this, emailEditText.getText().toString(), passwordConfirmationEditText.getText().toString(), usernameEditText.getText().toString());
        actionPending = false;
        LoginActivity.setLoginFromNewUserScreen(true);

        //account was created successfully, navigate back to login page
        Toast.makeText(this.getApplicationContext(), "Account was successfully created, logging in now!", Toast.LENGTH_SHORT).show();
        this.startActivity(new Intent(this.getApplication(), LoginActivity.class));
    }

    //called by Firebase helper when an account is not successfully created. Don't call from anywhere else
    protected void accountWasNotCreated(String error) {
        creatingAccountToast.cancel();

        Toast.makeText(this.getApplicationContext(), "Account was not successfully created: " + error, Toast.LENGTH_LONG).show();
        actionPending = false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected boolean isValidUsername(String username) {
        boolean isValidUsername = true;
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                isValidUsername = false;
            }
        }
        return isValidUsername;
    }
}