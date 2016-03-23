package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompeteChuckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompeteChuckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompeteChuckFragment extends CompeteFragment{
    private final String TUTORIAL_TEXT = "Click the arrow to begin, then chuck your phone!";

    Sensor linearAccelerometer;

    public CompeteChuckFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompeteChuckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompeteFragment newInstance(String param1, String param2) {
        CompeteFragment fragment = new CompeteChuckFragment();
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
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            Log.d(TAG, "ax=" + ax + " | ay=" + ay + " | az=" + az);

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                long dt = (curTime - lastUpdate);
                lastUpdate = curTime;

                //not actually speed, but that is hard to derive
                score = (long) Math.sqrt(ax * ax + ay * ay + az * az);

                //if new high score
                if (score > currentUser.getChuckScore()) {
                    currentUser.updateChuckScore(score);
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

        currentScoreTextView.setText(String.valueOf(score));
        yourBestScoreTextView.setText(TUTORIAL_TEXT);

        updateViewSubRunnableScore = new Runnable() {
            @Override
            public void run() {
                currentScoreTextView.setText(String.valueOf(score));
                if (currentUser.getChuckScore() == 0.0) {
                    yourBestScoreTextView.setText(TUTORIAL_TEXT);
                } else{
                    yourBestScoreTextView.setText(String.format("Your best: %.3f m/s", currentUser.getChuckScore()));
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
