package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.res.Resources;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    protected int NUM_SECONDS_BADGE_POPUP_DISMISS = 6;

    private final String CREDITS_MESSAGE = "Thank you so much for playing our game!\n\n Tim Taylor - Idea creator, Software Eng.\n\n" +
            "Nate Stewart - Lead Software Engineer\n\nJoao Magalhaes - Lead Software Eng.";

    private OnFragmentInteractionListener mListener;

    // Views
    private TextView termsOfService;
    private TextView randomizedTip;

    private Button creditsButton;

    public AboutFragment() {}

    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_about, container, false);

        initializeViews(view);

        return view;
    }

    public void initializeViews(View view) {
        termsOfService = (TextView) view.findViewById(R.id.about_terms_of_service_textview);
        termsOfService.setOnClickListener(this);

        creditsButton = (Button) view.findViewById(R.id.about_credits_button);
        creditsButton.setOnClickListener(this);

        randomizedTip = (TextView) view.findViewById(R.id.about_random_tip_textview);
        randomizedTip.setText(getRandomTip());
    }

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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.about_terms_of_service_textview :
                Uri uri = Uri.parse(getContext().getString(R.string.terms_of_service));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), CREDITS_MESSAGE, Toast.LENGTH_LONG).show();
                if (!FirebaseHelper.getInstance().hasBadge(getContext().getString(R.string.badge_hidden))) {
                    FirebaseHelper.getInstance().unlockBadge(getContext().getString(R.string.badge_hidden));
                    initiatePopupWindow();
                }

                //FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard();

                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    protected PopupWindow pw;
    protected void initiatePopupWindow() {
        if (CurrentUser.getInstance().getBadgeNotificationsEnabled()) {
            try {
                final String bName = getString(R.string.badge_hidden);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup, (ViewGroup) getActivity().findViewById(R.id.popup_element));
                        // create a 300px width and 470px height PopupWindow
                        pw = new PopupWindow(layout, 800, 800, true);

                        TextView badgeTitle = (TextView) layout.findViewById(R.id.popup_BadgeTitleTextView);
                        TextView badgeDescription = (TextView) layout.findViewById(R.id.popup_BadgeDescriptionTextView);

                        badgeTitle.setText(Html.fromHtml("<i>" + bName + "</i>"));
                        badgeDescription.setText("\n" + Badge.badgeNameToDescriptionMap.get(bName));

                        Button cancelButton = (Button) layout.findViewById(R.id.popup_cancel_button);
                        cancelButton.setOnClickListener(cancel_button_click_listener);

                        // display the popup in the center
                        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    private String getRandomTip() {
        String tip = "";
        Resources res = getResources();
        String[] randomTips = res.getStringArray(R.array.about_random_tips);

        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int val = r.nextInt(randomTips.length);

        tip = randomTips[val];

        return tip;
    }
}