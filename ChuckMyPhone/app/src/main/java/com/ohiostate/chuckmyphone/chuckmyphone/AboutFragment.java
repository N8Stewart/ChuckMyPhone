package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private final String CREDITS_MESSAGE = "Thank you so much for playing our game!\n\n Tim Taylor - Idea Creator, Software Eng.\n\nNate Stewart - Lead Software Engineer\n\nJoao - Lead Software Engineer";


    private OnFragmentInteractionListener mListener;

    // Views
    private TextView termsOfService;
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

        termsOfService.setOnClickListener(this);

        return view;
    }

    public void initializeViews(View view) {
        termsOfService = (TextView) view.findViewById(R.id.about_terms_of_service_textview);
        creditsButton = (Button) view.findViewById(R.id.about_credits_button);
        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), CREDITS_MESSAGE, Toast.LENGTH_LONG).show();
                FirebaseHelper.getInstance().unlockBadge("The Kindness Badge");
            }
        });
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
        // open webview or dialog to terms of service
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
}