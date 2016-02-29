package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompeteSpinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompeteSpinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompeteSpinFragment extends CompeteFragment{
    private float rotationSpeed;
    private float maxSpeed;
    Sensor gyroscope;

    public CompeteSpinFragment() {
        // Required empty public constructor
    }

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

        maxSpeed = 0;
        rotationSpeed = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_compete_spin, container, false);

        initializeSensors();
        initializeViews(view);

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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

                //not actually rotationSpeed, but that is hard to derive
                rotationSpeed = Math.abs(ax) + Math.abs(ay) + Math.abs(az);
                if (rotationSpeed > maxSpeed) {
                    maxSpeed = rotationSpeed;
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
    }

    public void initializeViews(View view) {
        currentSpeedTextView = (TextView) view.findViewById(R.id.CompeteSpinActivityCurrentSpeedTextView);
        yourBestTextView = (TextView) view.findViewById(R.id.CompeteSpinActivityYourBestTextBox);
        competeButton = (ImageButton) view.findViewById(R.id.CompeteSpinActivityCompeteButton);

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
                        currentSpeedTextView.setText(rotationSpeed + " m/s");
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

        //make the sensor start listening again
        userHasSensor = sensManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
