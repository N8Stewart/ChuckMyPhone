package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public abstract class CompeteFragment extends Fragment implements SensorEventListener {

    protected final String TAG = this.getClass().getSimpleName();

    SensorManager sensManager;

    protected boolean userHasSensor;
    protected boolean isRecording;
    protected long lastUpdate;
    protected ArrayList<String> badgeUnlockNames;

    protected static boolean userNavigatedAway;

    // Control the progress of the progress bar
    protected int progress;

    // Score of the different compete screens
    protected long score;
    // Largest score achieved in the past run
    protected long runHighScore;

    protected long NUM_MILLISECONDS_FOR_ACTION = 5000;
    protected long SCORE_VIEW_UPDATE_FREQUENCY = 100; //higher number leads to lower refresh rate
    protected int NUM_SECONDS_BADGE_POPUP_DISMISS = 6;

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

        badgeUnlockNames = new ArrayList<>();

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

        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity().getApplicationContext(), "You have no internet connection currently\nScores will only be saved locally until an internet connection is re-established", Toast.LENGTH_LONG).show();
        }

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

        if (!CurrentUser.getInstance().isGPSEnabled()){
            Toast.makeText(getActivity(), "Please, enable the GPS", Toast.LENGTH_SHORT).show();
        } else if (CurrentUser.getInstance().needToUpdateLocation()){
            Toast.makeText(getActivity(), "Please wait for the GPS update your location", Toast.LENGTH_LONG).show();
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

    View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (userHasSensor) {
                isRecording = !isRecording;
                if (isRecording)
                    runHighScore = 0;

                userNavigatedAway = false;

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
                try {
                    Thread.sleep(SCORE_VIEW_UPDATE_FREQUENCY, 0);
                } catch (Exception e) {

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
            if (badgeUnlockNames.size() != 0) {
                //to avoid concurrency issues, clone the list of badges to unlock
                ArrayList<String> tempBadges = (ArrayList<String>) badgeUnlockNames.clone();
                for (String badgeName : tempBadges) {
                    FirebaseHelper.getInstance().unlockBadge(badgeName);
                }
                //only display pop up for most impressive badge
                initiatePopupWindow(tempBadges.get(tempBadges.size()-1));

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

    protected PopupWindow pw;
    protected void initiatePopupWindow(String badgeName) {
        if (CurrentUser.getInstance().getBadgeNotificationsEnabled()) {
            try {
                final String bName = badgeName;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup, (ViewGroup) getActivity().findViewById(R.id.popup_element));
                        // 800 px by 800 px
                        pw = new PopupWindow(layout, 800, 800, true);

                        TextView badgeTitle = (TextView) layout.findViewById(R.id.popup_BadgeTitleTextView);
                        TextView badgeDescription = (TextView) layout.findViewById(R.id.popup_BadgeDescriptionTextView);

                        badgeTitle.setText(Html.fromHtml("<i>" + bName + "</i>"));
                        badgeDescription.setText("\n" + Badge.badgeNameToDescriptionMap.get(bName));

                        Button cancelButton = (Button) layout.findViewById(R.id.popup_cancel_button);
                        cancelButton.setOnClickListener(cancel_button_click_listener);

                        // display the popup in the center if user didn't navigate away quickly via hamburger menu
                        if (!userNavigatedAway) {
                            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                        }

                        userNavigatedAway = false;

                        Thread badgeTimeoutThread = new Thread(badgeTimeoutRunnable);
                        badgeTimeoutThread.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void userNavigatedAway() {
        userNavigatedAway = true;
    }

    public static void initializeUserNavigationTracking() {
        userNavigatedAway = false;
    }

    private Runnable badgeTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(NUM_SECONDS_BADGE_POPUP_DISMISS*1000, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pw.dismiss();
                }
            });
        }
    };

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}