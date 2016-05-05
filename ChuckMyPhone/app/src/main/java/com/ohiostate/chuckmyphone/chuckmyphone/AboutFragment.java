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

import com.ohiostate.chuckmyphone.chuckmyphone.util.IabHelper;
import com.ohiostate.chuckmyphone.chuckmyphone.util.IabResult;
import com.ohiostate.chuckmyphone.chuckmyphone.util.Inventory;
import com.ohiostate.chuckmyphone.chuckmyphone.util.Purchase;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private final String CREDITS_MESSAGE = "Thank you so much for playing our game!\n\n Tim Taylor - Idea creator, Software Eng.\n\n" +
            "Nate Stewart - Lead Software Engineer\n\nJoao Magalhaes - Lead Software Eng.";

    private OnFragmentInteractionListener mListener;

    private boolean inAppBillingReady;
    private boolean inventoryLoaded;
    private Inventory userInventory;

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

        initializeInAppBilling();
    }

    //prepare for any possible user instigated purchases made
    public void initializeInAppBilling() {
        inAppBillingReady = false;
        inventoryLoaded = false;

        //store public key elsewhere so it may be gotten at runtime only. Don't want it in source code for security reasons
        String base64EncodedPublicKey = FirebaseHelper.getInstance().getPublicKey();
        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    // IAB is fully set up!
                    inAppBillingReady = true;
                }
                List additionalSkuList = new ArrayList();
                additionalSkuList.add("tier_one_donation");
                additionalSkuList.add("tier_two_donation");
                additionalSkuList.add("tier_three_donation");
                additionalSkuList.add("tier_four_donation");

                //get prices asynchonously
                try {
                    mHelper.queryInventoryAsync(true, additionalSkuList, null, mQueryGetInventoryListener);
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
                attemptToPlaceOrder("tier_one_donation");
                break;
            case R.id.about_donate_tier_2:
                attemptToPlaceOrder("tier_two_donation");
                break;
            case R.id.about_donate_tier_3:
                attemptToPlaceOrder("tier_three_donation");
                break;
            case R.id.about_donate_tier_4:
                attemptToPlaceOrder("tier_four_donation");
                break;
            default:
                Toast.makeText(getActivity().getApplicationContext(), CREDITS_MESSAGE, Toast.LENGTH_LONG).show();
                if (!FirebaseHelper.getInstance().hasBadge(getContext().getString(R.string.badge_hidden))) {
                    FirebaseHelper.getInstance().unlockBadge(getContext().getString(R.string.badge_hidden));
                    MiscHelperMethods.initiatePopupWindow(getString(R.string.badge_hidden), this);
                }

                break;
        }
    }

    public void attemptToPlaceOrder(String orderSku) {
        if (inAppBillingReady) {
            if (inventoryLoaded) {
                if (!userInventory.hasPurchase(orderSku)) {
                    placeOrder(orderSku);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You already bought this tier", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Fetching your current inventory of purchases, please wait", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
        }
    }

    private void placeOrder(String orderSku) {
        try {
            //need a string to uniquely identify who made order and what order was for record keeping
            String uniqueIdentifier = CurrentUser.getInstance().getUserId() + "_" + orderSku;
            mHelper.launchPurchaseFlow(getActivity(), orderSku, 1, mPurchaseFinishedListener, uniqueIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "There was a problem establishing connection with Google Play Billing, please try again later", Toast.LENGTH_LONG).show();
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

    IabHelper.QueryInventoryFinishedListener mQueryGetInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure()) {
                Toast.makeText(getActivity().getApplicationContext(), "Error fetching info from Google Play Store, please try again later", Toast.LENGTH_LONG).show();
                return;
            }

            String tierOnePrice = inventory.getSkuDetails("tier_one_donation").getPrice();
            String tierTwoPrice = inventory.getSkuDetails("tier_two_donation").getPrice();
            String tierThreePrice = inventory.getSkuDetails("tier_three_donation").getPrice();
            String tierFourPrice = inventory.getSkuDetails("tier_four_donation").getPrice();

            // update the UI
            TextView priceView = (TextView) getView().findViewById(R.id.about_tier_1_price_text_view);
            priceView.setText(tierOnePrice);

            priceView = (TextView) getView().findViewById(R.id.about_tier_2_price_text_view);
            priceView.setText(tierTwoPrice);

            priceView = (TextView) getView().findViewById(R.id.about_tier_3_price_text_view);
            priceView.setText(tierThreePrice);

            priceView = (TextView) getView().findViewById(R.id.about_tier_4_price_text_view);
            priceView.setText(tierFourPrice);

            userInventory = inventory;
            inventoryLoaded = true;
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                Toast.makeText(getActivity().getApplicationContext(), "Error occurred during the purchase, you will not be charged", Toast.LENGTH_LONG).show();
                return;
            } else if (purchase.getSku().equals("tier_one_donation")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser(LeaderboardsFragment.Star_icon_names.bronze);
            } else if (purchase.getSku().equals("tier_two_donation")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser(LeaderboardsFragment.Star_icon_names.silver);
            } else if (purchase.getSku().equals("tier_three_donation")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser(LeaderboardsFragment.Star_icon_names.gold);
            } else if (purchase.getSku().equals("tier_four_donation")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser(LeaderboardsFragment.Star_icon_names.shooting);
            } else {
                //this should not occur ever
                Toast.makeText(getActivity().getApplicationContext(), "You should not see this message. Congrats since you did", Toast.LENGTH_LONG).show();
            }
        }
    };
}