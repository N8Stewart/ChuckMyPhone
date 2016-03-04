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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompeteChuckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompeteChuckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompeteChuckFragment extends CompeteFragment{
    private double speed; //Speed is in meters per second
    private double maxSpeed;

    public CompeteChuckFragment() {
        // Required empty public constructor
    }

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

        maxSpeed = 0;
        speed = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_compete_chuck, container, false);

        initializeSensors();
        initializeViews(view);

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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
                speed = Math.sqrt(ax * ax + ay * ay + az * az);
                if (speed > maxSpeed) {
                    maxSpeed = speed;
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
    }

    public void initializeViews(View view) {
        currentSpeedTextView = (TextView) view.findViewById(R.id.CompeteChuckActivityCurrentSpeedTextView);
        yourBestTextView = (TextView) view.findViewById(R.id.CompeteChuckActivityYourBestTextBox);
        competeButton = (ImageButton) view.findViewById(R.id.CompeteChuckActivityCompeteButton);

        if (!userHasSensor) {
            yourBestTextView.setText("Your phone does not have the necessary sensors for this activity");
        }

        competeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userHasSensor) {
                    isRecording = !isRecording;
                    if (isRecording) {
                        competeButton.setImageResource(R.drawable.compete_stop);
                    } else {
                        competeButton.setImageResource(R.drawable.compete_play);
                    }
                    Thread mythread = new Thread(updateViewRunnable);
                    mythread.start();
                }
            }
        });
    }

    //create a updateViewRunnable thread to run to listen for and update current rotationSpeed
    Runnable updateViewRunnable = new Runnable() {
        public void run() {
            int count = 1;
            while (isRecording && count < 50000) {
                count++;

                //This code updates the UI, needs to be separate because on the original thread can touch the views
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentSpeedTextView.setText(speed + " m/s");
                        yourBestTextView.setText("Your best: " + maxSpeed + " m/s");
                    }
                });
            }

            //once the loop is done, stop recording and switch the image back to the play button
            isRecording = false;
            //This code updates the UI, needs to be separate because on the original thread can touch the views
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    competeButton.setImageResource(R.drawable.compete_play);
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        //make the sensor start listening again
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
}
