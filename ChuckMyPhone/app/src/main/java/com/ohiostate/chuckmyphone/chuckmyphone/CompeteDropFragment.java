package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CompeteDropFragment extends CompeteFragment {
    private final float FALLING_MAX_ACCELERATION = 4.0f;

    private final String TUTORIAL_TEXT = "Click the arrow to begin, then drop your phone!";

    private double acceleration;
    private boolean isFalling;

    private long fallingStartTime;
    private long fallingEndTime;

    Sensor linearAccelerometer;

    MediaPlayer explosionSound;

    public CompeteDropFragment() {}

    public static CompeteDropFragment newInstance(String param1, String param2) {
        CompeteDropFragment fragment = new CompeteDropFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        score = 0;
        acceleration = 0;
        isFalling = false;

        explosionSound = MediaPlayer.create(getActivity(), R.raw.explosion_sound);

        //max falling speed is set when the scores are grabbed, no need to initialize here
        //maxTimeFalling = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        //make the sensor start listening again
        initializeSensors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initializeViews(view);

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //need to reinstantiate this at least once between each button press so a thread isn't run
        //while it is already running. This is a convenient spot that happens at least once
        //between button presses. May need to put in a delay if performance suffers (note, thread isn't run until later)
        updateViewRunnableThread = new Thread(updateViewRunnable);

        Sensor sensor = event.sensor;

        if (isRecording && sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                //long dt = (curTime - lastUpdate);
                lastUpdate = curTime;

                acceleration = Math.sqrt(ax*ax + ay*ay + az*az);

                //if phone starts falling
                if (!isFalling && acceleration < FALLING_MAX_ACCELERATION) {
                    fallingStartTime = System.currentTimeMillis();
                    isFalling = true;
                }

                //if phone stops falling
                if (isFalling && acceleration > FALLING_MAX_ACCELERATION) {
                    fallingEndTime = System.currentTimeMillis();
                    score = (fallingEndTime-fallingStartTime);
                    isFalling = false;
                    if (CurrentUser.getInstance().getSoundEnabled()) {
                        explosionSound.start();
                    }
                }

                if (score > runHighScore)
                    runHighScore = score;

                //if new high score
                if (score > currentUser.getDropScore()) {
                    currentUser.updateDropScore(score, mGPSHelper.getLatitude(), mGPSHelper.getLongitude());
                    Log.d("coordsdrop", mGPSHelper.getLatitude() + " " + mGPSHelper.getLongitude());

                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_drop_level_one)) && !popupIsUp && score >= Badge.BADGE_DROP_LEVEL_1_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_drop_level_one);
                    }
                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_drop_level_two)) && !popupIsUp && score >= Badge.BADGE_DROP_LEVEL_2_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_drop_level_two);
                    }
                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_drop_level_three)) && !popupIsUp && score >= Badge.BADGE_DROP_LEVEL_3_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_drop_level_three);
                    }
                }
            }
        }
    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        linearAccelerometer = sensManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //make the sensor start listening
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        if (!userHasSensor) {
            Toast.makeText(getActivity().getApplicationContext(), "Your phone does not have the necessary sensors for this activity", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeViews(View view) {
        super.initializeViews(view);

        yourBestScoreTextView.setText(TUTORIAL_TEXT);

        updateViewSubRunnableScore = new Runnable() {
            @Override
            public void run() {
                if(isRecording)
                    currentScoreTextView.setText(String.format("%d", score));
                else
                    currentScoreTextView.setText(String.format("%d", runHighScore));

                if (currentUser.getDropScore() == 0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Your best: %d", currentUser.getDropScore()));
                }
            }
        };

        showTutorialToastRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), "Drop your phone now! \n(disable this message in settings menu)", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onGpsStatusChanged(int event) {}
}