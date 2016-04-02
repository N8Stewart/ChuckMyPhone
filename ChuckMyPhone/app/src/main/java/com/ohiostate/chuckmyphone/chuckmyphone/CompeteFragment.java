package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public abstract class CompeteFragment extends Fragment implements SensorEventListener, GpsStatus.Listener {

    protected final String TAG = this.getClass().getSimpleName();

    SensorManager sensManager;
    GPSHelper mGPSHelper;

    protected boolean userHasSensor;
    protected boolean isRecording;
    protected long lastUpdate;

    // Control the progress of the progress bar
    protected int progress;

    // Score of the different compete screens
    protected long score;

    protected long NUM_MILLISECONDS_FOR_ACTION = 5000;
    protected long SCORE_VIEW_UPDATE_FREQUENCY = 100; //higher number leads to lower refresh rate

    ProgressBar progressBar;
    Animation progressBarAnimation;
    TextView yourBestScoreTextView;
    TextView currentScoreTextView;
    ImageButton competeButton;

    Thread updateViewRunnableThread;

    protected Runnable updateViewSubRunnableScore;
    protected Runnable showTutorialToastRunnable;

    protected CurrentUser currentUser;

    protected OnFragmentInteractionListener mListener;

    public CompeteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = CurrentUser.getInstance();

        mGPSHelper = new GPSHelper(getActivity());
        mGPSHelper.requestPermissionForGPS(getActivity());

        Log.d(TAG, "onCreate() called");

        isRecording = false;
        lastUpdate = 0;
    }

    public void initializeViews(View view) {
        currentScoreTextView = (TextView) view.findViewById(R.id.compete_measure_textview);
        yourBestScoreTextView = (TextView) view.findViewById(R.id.compete_best_score_textview);
        competeButton = (ImageButton) view.findViewById(R.id.compete_button);

        competeButton.setOnClickListener(buttonListener);

        // Orient the progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.compete_progress_bar);
        progressBarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.pivot_center);
        progressBarAnimation.setFillAfter(true);
        progressBar.startAnimation(progressBarAnimation);

        currentScoreTextView.setText(String.format("%d", score));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_compete, container, false);
        return view;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mGPSHelper.requestLocation(getActivity(), LocationManager.GPS_PROVIDER);
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
        mGPSHelper.stopGPS(getActivity());
        sensManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        mGPSHelper.stopGPS(getActivity());
        sensManager.unregisterListener(this);
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(mGPSHelper.isPreciseGPSPresent()) {
                if(mGPSHelper.isPreciseGPSEnabled()) {
                    if (userHasSensor) {
                        isRecording = !isRecording;
                        getActivity().runOnUiThread(updateViewSubRunnableImage);
                        updateViewRunnableThread.start();
                    }
                } else Toast.makeText(getActivity().getApplicationContext(), "Turn on the GPS!", Toast.LENGTH_LONG).show();
            } else Toast.makeText(getActivity().getApplicationContext(), "Your device doesn't have GPS!", Toast.LENGTH_LONG).show();
        }
    };

    //create a updateViewRunnable thread to run to listen for and update current rotationSpeed
    Runnable updateViewRunnable = new Runnable() {
        public void run() {

            if (CurrentUser.getInstance().getTutorialMessagesEnabled() && isRecording) {
                Thread showTutorialMessageThread = new Thread(showTutorialToastRunnable);
                getActivity().runOnUiThread(showTutorialMessageThread);
            }

            long timeUntilEnd = System.currentTimeMillis() + NUM_MILLISECONDS_FOR_ACTION;
            long timeNow = System.currentTimeMillis();
            while (isRecording && (timeNow < timeUntilEnd)) {
                try {
                    Thread.sleep(SCORE_VIEW_UPDATE_FREQUENCY, 0);
                } catch (Exception e) {

                }

                //This code updates the UI, needs to be separate because on the original thread can touch the views
                getActivity().runOnUiThread(updateViewSubRunnableScore);
                progress = (int)((timeUntilEnd - timeNow) * 100 / NUM_MILLISECONDS_FOR_ACTION);
                getActivity().runOnUiThread(updateProgressBar);
                timeNow = System.currentTimeMillis();
            }

            //once the loop is done, stop recording and switch the image back to the play button
            isRecording = false;
            //This code updates the UI, needs to be separate because on the original thread can touch the views
            getActivity().runOnUiThread(updateViewSubRunnableImage);
            getActivity().runOnUiThread(updateProgressBar);
        }
    };

    protected Runnable updateViewSubRunnableImage = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                competeButton.setImageResource(R.drawable.compete_stop);
            } else {
                competeButton.setImageResource(R.drawable.compete_play);
            }
        }
    };

    protected Runnable updateProgressBar = new Runnable() {

        @Override
        public void run() {
            if (isRecording) {
                progressBar.setProgress(progress);
            } else {
                progressBar.setProgress(getContext().getResources().getInteger(R.integer.progress_bar_default));
            }
        }
    };
}