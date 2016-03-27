package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.FirebaseError;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangePasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    // views
    private Button confirmButton;
    private Button cancelButton;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordConfirmationEditText;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    public ChangePasswordFragment() {}

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        mSharedPreferencesHelper = new SharedPreferencesHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_change_password, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view){
        oldPasswordEditText = (EditText) view.findViewById(R.id.change_password_old_password_edit_text);
        newPasswordEditText = (EditText) view.findViewById(R.id.change_password_new_password_edit_text);
        newPasswordConfirmationEditText = (EditText) view.findViewById(R.id.change_password_new_password_confirmation_edit_text);

        confirmButton = (Button) view.findViewById(R.id.change_password_confirm_button);
        confirmButton.setOnClickListener(this);
        cancelButton = (Button) view.findViewById(R.id.change_password_cancel_button);
        cancelButton.setOnClickListener(this);
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
            case R.id.change_password_confirm_button:
                if (newPasswordEditText.getText().toString().equals(newPasswordConfirmationEditText.getText().toString())) {
                    //TODO
                    //is it bad to use the shared preferences as the check here for the password?
                    if (mSharedPreferencesHelper.getPassword().equals(oldPasswordEditText.getText().toString())) {
                        mSharedPreferencesHelper.setPassword(newPasswordConfirmationEditText.getText().toString());
                        FirebaseHelper.getInstance().changePassword(mSharedPreferencesHelper.getEmail(), oldPasswordEditText.getText().toString(), newPasswordConfirmationEditText.getText().toString(), this);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Your old password is incorrect, please re-type it", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Your new password entries don't match, please re-type them", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                // go to previous fragment
                startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
                break;
        }
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

    public void onSuccessfulPasswordChange() {
        Toast.makeText(getActivity().getApplicationContext(), "Password was changed!", Toast.LENGTH_LONG).show();
        mSharedPreferencesHelper.clearSharedData();

        //jump to chuck compete fragment, might be a better way to do this
        startActivity(new Intent(getActivity().getApplication(), MainActivity.class));
    }

    public void onUnsuccessfulPasswordChange(FirebaseError firebaseError) {
        Toast.makeText(getActivity().getApplicationContext(), "Password was not changed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
    }
}