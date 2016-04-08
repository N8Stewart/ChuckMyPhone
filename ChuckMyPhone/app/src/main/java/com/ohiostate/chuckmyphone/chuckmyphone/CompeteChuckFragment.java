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

public class CompeteChuckFragment extends CompeteFragment {
    private final String TUTORIAL_TEXT = "Click the arrow to begin, then chuck your phone!";
    private final int SCORE_THRESHOLD_FOR_SOUND = 1500;

    Sensor linearAccelerometer;
    public CompeteChuckFragment() {}

    MediaPlayer whooshSound;

    public static CompeteFragment newInstance(String param1, String param2) {
        CompeteFragment fragment = new CompeteChuckFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whooshSound = MediaPlayer.create(getActivity(), R.raw.whoosh_sound);

        score = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
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

        if (isRecording && mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                lastUpdate = curTime;

                //not actually speed, but that is hard to derive
                double ax = event.values[0];
                double ay = event.values[1];
                double az = event.values[2];
                score = (long)(Math.sqrt(ax * ax + ay * ay + az * az) * 100);

                if (score > SCORE_THRESHOLD_FOR_SOUND && CurrentUser.getInstance().getSoundEnabled()) {
                    whooshSound.start();
                }

                if (score > runHighScore)
                    runHighScore = score;

                //if new high score
                if (score > currentUser.getChuckScore()) {
                    currentUser.updateChuckScore(score, mGPSHelper.getLatitude(), mGPSHelper.getLongitude());

                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_one)) && !popupIsUp && score >= Badge.BADGE_CHUCK_LEVEL_1_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_chuck_level_one);
                    }
                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_two)) && !popupIsUp && score >= Badge.BADGE_CHUCK_LEVEL_2_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_chuck_level_two);
                    }
                    if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_three)) && !popupIsUp && score >= Badge.BADGE_CHUCK_LEVEL_3_SCORE()) {
                        badgeUnlockName = getString(R.string.badge_chuck_level_three);
                    }
                }
            }
        }
    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE) ;
        linearAccelerometer = sensManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //make the sensor start listening, don't want this here later
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
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

                if (currentUser.getChuckScore() == 0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Your best: %d", currentUser.getChuckScore()));
                }
            }
        };

        showTutorialToastRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), "Chuck your phone now! \n(disable this message in settings menu)", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onGpsStatusChanged(int event) {}

}