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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button saveButton;
    private RadioButton metricSystemButton;
    private RadioButton imperialSystemButton;
    private CheckBox soundEnabledCheckbox;
    private CheckBox backgroundNotificationsCheckbox;

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
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mSharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_settings, container, false);

        saveButton = (Button) view.findViewById(R.id.settings_save_button);
        saveButton.setOnClickListener(this);

        metricSystemButton = (RadioButton) view.findViewById(R.id.settings_metric_system_button);
        metricSystemButton.setOnClickListener(this);
        imperialSystemButton = (RadioButton) view.findViewById(R.id.settings_imperial_system_button);
        imperialSystemButton.setOnClickListener(this);

        backgroundNotificationsCheckbox = (CheckBox) view.findViewById(R.id.settings_background_notification_checkbox);
        backgroundNotificationsCheckbox.setOnClickListener(this);
        soundEnabledCheckbox = (CheckBox) view.findViewById(R.id.settings_sound_enabled_checkbox);
        soundEnabledCheckbox.setOnClickListener(this);

        loadSettings();
        
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
            case R.id.settings_background_notification_checkbox:

                break;
            case R.id.settings_sound_enabled_checkbox:

                break;
            case R.id.settings_imperial_system_button:
                if(metricSystemButton.isChecked()){
                    metricSystemButton.setChecked(false);
                    imperialSystemButton.setChecked(true);
                }
                break;
            case R.id.settings_metric_system_button:
                if(imperialSystemButton.isChecked()){
                    metricSystemButton.setChecked(true);
                    imperialSystemButton.setChecked(false);
                }
                break;
            default:
                saveSettings();
                break;
        }

    }

    private void saveSettings(){
        mSharedPreferencesHelper.setNotificationsEnabled(backgroundNotificationsCheckbox.isChecked());
        mSharedPreferencesHelper.setImperialSystem(imperialSystemButton.isChecked());
        mSharedPreferencesHelper.setSoundEnabled(soundEnabledCheckbox.isChecked());
    }

    private void loadSettings(){
        backgroundNotificationsCheckbox.setChecked(mSharedPreferencesHelper.getNotificationsEnabled());
        soundEnabledCheckbox.setChecked(mSharedPreferencesHelper.getSoundEnabled());
        imperialSystemButton.setChecked(mSharedPreferencesHelper.getImperialSystem());
        metricSystemButton.setChecked(!mSharedPreferencesHelper.getImperialSystem());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
