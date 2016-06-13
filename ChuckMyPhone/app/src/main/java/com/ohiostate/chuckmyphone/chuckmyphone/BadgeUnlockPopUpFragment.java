package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;


public class BadgeUnlockPopUpFragment extends DialogFragment {

    private String badgeName;

    public void setBadgeName(String bName) {
        badgeName = bName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Badge.badgeNameToDescriptionMap.get(badgeName))
                .setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        builder.setTitle(Html.fromHtml("Unlocked: <b><i>" + badgeName + "</i></b>"));

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
