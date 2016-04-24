package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private MiscHelperMethods () { // private constructor
    }

    private static boolean userNavigatedAway;

    public static boolean isNetworkAvailable(Activity a) {
        ConnectivityManager connectivityManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static PopupWindow pw;
    static void initiatePopupWindow(String badgeName, Fragment fragment) {
        if (CurrentUser.getInstance().getBadgeNotificationsEnabled()) {
            try {
                final String bName = badgeName;
                final Fragment f = fragment;
                final Activity a = fragment.getActivity();
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) f.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup, (ViewGroup) a.findViewById(R.id.popup_element));
                        pw = new PopupWindow(layout, 1000, 1000, true);
                        pw.setOutsideTouchable(true);

                        TextView badgeTitle = (TextView) layout.findViewById(R.id.popup_BadgeTitleTextView);
                        TextView badgeDescription = (TextView) layout.findViewById(R.id.popup_BadgeDescriptionTextView);

                        badgeTitle.setText(Html.fromHtml("<i>" + bName + "</i>"));
                        badgeDescription.setText("\n" + Badge.badgeNameToDescriptionMap.get(bName));

                        Button cancelButton = (Button) layout.findViewById(R.id.popup_cancel_button);
                        cancelButton.setOnClickListener(cancel_button_click_listener);

                        // display the popup in the center if user didn't navigate away quickly via hamburger menu
                        if (!userNavigatedAway) {
                            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                        }

                        userNavigatedAway = false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final static View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Log.d("", "Pop up window dismissed");
        }
    };

    public static void setUserNavigatedAway(Boolean b) {
        userNavigatedAway = b;
    }

    public static void initializeUserNavigationTracking() {
        userNavigatedAway = false;
    }
}