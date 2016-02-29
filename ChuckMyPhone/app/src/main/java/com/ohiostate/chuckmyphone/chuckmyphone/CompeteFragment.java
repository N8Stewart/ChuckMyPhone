package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} abstract subclass.
 * Activities that contain this fragment must implement the
 * {@link CompeteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public abstract class CompeteFragment extends Fragment implements SensorEventListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    protected static final String ARG_PARAM2 = "param2";

    protected String TAG = this.getClass().getSimpleName();

    SensorManager sensManager;
    Sensor linearAccelerometer;

    protected boolean userHasSensor;
    protected boolean isRecording;
    protected long lastUpdate;

    TextView yourBestTextView;
    TextView currentSpeedTextView;
    ImageButton competeButton;

    // TODO: Rename and change types of parameters
    protected String mParam1;
    protected String mParam2;

    protected OnFragmentInteractionListener mListener;

    public CompeteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        isRecording = false;
        lastUpdate = 0;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        linearAccelerometer = sensManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //make the sensor start listening, don't want this here later
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensManager.unregisterListener(this);
    }

}
