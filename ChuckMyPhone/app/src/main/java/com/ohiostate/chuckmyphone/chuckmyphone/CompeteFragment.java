package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public abstract class CompeteFragment extends Fragment implements SensorEventListener {

    protected final String TAG = this.getClass().getSimpleName();

    SensorManager sensManager;

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
        sensManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        sensManager.unregisterListener(this);
    }

    public void setButtonImage() {
        if (isRecording) {
            competeButton.setImageResource(R.drawable.compete_stop);
        } else {
            competeButton.setImageResource(R.drawable.compete_play);
        }
    }

    View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (userHasSensor) {
                isRecording = !isRecording;
                getActivity().runOnUiThread(updateViewSubRunnableImage);

                updateViewRunnableThread.start();
            }
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
                if (timeNow % SCORE_VIEW_UPDATE_FREQUENCY == 0) {
                    //This code updates the UI, needs to be separate because on the original thread can touch the views
                    getActivity().runOnUiThread(updateViewSubRunnableScore);
                    progress = (int)((timeUntilEnd - timeNow) * 100 / NUM_MILLISECONDS_FOR_ACTION);
                    getActivity().runOnUiThread(updateProgressBar);
                }
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