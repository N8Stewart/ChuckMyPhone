package com.ohiostate.chuckmyphone.chuckmyphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.FirebaseError;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button cancelButton;
    private Button resetButton;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Log.d(TAG, "onCreate() called");

        getSupportActionBar().setTitle("Forgot Password");

        initializeViews();
    }

    public void initializeViews() {
        cancelButton = (Button) findViewById(R.id.activity_forgot_password_cancel_button);
        cancelButton.setOnClickListener(this);

        resetButton = (Button) findViewById(R.id.activity_forgot_password_reset_button);
        resetButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.activity_forgot_password_email_edit_text);
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
        switch(v.getId()){
            case R.id.activity_forgot_password_reset_button:
                if (!emailEditText.getText().toString().equals("")) {
                    FirebaseHelper.getInstance().resetPassword(emailEditText.getText().toString(), this);
                } else {
                    Toast.makeText(this.getApplicationContext(), "Please enter your email above", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                finish();
                break;
        }
    }

    public void onPasswordSuccessfullyReset() {
        Toast.makeText(this.getApplicationContext(), "Password reset email was sent", Toast.LENGTH_LONG).show();
        SharedPreferencesHelper.clearSharedData(getApplicationContext());
        finish();
    }

    public void onPasswordUnsuccessfullyReset(FirebaseError error) {
        Toast.makeText(this.getApplicationContext(), "Password reset email was not sent: "+ error.getMessage(), Toast.LENGTH_LONG).show();
    }
}