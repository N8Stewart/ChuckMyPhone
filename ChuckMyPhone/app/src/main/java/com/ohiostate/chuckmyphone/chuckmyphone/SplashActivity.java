package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.Firebase;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private final static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //need to initialize mappings for badge descriptions
        Badge b = new Badge("");
        b.initializeDescriptionMappings(getApplicationContext());
        b.initializeDrawableMappings(getApplicationContext());
        MiscHelperMethods.initializeUserNavigationTracking();
        LoginActivity.setLoginFromNewUserScreen(false);


        Log.d(TAG, "onCreate() called");

        Firebase.setAndroidContext(this);
        FirebaseHelper.getInstance().create();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
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
}