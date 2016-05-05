package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

final class MiscHelperMethods {
    private MiscHelperMethods () { }

    public static boolean userNavigatedAway;

    public static boolean isNetworkAvailable(Activity a) {
        ConnectivityManager connectivityManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //private static PopupWindow pw;
    static void initiatePopupWindow(String badgeName, Fragment fragment) {
        if (CurrentUser.getInstance().getBadgeNotificationsEnabled()) {
            BadgeUnlockPopUpFragment popUpFragment = new BadgeUnlockPopUpFragment();
            popUpFragment.setBadgeName(badgeName);
            popUpFragment.show(fragment.getActivity().getFragmentManager(), "PopUp");
        }
    }

    public static void setUserNavigatedAway(Boolean b) {
        userNavigatedAway = b;
    }

    public static void initializeUserNavigationTracking() {
        userNavigatedAway = false;
    }
}