package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import java.util.ArrayList;
import java.util.Locale;

public abstract class CompeteFragment extends Fragment implements SensorEventListener {

    final String TAG = this.getClass().getSimpleName();

    boolean userHasSensor;
    boolean isRecording;

    long lastUpdate;
    // Score of the different compete screens
    long score;
    long runHighScore;

    ArrayList<String> badgeUnlockNames;

    CurrentUser currentUser;

    SensorManager sensManager;

    private OnFragmentInteractionListener mListener;

    // Control the progress of the progress bar
    private int progress;

    private final long NUM_MILLISECONDS_FOR_ACTION = 5000;
    private final long SCORE_VIEW_UPDATE_FREQUENCY = 100; //higher number leads to lower refresh rate

    Runnable updateViewSubRunnableScore;
    Runnable showTutorialToastRunnable;

    Thread updateViewRunnableThread;


    // Views
    private Animation progressBarAnimation;

    private ImageButton competeButton;

    private ProgressBar progressBar;

    TextView yourBestScoreTextView;
    TextView currentScoreTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = CurrentUser.getInstance();

        badgeUnlockNames = new ArrayList<>();

        Log.d(TAG, "onCreate() called");

        isRecording = false;
        lastUpdate = 0;
    }

    void initializeViews(View view) {
        currentScoreTextView = (TextView) view.findViewById(R.id.compete_measure_textview);
        yourBestScoreTextView = (TextView) view.findViewById(R.id.compete_best_score_textview);
        competeButton = (ImageButton) view.findViewById(R.id.compete_button);

        competeButton.setOnClickListener(buttonListener);

        // Orient the progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.compete_progress_bar);
        progressBarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.pivot_center);
        progressBarAnimation.setFillAfter(true);
        progressBar.startAnimation(progressBarAnimation);

        currentScoreTextView.setText(String.format(Locale.ENGLISH, "%d", score));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_compete, container, false);

        if (!MiscHelperMethods.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), "You have no internet connection currently\nScores will only be saved locally until an internet connection is re-established", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!CurrentUser.getInstance().isGPSEnabled() && !CurrentUser.getInstance().playedOnceWithoutGps()){
            Toast.makeText(getActivity(), "Please, enable the GPS", Toast.LENGTH_SHORT).show();
            CurrentUser.getInstance().updatePlayedOnceWithoutGps();
        } else if (CurrentUser.getInstance().needToUpdateLocation() && !CurrentUser.getInstance().playedOnceWithGps()){
            Toast.makeText(getActivity(), "Please wait for the GPS update your location", Toast.LENGTH_LONG).show();
            CurrentUser.getInstance().updatePlayedOnceWithGps();
        }

        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();

        score = 0;
        runHighScore = 0;
        isRecording = false;
        progress = 100;
        getActivity().runOnUiThread(updateViewRunnable);

        Log.d(TAG, "onPause() called");
        sensManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        sensManager.unregisterListener(this);
    }

    private final View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (userHasSensor) {
                isRecording = !isRecording;
                if (isRecording)
                    runHighScore = 0;

                MiscHelperMethods.setUserNavigatedAway(false);

                getActivity().runOnUiThread(updateViewSubRunnableImage);
                updateViewRunnableThread.start();
            }

        }
    };

    private void triggerPopUp(String badgeName) {
        Fragment f = this;
        MiscHelperMethods.initiatePopupWindow(badgeName, f);
    }

    //create a updateViewRunnable thread to run to listen for and update current rotationSpeed
    final Runnable updateViewRunnable = new Runnable() {
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
                    e.printStackTrace();
                }

                //This code updates the UI, needs to be separate because on the original thread can touch the views
                //this code may fail if it is performed when user transitions to other page during it
                try {
                    getActivity().runOnUiThread(updateViewSubRunnableScore);
                    progress = (int) ((timeUntilEnd - timeNow) * 100 / NUM_MILLISECONDS_FOR_ACTION);
                    getActivity().runOnUiThread(updateProgressBar);
                } catch (Exception e) {
                    //its okay for this chunk to fail, so don't print stuff here. We all make mistakes sometimes
                }
                timeNow = System.currentTimeMillis();
            }

            //once the loop is done, stop recording and switch the image back to the play button
            isRecording = false;

            //if a badge was unlocked during the run
            if (badgeUnlockNames.size() != 0 && !MiscHelperMethods.userNavigatedAway) {
                //to avoid concurrency issues, clone the list of badges to unlock
                ArrayList<String> tempBadges = (ArrayList<String>) badgeUnlockNames.clone();
                for (String badgeName : tempBadges) {
                    FirebaseHelper.getInstance().unlockBadge(badgeName);
                }
                //only display pop up for most impressive badge
                String badgeName = tempBadges.get(tempBadges.size()-1);
                triggerPopUp(badgeName);
                badgeUnlockNames.clear();
            }

            //This code updates the UI, needs to be separate because on the original thread can touch the views
            //this code may fail if it is performed when user transitions to other page during it
            try {
                getActivity().runOnUiThread(updateViewSubRunnableImage);
                getActivity().runOnUiThread(updateProgressBar);
            } catch (Exception e) {
                //its okay for this chunk to fail, so don't print stuff here. We all make mistakes sometimes
            }
        }
    };

    private final Runnable updateViewSubRunnableImage = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                competeButton.setImageResource(R.drawable.compete_stop);
            } else {
                competeButton.setImageResource(R.drawable.compete_play);
            }
        }
    };

    private final Runnable updateProgressBar = new Runnable() {

        @Override
        public void run() {
            if (!MiscHelperMethods.userNavigatedAway) {
                if (isRecording) {
                    progressBar.setProgress(progress);
                } else {
                    progressBar.setProgress(getContext().getResources().getInteger(R.integer.progress_bar_default));
                }
            }
        }
    };

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
}