package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
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

public class AboutFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private final String CREDITS_MESSAGE = "Thank you so much for playing our game!\n\n Tim Taylor - Idea creator, Software Eng.\n\n" +
            "Nate Stewart - Lead Software Engineer\n\nJoao Magalhaes - Lead Software Eng.";

    private OnFragmentInteractionListener mListener;

    private IABHelper iabHelper;

    // Views
    private View view;
    private TextView termsOfService;
    private Button creditsButton;
    private Button donateTier1Button;
    private Button donateTier2Button;
    private Button donateTier3Button;
    private Button donateTier4Button;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_about, container, false);

        initializeViews(view);

        iabHelper = new IABHelper(view);

        return view;
    }

    private void initializeViews(View view) {
        termsOfService = (TextView) view.findViewById(R.id.about_terms_of_service_textview);
        termsOfService.setOnClickListener(this);

        creditsButton = (Button) view.findViewById(R.id.about_credits_button);
        creditsButton.setOnClickListener(this);

        donateTier1Button = (Button) view.findViewById(R.id.about_donate_tier_1);
        donateTier2Button = (Button) view.findViewById(R.id.about_donate_tier_2);
        donateTier3Button = (Button) view.findViewById(R.id.about_donate_tier_3);
        donateTier4Button = (Button) view.findViewById(R.id.about_donate_tier_4);

        donateTier1Button.setOnClickListener(this);
        donateTier2Button.setOnClickListener(this);
        donateTier3Button.setOnClickListener(this);
        donateTier4Button.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (iabHelper.isLoaded()) {
            iabHelper.consumeAllKnownPurchases();
        }
        Log.d(TAG, "onResume() called");
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.about_terms_of_service_textview :
                Uri uri = Uri.parse(getContext().getString(R.string.terms_of_service));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.about_donate_tier_1:
                if (iabHelper.isLoaded()) {
                    iabHelper.makePurchase("tier_one_donation", getActivity());
                } else {
                    Toast.makeText(view.getContext(), "Google play store is still loading, please wait...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_2:
                if (iabHelper.isLoaded()) {
                    iabHelper.makePurchase("tier_two_donation", getActivity());
                } else {
                    Toast.makeText(view.getContext(), "Google play store is still loading, please wait...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_3:
                if (iabHelper.isLoaded()) {
                    iabHelper.makePurchase("tier_three_donation", getActivity());
                } else {
                    Toast.makeText(view.getContext(), "Google play store is still loading, please wait...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_4:
                if (iabHelper.isLoaded()) {
                    iabHelper.makePurchase("tier_four_donation", getActivity());
                } else {
                    Toast.makeText(view.getContext(), "Google play store is still loading, please wait...", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                if (!FirebaseHelper.getInstance().hasBadge(getContext().getString(R.string.badge_hidden))) {
                    FirebaseHelper.getInstance().unlockBadge(getContext().getString(R.string.badge_hidden));
                    MiscHelperMethods.initiatePopupWindow(getString(R.string.badge_hidden), this);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), CREDITS_MESSAGE, Toast.LENGTH_LONG).show();
                }

                //TODO remove this later
                //iabHelper.consumeAllPurchasesAdmin();
                break;
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
    public void onDestroy() {
        super.onDestroy();
        iabHelper.onDestroy();
    }

    //set asynchronously by IABHelper when it is ready with the prices
    public void setPricesTexts(View view, String tierOnePrice, String tierTwoPrice, String tierThreePrice, String tierFourPrice) {
        // update the UI
        TextView priceView = (TextView) view.findViewById(R.id.about_tier_1_price_text_view);
        priceView.setText(tierOnePrice);

        priceView = (TextView) view.findViewById(R.id.about_tier_2_price_text_view);
        priceView.setText(tierTwoPrice);

        priceView = (TextView) view.findViewById(R.id.about_tier_3_price_text_view);
        priceView.setText(tierThreePrice);

        priceView = (TextView) view.findViewById(R.id.about_tier_4_price_text_view);
        priceView.setText(tierFourPrice);
    }

    public static void handlePurchaseFinished(String sku, String token) {
        IABHelper.onSuccessfulPurchase(sku, token);
    }

}