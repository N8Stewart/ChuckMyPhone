package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;


public class TermsOfServicePopUpFragment extends DialogFragment {
    private String badgeName;

    public void setBadgeName(String bName) {
        badgeName = bName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        builder.setTitle("Terms of Service");
        builder.setMessage(
                "By accessing and using this application, you accept and agree to be bound by the terms and provision of this agreement. In addition, when using this applications particular services, you shall be subject to any posted guidelines or rules applicable to such services, which may be posted and modified from time to time. All such guidelines or rules are hereby incorporated by reference into the TOS.\n\n" +
                "ANY PARTICIPATION IN THIS APPLICATION WILL CONSTITUTE ACCEPTANCE OF THIS AGREEMENT. IF YOU DO NOT AGREE TO ABIDE BY THE ABOVE, PLEASE DO NOT USE THIS APPLICATION.\n\n" +
                "This application and its components are offered for entertainment purposes only; this application shall not be responsible or liable for any damages such as, but not limited to, cracked screens, destroyed mobile devices, physical harm to any objects hit while throwing the mobile device, emotional instability or hurt pride. This application and its components shall not be responsible or liable for the accuracy, usefullness or availability of any information transmitted or made available via the application, and shall not be responsible or liable for any error or omissions in that information.\n\n" +
                "If you feel a third-party or owner of this copyright has infringed on the intellectual property rights of this application, you can contact us at ChuckMyPhoneTeam@gmail.com.\n\n" +
                "Under no circumstances will advertisements be displayed on this application. The creators of this application are not getting paid to provide endorsement whatsoever.\n\n" +
                "No sensitive information is being held or processed by this application. We highly recommend against reusing a common password with use of this site.\n\n" +
                "Other user's usernames may contain lewd content. By using this application, you accept that seeing this lewd content is possible");

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
