package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CompeteSpinFragment extends CompeteFragment{
    Sensor gyroscope;

    MediaPlayer spinSound;

    private final String TUTORIAL_TEXT = "Click the arrow to begin, then spin your phone!";

    public CompeteSpinFragment() {}

    public static CompeteSpinFragment newInstance(String param1, String param2) {
        CompeteSpinFragment fragment = new CompeteSpinFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinSound = MediaPlayer.create(getActivity(), R.raw.goofy_scream_sound);

        //max rotation speed is set when the scores are grabbed, no need to initialize here
        //maxRotationSpeed = 0;
        score = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initializeViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");

        //make the sensor start listening again
        initializeSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //need to reinstantiate this at least once between each button press so a thread isn't run
        //while it is already running. This is a convenient spot that happens at least once
        //between button presses. May need to put in a delay if performance suffers (note, thread isn't run until later)
        updateViewRunnableThread = new Thread(updateViewRunnable);

        Sensor mySensor = event.sensor;

        //update speeds
        if (isRecording && mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            long currTime = System.currentTimeMillis();

            if (score > 600 && CurrentUser.getInstance().getSoundEnabled()) {
                spinSound.start();
            }

            if ((currTime - lastUpdate) > 10) {
                long dt = (currTime - lastUpdate);
                lastUpdate = currTime;

                score = (long)((Math.abs(ax) + Math.abs(ay) + Math.abs(az)) * 100);
                if (score > currentUser.getSpinScore()) {
                    currentUser.updateSpinScore(score, mGPSHelper.getLatitude(), mGPSHelper.getLongitude());
                    if (score >= Badge.BADGE_SPIN_LEVEL_1_SCORE()) {
                        FirebaseHelper.getInstance().unlockBadge("Inelastic Gymnastics");
                    }
                    if (score >= Badge.BADGE_SPIN_LEVEL_2_SCORE()) {
                        FirebaseHelper.getInstance().unlockBadge("Enthusiastic Gymnastics");
                    }
                    if (score >= Badge.BADGE_SPIN_LEVEL_3_SCORE()) {
                        FirebaseHelper.getInstance().unlockBadge("Bombastic Gymnastics");
                    }
                }
            }
        }
    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        gyroscope = sensManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //make the sensor start listening, don't want this here later
        userHasSensor = sensManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);

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
                currentScoreTextView.setText(String.format("%d", score));
                if (currentUser.getSpinScore() == 0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Your best: %d", currentUser.getSpinScore()));
                }
            }
        };

        showTutorialToastRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), "Spin your phone now! \n(disable this message in settings menu)", Toast.LENGTH_LONG).show();
            }
        };
    }
}