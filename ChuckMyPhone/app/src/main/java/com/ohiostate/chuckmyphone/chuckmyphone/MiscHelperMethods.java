package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Tim on 4/18/2016.
 */
final class MiscHelperMethods {
    private MiscHelperMethods () { // private constructor
    }

    protected int NUM_SECONDS_BADGE_POPUP_DISMISS = 6;
    private static boolean userNavigatedAway;

    public static boolean isNetworkAvailable(Activity a) {
        ConnectivityManager connectivityManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
