package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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

    private TableLayout leaderboardTable;

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

        if (!CurrentUser.getInstance().sawBoardOnceWithoutGps() && !CurrentUser.getInstance().isGPSEnabled()) {
            Toast.makeText(getActivity(), "Please, enable the GPS to update your location", Toast.LENGTH_LONG).show();
            CurrentUser.getInstance().updateSawBoardOnceWithoutGps();
        } else if (!CurrentUser.getInstance().sawBoardOnceWithGps() && CurrentUser.getInstance().needToUpdateLocation()) {
            Toast.makeText(getActivity(), "Please, wait the GPS update your location", Toast.LENGTH_LONG).show();
            CurrentUser.getInstance().updateSawBoardOnceWithGps();
        }

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
                leaderboardTable = (TableLayout) getActivity().findViewById(R.id.leaderboards_table_layout);
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
                    int rank = -1;
                    long score = 0;
                    if (distanceOption.equals("Global")) {
                        for (int i = 0; i < records.size(); i++) {
                            //don't have more than 100 entries
                            if (i > 99) {
                                break;
                            }
                            addEntryToLeaderboard(i + 1, records.get(i).username, records.get(i).score, v);
                            if (CurrentUser.getInstance().getUsername().equals(records.get(i).username)) {
                                rank = i + 1;
                                score = records.get(i).score;
                            }
                        }
                    } else {
                        // Grab the target distance
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
                            if (CurrentUser.getInstance().getUsername().equals(record.username)) {
                                rank = i;
                                score = record.score;
                            }
                            //don't have more than 100 entries
                            if (i > 99) {
                                break;
                            }
                        }

                    }
                    if (rank == -1) {
                        addUserStaticRankToLeaderboard("N/A", CurrentUser.getInstance().getUsername(), 0 + "", v);
                    } else {
                        addUserStaticRankToLeaderboard(rank + "", CurrentUser.getInstance().getUsername(), score + "", v);
                    }

                    checkToUnlockOnePercentBadge(rank);
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

    public void checkToUnlockOnePercentBadge(int rank) {
        int numRecords = leaderboardTable.getChildCount() / 2;
        if (numRecords > 99 && rank < 11 && rank > 0 && !FirebaseHelper.getInstance().hasBadge(getActivity().getString(R.string.badge_one_percent))) {
            FirebaseHelper.getInstance().unlockBadge(getActivity().getString(R.string.badge_one_percent));
            //initiatePopupWindow(getActivity().getString(R.string.badge_one_percent));
            Log.d(TAG, "UNLOCKING BADGE");
        }
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

    public void addUserStaticRankToLeaderboard(String rank, String name, String score, View view) {
        final float scale = this.getResources().getDisplayMetrics().density;

        TableLayout leaderboardUserRecordTable = (TableLayout) view.findViewById(R.id.leaderboards_user_record);

        TableRow userRow = new TableRow(getActivity());
        TableLayout.LayoutParams LLParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        userRow.setLayoutParams(LLParams);
        userRow.setWeightSum(2);

        TextView leaderboardRank = new TextView(getActivity());
        leaderboardRank.setText(rank);
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
        leaderboardScore.setText(score);
        leaderboardScore.setGravity(Gravity.RIGHT);
        leaderboardScore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardScore.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        userRow.addView(leaderboardRank);
        userRow.addView(leaderboardName);
        userRow.addView(leaderboardScore);

        leaderboardUserRecordTable.addView(userRow);
    }

    public void addEntryToLeaderboard(int rank, String name, long score, View view) {
        final float scale = this.getResources().getDisplayMetrics().density;

        leaderboardTable = (TableLayout) view.findViewById(R.id.leaderboards_table_layout);

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
        Log.d(TAG, "onResume() called");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected PopupWindow pw;
    protected void initiatePopupWindow(String badgeName) {
        if (CurrentUser.getInstance().getBadgeNotificationsEnabled()) {
            try {
                final String bName = badgeName;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup, (ViewGroup) getActivity().findViewById(R.id.popup_element));
                        pw = new PopupWindow(layout, 1000, 1000, true);
                        TextView badgeTitle = (TextView) layout.findViewById(R.id.popup_BadgeTitleTextView);
                        TextView badgeDescription = (TextView) layout.findViewById(R.id.popup_BadgeDescriptionTextView);

                        badgeTitle.setText(Html.fromHtml("<i>" + bName + "</i>"));
                        badgeDescription.setText("\n" + Badge.badgeNameToDescriptionMap.get(bName));

                        Button cancelButton = (Button) layout.findViewById(R.id.popup_cancel_button);
                        cancelButton.setOnClickListener(cancel_button_click_listener);

                        // display the popup in the center if user didn't navigate away quickly via hamburger menu
                        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
            Log.d(TAG, "Cancel button pressed");
        }
    };
}