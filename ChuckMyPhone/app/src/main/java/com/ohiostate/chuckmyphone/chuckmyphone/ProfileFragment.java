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

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private TextView bestChuckScoreTextView;
    private TextView bestDropScoreTextView;
    private TextView bestSpinScoreTextView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_profile, container, false);

        ArrayList<Badge> badgeList = CurrentUser.getInstance().getBadgeList();
        for (Badge badge : badgeList) {
            if (badge.isUnlocked()) {
                addBadge(view, badge);
            }
        }
        for (Badge badge : badgeList) {
            if (!badge.isUnlocked()) {
                addBadge(view, badge);
            }
        }
        initializeViews(view);

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

    public void addBadge(View view, final Badge badge) {
        boolean isUnlocked = badge.isUnlocked();

        LinearLayout horzLayout = (LinearLayout) view.findViewById(R.id.profile_trophies_linear_layout);

        LinearLayout podLayout = new LinearLayout(getActivity());
        podLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView badgeImageView = new ImageView(getActivity());
        if (isUnlocked) {
            badgeImageView.setImageResource(R.drawable.badge_unlocked);
        } else {
            badgeImageView.setImageResource(R.drawable.badge_locked);
        }

        badgeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (badge.isUnlocked()) {
                    Toast.makeText(getActivity().getApplicationContext(), badge.UnlockedDescription() + "\n\n" +"Date Earned: " + badge.getUnlockDate(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), badge.LockedDescription(), Toast.LENGTH_LONG).show();
                }
            }
        });
        TextView badgeNameTextView = new TextView(getActivity());
        badgeNameTextView.setGravity(Gravity.CENTER);
        badgeNameTextView.setText(badge.getName());

        podLayout.addView(badgeImageView);
        podLayout.addView(badgeNameTextView);

        horzLayout.addView(podLayout);
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
