package com.ohiostate.chuckmyphone.chuckmyphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private Button cancelButton;
    private Button resetButton;

    private EditText usernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Log.d(TAG, "onCreate() called");

        getSupportActionBar().setTitle("Forgot Password");

        cancelButton = (Button) findViewById(R.id.activity_forgot_password_cancel_button);
        cancelButton.setOnClickListener(this);

        resetButton = (Button) findViewById(R.id.activity_forgot_password_reset_button);
        resetButton.setOnClickListener(this);

        usernameEditText = (EditText) findViewById(R.id.activity_forgot_password_username_email_edit_text);
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
                // send email to user to reset password
                break;
            default:
                finish();
                break;
        }
    }
}
