package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompeteDropFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompeteDropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompeteDropFragment extends CompeteFragment {
    private final float FALLING_MIN_ACCELERATION = 8.5f;
    private final float FALLING_MAX_ACCELERATION = 11.5f;

    private final String TUTORIAL_TEXT = "Click the arrow to begin, then drop your phone!";

    private double acceleration;
    private boolean isFalling;

    private long fallingStartTime;
    private long fallingEndTime;

    Sensor linearAccelerometer;

    public CompeteDropFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompeteDropFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompeteDropFragment newInstance(String param1, String param2) {
        CompeteDropFragment fragment = new CompeteDropFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        score = 0;
        acceleration = 0;
        isFalling = false;

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

        if (isRecording && sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                //long dt = (curTime - lastUpdate);
                lastUpdate = curTime;

                acceleration = Math.sqrt(ax*ax + ay*ay + az*az);

                //if phone starts falling
                if (!isFalling && acceleration > FALLING_MIN_ACCELERATION) {
                    fallingStartTime = System.currentTimeMillis();
                    isFalling = true;
                }

                //TODO
                //Need a way to ensure that the user does not just wave their phones around?

                //if phone stops falling
                if (isFalling && (acceleration < FALLING_MIN_ACCELERATION || acceleration > FALLING_MAX_ACCELERATION)) {
                    fallingEndTime = System.currentTimeMillis();
                    score = (fallingEndTime-fallingStartTime);
                    isFalling = false;
                }

                //if new high score
                if (score > currentUser.getDropScore()) {
                    currentUser.updateDropScore(score);
                }
            }
        }
    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        linearAccelerometer = sensManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //make the sensor start listening
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (!userHasSensor) {
            Toast.makeText(getActivity().getApplicationContext(), "Your phone does not have the necessary sensors for this activity", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeViews(View view) {
        super.initializeViews(view);

        currentScoreTextView.setText(String.format("%d", score));
        yourBestScoreTextView.setText(TUTORIAL_TEXT);

        updateViewRunnable = new Runnable() {
            public void run() {
                long timeUntilEnd = System.currentTimeMillis() + NUM_MILLISECONDS_FOR_ACTION;
                long timeNow = System.currentTimeMillis();
                while (isRecording && (timeNow < timeUntilEnd)) {
                    if (timeNow % SCORE_VIEW_UPDATE_FREQUENCY == 0) {
                        //This code updates the UI, needs to be separate because on the original thread can touch the views
                        getActivity().runOnUiThread(updateViewSubRunnableScore);
                    }
                    timeNow = System.currentTimeMillis();
                }

                //once the loop is done, stop recording and switch the image back to the play button
                isRecording = false;
                //This code updates the UI, needs to be separate because on the original thread can touch the views
                getActivity().runOnUiThread(updateViewSubRunnableImage);
            }
        };

        updateViewSubRunnableScore = new Runnable() {
            @Override
            public void run() {
                currentScoreTextView.setText(String.format("%d", score));
                if (currentUser.getDropScore() == 0.0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Longest Fall: %d", currentUser.getDropScore()));
                }
            }
        };
    }
}
