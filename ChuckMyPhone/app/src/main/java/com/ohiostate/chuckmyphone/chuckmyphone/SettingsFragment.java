package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    // Views
    private Button saveButton;

    private CheckBox soundEnabledCheckbox;
    private CheckBox goofySoundEnabledCheckbox;
    private CheckBox tutorialMessagesEnabledCheckbox;
    private CheckBox badgeNotificationsCheckbox;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_settings, container, false);
        initializeViews(view);
        loadSettings();
        return view;
    }

    private void initializeViews(View view) {
        soundEnabledCheckbox = (CheckBox) view.findViewById(R.id.settings_sound_enabled_checkbox);
        soundEnabledCheckbox.setOnClickListener(this);

        badgeNotificationsCheckbox = (CheckBox) view.findViewById(R.id.settings_badge_unlock_notifications_checkbox);
        badgeNotificationsCheckbox.setOnClickListener(this);

        tutorialMessagesEnabledCheckbox = (CheckBox) view.findViewById(R.id.settings_tutorial_messages_checkbox);
        tutorialMessagesEnabledCheckbox.setOnClickListener(this);

        goofySoundEnabledCheckbox = (CheckBox) view.findViewById(R.id.settings_goofy_sounds_checkbox);
        goofySoundEnabledCheckbox.setOnClickListener(this);

        saveButton = (Button) view.findViewById(R.id.settings_save_button);
        saveButton.setOnClickListener(this);

        if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_hidden))) {
            goofySoundEnabledCheckbox.setVisibility(View.INVISIBLE);
        }
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
            case R.id.settings_save_button:
                saveSettings();
                Toast.makeText(getActivity().getApplicationContext(), "Saved successfully!", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            default:
                break;
        }
    }

    private void loadSettings(){
        // method to load settings values into views
        soundEnabledCheckbox.setChecked(CurrentUser.getInstance().getSoundEnabled());
        tutorialMessagesEnabledCheckbox.setChecked(CurrentUser.getInstance().getTutorialMessagesEnabled());
        badgeNotificationsCheckbox.setChecked(CurrentUser.getInstance().getBadgeNotificationsEnabled());
        goofySoundEnabledCheckbox.setChecked(CurrentUser.getInstance().getGoofySoundEnabled());
    }

    private void saveSettings(){
        // method to save settings values in the user class and in the mobile phone
        CurrentUser.getInstance().updateSoundEnabled(soundEnabledCheckbox.isChecked());
        CurrentUser.getInstance().updateTutorialMessagesEnabled(tutorialMessagesEnabledCheckbox.isChecked());
        CurrentUser.getInstance().updateBadgeNotificationsEnabled(badgeNotificationsCheckbox.isChecked());
        CurrentUser.getInstance().updateGoofySoundEnabled(goofySoundEnabledCheckbox.isChecked());

        SharedPreferencesHelper.setSoundEnabled(getActivity().getApplicationContext(), soundEnabledCheckbox.isChecked());
        SharedPreferencesHelper.setTutorialMessages(getActivity().getApplicationContext(), tutorialMessagesEnabledCheckbox.isChecked());
        SharedPreferencesHelper.setBadgeNotificationsEnabled(getActivity().getApplicationContext(), badgeNotificationsCheckbox.isChecked());
        SharedPreferencesHelper.setGoofySoundEnabled(getActivity().getApplicationContext(), goofySoundEnabledCheckbox.isChecked());
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
}