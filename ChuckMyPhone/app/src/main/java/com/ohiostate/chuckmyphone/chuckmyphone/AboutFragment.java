package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.res.Resources;
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

import com.ohiostate.chuckmyphone.chuckmyphone.util.IabHelper;
import com.ohiostate.chuckmyphone.chuckmyphone.util.IabResult;
import com.ohiostate.chuckmyphone.chuckmyphone.util.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private final String CREDITS_MESSAGE = "Thank you so much for playing our game!\n\n Tim Taylor - Idea creator, Software Eng.\n\n" +
            "Nate Stewart - Lead Software Engineer\n\nJoao Magalhaes - Lead Software Eng.";

    private OnFragmentInteractionListener mListener;

    private boolean inAppBillingReady;

    // Views
    private TextView termsOfService;
    private Button creditsButton;
    private Button donateTier1Button;
    private Button donateTier2Button;
    private Button donateTier3Button;
    private Button donateTier4Button;


    IabHelper mHelper;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");

        //TODO remove this later
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("1", "Bob", 600);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("2", "Jill", 700);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("3", "King", 770);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("4", "DudeMan", 800);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("5", "BadMan88", 850);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("6", "WhoDey", 870);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("7", "CheeseFace", 890);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("8", "Number1Dad", 910);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("9", "SoccerMom", 920);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("10", "CrimsonShin", 1000);
        FirebaseHelper.getInstance().addFakeChuckScoresToLeaderboard("11", "WithFries", 650);

        initializeInAppBilling();
    }

    //prepare for any possible user instigated purchases made
    public void initializeInAppBilling() {
        inAppBillingReady = false;

        //TODO for the love of god obfuscate this before publishing or pushing on github
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA03QJhg9aRQQn9rzygBAiCqSGgRVe6PFG0XjxAcEik0EsbJkNANgAQwczEwdBkjOUXQMDvyHc0gZxPLRyIbXmFqMdrNhSQDWUighgWDbLAwK0h62hm0IDmZNBWM2XuSg5vutfeXzlIi7f+nhTWxUMtk4U881ThDSdZ7/q8YPmEjQF/hQZEoXq1qbod2V5tF1nXth1p4qQvew2bHUmyjvHFoMatk/N2tMZzuHqVJ8EoKqR+ip8vCdUnQqwyHw3kJq+V/37v2lvXnvIC5u5N/1QT+2IoKsQOAhhUCdxO10Ng6M/Rw/7DFYwB7URqHdnspJHGoywVZpQAS+y5oN8/sdoAQIDAQAB";
        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                // IAB is fully set up!
                inAppBillingReady = true;

                List additionalSkuList = new ArrayList();
                additionalSkuList.add("tier_one_donation");
                additionalSkuList.add("tier_two_donation");
                additionalSkuList.add("tier_three_donation");
                additionalSkuList.add("tier_four_donation");

                //get prices asynchonously
                try {
                    mHelper.queryInventoryAsync(true, additionalSkuList, null, mQueryFinishedListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_about, container, false);

        initializeViews(view);

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
                if (inAppBillingReady) {

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_2:
                if (inAppBillingReady) {
                    //check if user already purchased this item
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_3:
                if (inAppBillingReady) {

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.about_donate_tier_4:
                if (inAppBillingReady) {

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), CREDITS_MESSAGE, Toast.LENGTH_LONG).show();
                if (!FirebaseHelper.getInstance().hasBadge(getContext().getString(R.string.badge_hidden))) {
                    FirebaseHelper.getInstance().unlockBadge(getContext().getString(R.string.badge_hidden));
                    MiscHelperMethods.initiatePopupWindow(getString(R.string.badge_hidden), this);
                }

                FirebaseHelper.getInstance().updateStarStatusOfUser(LeaderboardsFragment.Star_icon_names.gold);

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
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }

    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure()) {
                // handle error
                return;
            }

            //String tierOnePrice = inventory.getSkuDetails("tier_one_donation").getPrice();
            //String tierTwoPrice = inventory.getSkuDetails("tier_two_donation").getPrice();
            //String tierThreePrice = inventory.getSkuDetails("tier_three_donation").getPrice();
            //String tierFourPrice = inventory.getSkuDetails("tier_four_donation").getPrice();

            // update the UI
            String bob = "5";
        }
    };
}