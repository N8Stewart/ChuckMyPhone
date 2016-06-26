package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class TipsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    //Views
    private TextView randomizedTip;

    public TipsFragment() {
        // Required empty public constructor
    }

    public static TipsFragment newInstance() {
        return new TipsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_tips, container, false);
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

    private void initializeViews(View view) {
        randomizedTip = (TextView) view.findViewById(R.id.tips_random_tip_textview);
        randomizedTip.setText(getRandomTip());
    }

    private String getRandomTip() {
        Resources res = getResources();
        String[] randomTips = res.getStringArray(R.array.about_random_tips);

        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int val = r.nextInt(randomTips.length);

        String tip = randomTips[val];

        return tip;
    }
}
