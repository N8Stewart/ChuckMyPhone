package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;


public class UnusualUnlockPopUpFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        builder.setTitle("Unusual Unlocked!");
        builder.setMessage("Congratulations! You unlocked an unusual icon on the leaderboard! Go check it out!");

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
