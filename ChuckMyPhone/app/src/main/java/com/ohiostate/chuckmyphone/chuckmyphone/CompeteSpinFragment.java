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
 * {@link CompeteSpinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompeteSpinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompeteSpinFragment extends CompeteFragment{
    Sensor gyroscope;

    private final String TUTORIAL_TEXT = "Click the arrow to begin, then spin your phone!";

    public CompeteSpinFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompeteSpinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompeteSpinFragment newInstance(String param1, String param2) {
        CompeteSpinFragment fragment = new CompeteSpinFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            if ((currTime - lastUpdate) > 10) {
                long dt = (currTime - lastUpdate);
                lastUpdate = currTime;

                score = (long)((Math.abs(ax) + Math.abs(ay) + Math.abs(az)) * 100);
                if (score > currentUser.getSpinScore()) {
                    currentUser.updateSpinScore(score);
                }
            }
        }
    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        gyroscope = sensManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //make the sensor start listening, don't want this here later
        userHasSensor = sensManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

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
                if (currentUser.getSpinScore() == 0.0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Your best: %d", currentUser.getSpinScore()));
                }
            }
        };
    }
}