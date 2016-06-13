package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    //used to hide the keyboard whenever user clicks on something that should hide it, but android is too dumb to do so >:(
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void setupUI(View view, final Activity activity) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    MiscHelperMethods.hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView, activity);
            }
        }
    }

    static double getDoubleValue(Object value){
        double doubleValue = -1; // whatever to state invalid!

        if(value instanceof Long) {
            doubleValue = ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            doubleValue = (double) value;
        }

        return doubleValue;
    }
}