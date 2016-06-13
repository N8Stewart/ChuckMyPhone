package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    private boolean hasUnlockedUsernameChange;

    // Views
    private Button saveButton;
    private Button cancelButton;

    private EditText changeUsernameEditText;

    private CheckBox soundEnabledCheckbox;
    private CheckBox goofySoundEnabledCheckbox;
    private CheckBox tutorialMessagesEnabledCheckbox;
    private CheckBox badgeNotificationsCheckbox;

    private Button noStarIconButton;
    private ImageButton bronzeStarIconButton;
    private ImageButton silverStarIconButton;
    private ImageButton goldStarIconButton;
    private ImageButton shootingStarIconButton;

    private String userStarStatus;

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
        MiscHelperMethods.setupUI(view, getActivity());

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

        cancelButton = (Button) view.findViewById(R.id.settings_cancel_button);
        cancelButton.setOnClickListener(this);

        changeUsernameEditText = (EditText) view.findViewById(R.id.settings_change_username_edit_text);

        hasUnlockedUsernameChange = FirebaseHelper.getInstance().hasUnlockedChangingUsername();
        if (hasUnlockedUsernameChange) {
            changeUsernameEditText.setHint(getString(R.string.settings_change_username_button_unlocked));
            changeUsernameEditText.setEnabled(true);
        } else {
            changeUsernameEditText.setEnabled(false);
        }

        if (!FirebaseHelper.getInstance().hasBadge(getString(R.string.badge_hidden))) {
            ViewGroup viewGroup = (ViewGroup) view;
            viewGroup.removeView(view.findViewById(R.id.settings_goofy_sounds_checkbox));
        }

        noStarIconButton = (Button) view.findViewById(R.id.settings_no_star_image_button);
        noStarIconButton.setOnClickListener(this);

        bronzeStarIconButton = (ImageButton) view.findViewById(R.id.settings_bronze_star_image_button);
        bronzeStarIconButton.setOnClickListener(this);

        silverStarIconButton = (ImageButton) view.findViewById(R.id.settings_silver_star_image_button);
        silverStarIconButton.setOnClickListener(this);

        goldStarIconButton = (ImageButton) view.findViewById(R.id.settings_gold_star_image_button);
        goldStarIconButton.setOnClickListener(this);

        shootingStarIconButton = (ImageButton) view.findViewById(R.id.settings_shooting_star_image_button);
        shootingStarIconButton.setOnClickListener(this);

        highlightCurrentStarStatusIcon();
        highlightLockedIcons();
    }

    private void highlightCurrentStarStatusIcon() {
        //set up screen such that their current star status has its button highlighted
        userStarStatus = FirebaseHelper.getInstance().getStarStatusOfUser(CurrentUser.getInstance().getUsername());

        if (userStarStatus.equals("none")) {
            clearAllFiltersOnButtons();
            noStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        } else if (userStarStatus.equals("bronze")) {
            clearAllFiltersOnButtons();
            bronzeStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        } else if (userStarStatus.equals("silver")) {
            clearAllFiltersOnButtons();
            silverStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        } else if (userStarStatus.equals("gold")) {
            clearAllFiltersOnButtons();
            goldStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        } else if (userStarStatus.equals("shooting")) {
            clearAllFiltersOnButtons();
            shootingStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        }
    }

    private void highlightLockedIcons() {
        //set up screen such that all options that user hasn't unlocked are shaded red to indicate that
        String highestStarStatusLevel = FirebaseHelper.getInstance().getHighestStarStatusOfUser(CurrentUser.getInstance().getUsername());
        switch(highestStarStatusLevel) {
            //no breaks on purpose, utilizing fall through of switch case
            case "none":
                bronzeStarIconButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            case "bronze":
                silverStarIconButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            case "silver":
                goldStarIconButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            case "gold":
                shootingStarIconButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            case "shooting":
                //do nothing, they have unlocked all, so don't lock any
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
        String highestStarStatusLevel = FirebaseHelper.getInstance().getHighestStarStatusOfUser(CurrentUser.getInstance().getUsername());
        switch(v.getId()){
            case R.id.settings_save_button:
                if (!changeUsernameEditText.getText().toString().equals("")) {
                    String proposedUsername = changeUsernameEditText.getText().toString();
                    if (attemptToChangeUsername(proposedUsername)) {
                        saveSettings();
                        getActivity().onBackPressed();
                    }
                } else {
                    saveSettings();
                    getActivity().onBackPressed();
                }
                break;

            case R.id.settings_no_star_image_button:
                //highlight only the no star button
                clearAllFiltersOnButtons();
                highlightLockedIcons();
                noStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                userStarStatus = "none";
                break;

            case R.id.settings_bronze_star_image_button:
                if (!highestStarStatusLevel.equals("none")) {
                    //highlight only the bronze star button
                    clearAllFiltersOnButtons();
                    highlightLockedIcons();
                    bronzeStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    userStarStatus = "bronze";
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have not unlocked that icon. Please donate on the about page to unlock it", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.settings_silver_star_image_button:
                if (!highestStarStatusLevel.equals("none") && !highestStarStatusLevel.equals("bronze")) {
                    //highlight only the silver star button
                    clearAllFiltersOnButtons();
                    highlightLockedIcons();
                    silverStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    userStarStatus = "silver";
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have not unlocked that icon. Please donate on the about page to unlock it", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.settings_gold_star_image_button:
                if (highestStarStatusLevel.equals("gold") || highestStarStatusLevel.equals("shooting")) {
                    //highlight only the gold star button
                    clearAllFiltersOnButtons();
                    highlightLockedIcons();
                    goldStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    userStarStatus = "gold";
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have not unlocked that icon. Please donate on the about page to unlock it", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.settings_shooting_star_image_button:
                if (highestStarStatusLevel.equals("shooting")) {
                    //highlight only the shooting star button
                    clearAllFiltersOnButtons();
                    highlightLockedIcons();
                    shootingStarIconButton.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    userStarStatus = "shooting";
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "You have not unlocked that icon. Please donate on the about page to unlock it", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.settings_cancel_button:
                Toast.makeText(getActivity().getApplicationContext(), "Settings not saved", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();

            default:
                break;
        }
    }

    private void clearAllFiltersOnButtons() {
        noStarIconButton.getBackground().clearColorFilter();
        bronzeStarIconButton.getBackground().clearColorFilter();
        silverStarIconButton.getBackground().clearColorFilter();
        goldStarIconButton.getBackground().clearColorFilter();
        shootingStarIconButton.getBackground().clearColorFilter();
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

        FirebaseHelper.getInstance().updateStarStatusOfUser(userStarStatus);

        Toast.makeText(getActivity().getApplicationContext(), "Saved successfully!", Toast.LENGTH_LONG).show();
    }

    private void changeUsername(String username) {
        CurrentUser.getInstance().assignUsername(username);
        FirebaseHelper.getInstance().changeUsername(username);
        Toast.makeText(getActivity().getApplicationContext(), "Username changed!", Toast.LENGTH_SHORT).show();
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

    //return true if username was changed
    private boolean attemptToChangeUsername(String proposedUsername) {
        if (hasUnlockedUsernameChange) {
            if (!proposedUsername.equals("")) {
                if (!proposedUsername.equals(CurrentUser.getInstance().getUsername())) {
                    if (FirebaseHelper.getInstance().hasUnlockedSpecialCharactersInUsername() || NewUserActivity.isValidUsername(proposedUsername)) {
                        if (proposedUsername.length() >= NewUserActivity.USERNAME_LENGTH_MIN && proposedUsername.length() <= NewUserActivity.USERNAME_LENGTH_MAX) {
                            if (MiscHelperMethods.isNetworkAvailable(getActivity())) {
                                if (FirebaseHelper.getInstance().isUsernameAvailable(proposedUsername)) {
                                    changeUsername(proposedUsername);
                                    return true;
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Username is taken, please try another", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "No internet available, check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Username must be between " + NewUserActivity.USERNAME_LENGTH_MIN + " and " + NewUserActivity.USERNAME_LENGTH_MAX + " characters long", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Only letters and numbers are allowed in a username", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "That's already your username, you goofball", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Please enter a new username", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "You have not unlocked this feature, see the about page for details on how", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}