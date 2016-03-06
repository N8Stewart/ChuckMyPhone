package com.ohiostate.chuckmyphone.chuckmyphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ForgotPasswordActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    Button button_cancel;
    Button button_reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Log.d(TAG, "onCreate() called");

        getSupportActionBar().setTitle("Forgot Password");

        button_cancel = (Button) findViewById(R.id.forgotPasswordActivityCancelButton);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button_reset = (Button) findViewById(R.id.button_reset);
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
