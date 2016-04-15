package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LeaderboardsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    // views
    private Spinner distanceSpinner;
    private Spinner competitionSpinner;

    private ArrayList<FirebaseHelper.CompeteRecord> chuckRecords;
    private ArrayList<FirebaseHelper.CompeteRecord> spinRecords;
    private ArrayList<FirebaseHelper.CompeteRecord> dropRecords;

    public LeaderboardsFragment() {}

    public static LeaderboardsFragment newInstance(String param1, String param2) {
        LeaderboardsFragment fragment = new LeaderboardsFragment();
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
        View view = inflater.inflate(R.layout.content_leaderboards, container, false);

        initializeViews(view);

        chuckRecords = CurrentUser.getInstance().getChuckLeaderboard();
        spinRecords = CurrentUser.getInstance().getSpinLeaderboard();
        dropRecords = CurrentUser.getInstance().getDropLeaderboard();

        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity().getApplicationContext(), "You have no internet connection currently\nThis leaderboard may be out of date", Toast.LENGTH_LONG).show();
        }


        return view;
    }

    public void initializeViews(View view) {
        competitionSpinner = (Spinner) view.findViewById(R.id.leaderboards_competition_filter_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Competition_spinner_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        competitionSpinner.setAdapter(adapter);

        distanceSpinner = (Spinner) view.findViewById(R.id.leaderboards_distance_filter_dropdown);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.Radius_spinner_labels, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter2);

        //needs a final declaration to be used in listener
        final View v = view;

        AdapterView.OnItemSelectedListener filterResults = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //clear leaderboard entries to make room for new entries
                TableLayout leaderboardTable = (TableLayout) getActivity().findViewById(R.id.leaderboards_table_layout);
                leaderboardTable.removeAllViews();

                String distanceOption = distanceSpinner.getSelectedItem().toString();

                // Select the correct records
                String competitionOption = competitionSpinner.getSelectedItem().toString();
                ArrayList<FirebaseHelper.CompeteRecord> records = null;
                switch (competitionOption) {
                    case "Chuck" :
                        records = chuckRecords;
                        break;
                    case "Drop" :
                        records = dropRecords;
                        break;
                    case "Spin" :
                        records = spinRecords;
                        break;
                    default :
                        Log.d(TAG, "No scores loaded.");
                        break;
                }
                if (records != null) {
                    if (distanceOption.equals("Global")) {
                        for (int i = 0; i < records.size(); i++) {
                            addEntryToLeaderboard(i + 1, records.get(i).username, records.get(i).score, v);
                        }
                    } else {
                        // Grab out target distance
                        double targetDistance;
                        if (distanceOption.equals("Within 10 miles"))
                            targetDistance = 10.0;
                        else
                            targetDistance = 100.0;

                        // Grab user's location
                        Location userLocation = new Location("No provider");
                        userLocation.setLatitude(CurrentUser.getInstance().getLatitude());
                        userLocation.setLongitude(CurrentUser.getInstance().getLongitude());
                        int i = 0;
                        for (FirebaseHelper.CompeteRecord record : records) {
                            // For each record, grab record location
                            Location recordLocation = new Location("No provider");
                            recordLocation.setLatitude(record.latitude);
                            recordLocation.setLongitude(record.longitude);
                            // Compute distance to target in miles
                            double distance = getDistance(userLocation, recordLocation);
                            // If distance is within our target distance, display the record
                            if (distance < targetDistance) {
                                i++;
                                addEntryToLeaderboard(i, record.username, record.score, v);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        };

        competitionSpinner.setOnItemSelectedListener(filterResults);
        distanceSpinner.setOnItemSelectedListener(filterResults);
    }

    private double getDistance(Location loc1, Location loc2) {
        double scalingFactor = 0.0006213711923;
        double distance = loc1.distanceTo(loc2);
        return distance * scalingFactor;
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
            case R.id.leaderboards_competition_filter_dropdown:
                // change competition filter and update leaderboards
                break;
            default:
                // change distance filter and update leaderboards
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void addEntryToLeaderboard(int rank, String name, long score, View view) {
        final float scale = this.getResources().getDisplayMetrics().density;

        TableLayout leaderboardTable = (TableLayout) view.findViewById(R.id.leaderboards_table_layout);

        TableRow leaderboardRow = new TableRow(getActivity());
        TableLayout.LayoutParams LLParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        leaderboardRow.setLayoutParams(LLParams);
        leaderboardRow.setWeightSum(2);

        TextView leaderboardRank = new TextView(getActivity());
        leaderboardRank.setText(String.valueOf(rank));
        leaderboardRank.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        leaderboardRank.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        //should have a constant buffer, since it may vary in size
        leaderboardRank.getLayoutParams().width = 150;

        TextView leaderboardName = new TextView(getActivity());
        leaderboardName.setText(name);
        leaderboardName.setGravity(Gravity.LEFT);
        leaderboardName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardName.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        TextView leaderboardScore = new TextView(getActivity());
        leaderboardScore.setText(String.valueOf(score));
        leaderboardScore.setGravity(Gravity.RIGHT);
        leaderboardScore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardScore.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        leaderboardRow.addView(leaderboardRank);
        leaderboardRow.addView(leaderboardName);
        leaderboardRow.addView(leaderboardScore);

        View dividerView = new View(getActivity());
        dividerView.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6);
        dividerView.setLayoutParams(lp);

        leaderboardTable.addView(leaderboardRow);
        leaderboardTable.addView(dividerView);
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
        if(!CurrentUser.getInstance().sawBoardOnceWithoutGps() && !CurrentUser.getInstance().isGPSEnabled()){
            Toast.makeText(getActivity(), "Please, enable the GPS to update your location", Toast.LENGTH_LONG).show();
            CurrentUser.getInstance().updateSawBoardOnceWithoutGps();
        } else if(!CurrentUser.getInstance().sawBoardOnceWithGps() && CurrentUser.getInstance().needToUpdateLocation()){
            Toast.makeText(getActivity(), "Please, wait the GPS update your location", Toast.LENGTH_LONG).show();
            CurrentUser.getInstance().updateSawBoardOnceWithGps();
        }
        Log.d(TAG, "onResume() called");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}