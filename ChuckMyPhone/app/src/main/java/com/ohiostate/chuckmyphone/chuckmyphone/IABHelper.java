package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class IABHelper extends Binder {
    private ArrayList skuList;
    private static ArrayList<String> tokenList;

    private static View view;

    static String base64EncodedPublicKey;
    private Bundle ownedItems;

    static String tierOnePrice = "";
    static String tierTwoPrice = "";
    static String tierThreePrice = "";
    static String tierFourPrice = "";

    public IABHelper(View v) {
        tokenList = new ArrayList<String>();
        view = v;

        //store public key elsewhere so it may be gotten at runtime only. Don't want it in source code for security reasons
        base64EncodedPublicKey = FirebaseHelper.getInstance().getPublicKey();

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        view.getContext().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void getInventory() {
        skuList = new ArrayList();
        skuList.add("tier_one_donation");
        skuList.add("tier_two_donation");
        skuList.add("tier_three_donation");
        skuList.add("tier_four_donation");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        Bundle skuDetails = null;
        try {
            skuDetails = mService.getSkuDetails(3, view.getContext().getPackageName(), "inapp", querySkus);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "There was a problem establishing connection with Google Play Billing, please try again later: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (skuDetails != null) {
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = null;
                    String sku = "NULL";
                    String price = "NULL";
                    try {
                        object = new JSONObject(thisResponse);
                        sku = object.getString("productId");
                        price = object.getString("price");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(), "There was a problem getting sku details from a response: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    if (sku.equals("tier_one_donation")) {
                        tierOnePrice = price;
                    } else if (sku.equals("tier_two_donation")) {
                        tierTwoPrice = price;
                    } else if (sku.equals("tier_three_donation")) {
                        tierThreePrice = price;
                    } else if (sku.equals("tier_four_donation")) {
                        tierFourPrice = price;
                    } else {
                        Toast.makeText(view.getContext(), "Unknown sku encountered", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.setPricesTexts(view, tierOnePrice, tierTwoPrice, tierThreePrice, tierFourPrice);
    }

    public void makePurchase(String sku, Activity activity) {
        Bundle buyIntentBundle = null;
        try {
            //TODO need a dev payload? maybe do the public key?
            String developerPayload = base64EncodedPublicKey + CurrentUser.getInstance().getUserId();
             buyIntentBundle = mService.getBuyIntent(3, view.getContext().getPackageName(), sku, "inapp", developerPayload);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Error making purchase: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (buyIntentBundle != null) {
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            try {
                int requestCode = 1001;
                activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Error getting purchase result: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void onSuccessfulPurchase(String sku, String token) {
        String username = CurrentUser.getInstance().getUsername();
        String highestStarStatus = FirebaseHelper.getInstance().getHighestStarStatusOfUser(username);

        //update database to reflect what user bought, giving them a new star status
        if (sku.equals("tier_one_donation")) {
            //if this is a new highest single donation, update star status of user
            if (highestStarStatus.equals("none")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser("bronze");
                FirebaseHelper.getInstance().updateHighestStarEarnedOfUser("bronze");
            }
            Toast.makeText(view.getContext(), "You successfully donated " + tierOnePrice +"!\nThank you so much for your donation!", Toast.LENGTH_LONG).show();
        } else if (sku.equals("tier_two_donation")) {
            if (highestStarStatus.equals("none") || highestStarStatus.equals("bronze")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser("silver");
                FirebaseHelper.getInstance().updateHighestStarEarnedOfUser("silver");
            }
            Toast.makeText(view.getContext(), "You successfully donated " + tierTwoPrice + "!\nThank you so much for your donation!", Toast.LENGTH_LONG).show();
        } else if (sku.equals("tier_three_donation")) {
            if (highestStarStatus.equals("none") || highestStarStatus.equals("bronze") || highestStarStatus.equals("silver")) {
                FirebaseHelper.getInstance().updateStarStatusOfUser("gold");
                FirebaseHelper.getInstance().updateHighestStarEarnedOfUser("gold");
            }
            Toast.makeText(view.getContext(), "You successfully donated " + tierThreePrice +"!\nThank you so much for your donation!", Toast.LENGTH_LONG).show();
        } else if (sku.equals("tier_four_donation")) {
            FirebaseHelper.getInstance().updateStarStatusOfUser("shooting");
            FirebaseHelper.getInstance().updateHighestStarEarnedOfUser("shooting");
            Toast.makeText(view.getContext(), "You successfully donated " + tierFourPrice +"!\nThank you so much for your donation!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(view.getContext(), "No error occurred during purchase, but unknown SKU was provided:" + sku, Toast.LENGTH_LONG).show();
        }

        //add purchase token to list of tokens to consume later
        tokenList.add(token);
    }

    public void checkPurchases() {
        ownedItems = null;
        try {
           ownedItems = mService.getPurchases(3, view.getContext().getPackageName(), "inapp", null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Error getting purchases: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (ownedItems != null) {
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }
        }
    }

    public void consumeAllKnownPurchases() {
        for (String token : tokenList) {
            try {
                int response = mService.consumePurchase(3, view.getContext().getPackageName(), token);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Error consuming purchase\ntoken:" + token + "\nerror: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void consumeAllPurchasesAdmin() {
        try {
            ownedItems = mService.getPurchases(3, view.getContext().getPackageName(), "inapp", null);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Error getting inventory: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            //ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
            //String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
            for (int i = 0; i < purchaseDataList.size(); ++i) {
                try {
                    String purchaseData = purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    final String token = jo.getString("purchaseToken");
                    String sku = null;
                    if (ownedSkus != null) {
                        sku = ownedSkus.get(i);
                    }
                    try {
                        mService.consumePurchase(3, view.getContext().getPackageName(), token);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(), "Error consuming purchase\ntoken:" + token + "\nerror: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(view.getContext(), "Error getting inventory. Response code: " + response, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isLoaded() {
        return !tierFourPrice.equals("NULL");
    }

    public void onDestroy() {
        if (mService != null) {
            view.getContext().unbindService(mServiceConn);
        }
    }

    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            getInventory();
        }
    };
}
