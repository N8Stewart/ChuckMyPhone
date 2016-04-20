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

    private Sensor linearAccelerometer;
    public CompeteChuckFragment() {}

    private MediaPlayer chuckSound;

    public static CompeteFragment newInstance() {
        CompeteFragment fragment = new CompeteChuckFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CurrentUser.getInstance().getGoofySoundEnabled()) {
            chuckSound = MediaPlayer.create(getActivity(), R.raw.wilhelm_scream);
        } else {
            chuckSound = MediaPlayer.create(getActivity(), R.raw.whoosh_sound);
        }
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
                    chuckSound.start();
                }

                if (score > runHighScore)
                    runHighScore = score;

                //if new high scores
                if (score > currentUser.getChuckScore()) {

                    //a weird bug sometimes has the run score being higher than the score saved in current user, this removes that possibility
                    if (runHighScore > currentUser.getChuckScore()) {
                        currentUser.updateChuckScore(runHighScore, CurrentUser.getInstance().getLatitude(), CurrentUser.getInstance().getLongitude());
                        score = runHighScore;
                    }

                    if (!badgeUnlockNames.contains(getString(R.string.badge_chuck_level_one)) && !FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_one)) && score >= Badge.BADGE_CHUCK_LEVEL_1_SCORE()) {
                        badgeUnlockNames.add(getString(R.string.badge_chuck_level_one));
                    }
                    if (!badgeUnlockNames.contains(getString(R.string.badge_chuck_level_two)) && !FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_two)) && score >= Badge.BADGE_CHUCK_LEVEL_2_SCORE()) {
                        badgeUnlockNames.add(getString(R.string.badge_chuck_level_two));
                    }
                    if (!badgeUnlockNames.contains(getString(R.string.badge_chuck_level_three)) && !FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_chuck_level_three)) && score >= Badge.BADGE_CHUCK_LEVEL_3_SCORE()) {
                        badgeUnlockNames.add(getString(R.string.badge_chuck_level_three));
                    }
                }
            }
        }
    }

    private void initializeSensors() {
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
}