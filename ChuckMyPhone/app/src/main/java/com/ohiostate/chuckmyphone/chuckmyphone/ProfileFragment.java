package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private TextView bestChuckScoreTextView;
    private TextView bestDropScoreTextView;
    private TextView bestSpinScoreTextView;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.content_profile, container, false);

        ArrayList<Badge> badgeList = CurrentUser.getInstance().getBadgeList();
        for (Badge badge : badgeList) {
            if (badge.unlocked()) {
                addBadge(view, badge);
            }
        }
        for (Badge badge : badgeList) {
            if (!badge.unlocked()) {
                addBadge(view, badge);
            }
        }
        initializeViews(view);

        return view;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void addBadge(View view, final Badge badge) {
        boolean isUnlocked = badge.unlocked();

        LinearLayout horzLayout = (LinearLayout) view.findViewById(R.id.profile_trophies_linear_layout);

        LinearLayout podLayout = new LinearLayout(getActivity());
        podLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView badgeImageView = new ImageView(getActivity());
        if (isUnlocked) {
            badgeImageView.setImageResource(Badge.badgeNameToDrawableMap.get(badge.getName()));
        } else {
            badgeImageView.setImageResource(R.drawable.badge_locked);
        }

        badgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (badge.unlocked()) {
                    Toast.makeText(getActivity().getApplicationContext(), badge.UnlockedDescription() + "\n\n" +"Date Earned: " + badge.getUnlockDate(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), badge.LockedDescription(getContext()), Toast.LENGTH_LONG).show();
                }
            }
        });
        TextView badgeNameTextView = new TextView(getActivity());
        badgeNameTextView.setGravity(Gravity.CENTER);
        badgeNameTextView.setText(badge.getName());

        podLayout.addView(badgeImageView);
        podLayout.addView(badgeNameTextView);

        horzLayout.addView(podLayout);

        View dividerView = new View(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(50, ViewGroup.LayoutParams.MATCH_PARENT);
        dividerView.setLayoutParams(lp);

        horzLayout.addView(dividerView);
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

    private void initializeViews(View view) {
        bestChuckScoreTextView = (TextView) view.findViewById(R.id.profile_fastest_thrown_record_textview);
        bestDropScoreTextView = (TextView) view.findViewById(R.id.profile_furthest_drop_record_textview);
        bestSpinScoreTextView = (TextView) view.findViewById(R.id.profile_most_spins_record_textview);

        bestChuckScoreTextView.setText(""+FirebaseHelper.getInstance().getBestChuckScore());
        bestDropScoreTextView.setText(""+FirebaseHelper.getInstance().getBestDropScore());
        bestSpinScoreTextView.setText(""+FirebaseHelper.getInstance().getBestSpinScore());
    }
}
