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
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    private Button saveButton;
    private CheckBox soundEnabledCheckbox;
    private CheckBox tutorialMessagesEnabledCheckbox;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    public SettingsFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
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
        tutorialMessagesEnabledCheckbox = (CheckBox) view.findViewById(R.id.settings_tutorial_messages_checkbox);
        tutorialMessagesEnabledCheckbox.setOnClickListener(this);
        saveButton = (Button) view.findViewById(R.id.settings_save_button);
        saveButton.setOnClickListener(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.settings_save_button:
                saveSettings();
                Toast.makeText(getActivity().getApplicationContext(), "Saved successfully!", Toast.LENGTH_LONG).show();
            default:
                break;
        }
    }

    private void saveSettings(){
        CurrentUser.getInstance().updateSoundEnabled(soundEnabledCheckbox.isChecked());
        CurrentUser.getInstance().updateTutorialMessagesEnabled(tutorialMessagesEnabledCheckbox.isChecked());
    }

    private void loadSettings(){
        soundEnabledCheckbox.setChecked(CurrentUser.getInstance().getSoundEnabled());
        tutorialMessagesEnabledCheckbox.setChecked(CurrentUser.getInstance().getTutorialMessagesEnabled());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
}