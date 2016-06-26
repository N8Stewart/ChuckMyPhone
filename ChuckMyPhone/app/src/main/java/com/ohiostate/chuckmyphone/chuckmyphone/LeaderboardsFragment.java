package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LeaderboardsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private final int MAX_NUM_LEADERBOARD_ENTRIES_DISPLAYED = 100;

    private OnFragmentInteractionListener mListener;

    private static final int earthRadius = 6371;

    private ArrayList<FirebaseHelper.CompeteRecord> chuckRecords;
    private ArrayList<FirebaseHelper.CompeteRecord> spinRecords;
    private ArrayList<FirebaseHelper.CompeteRecord> dropRecords;

    // Views
    private Spinner distanceSpinner;
    private Spinner competitionSpinner;

    private TableLayout leaderboardTable;

    public static LeaderboardsFragment newInstance() {
        return new LeaderboardsFragment();
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

        if (!MiscHelperMethods.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity().getApplicationContext(), "You have no internet connection\nThis leaderboard may be out of date", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void initializeViews(View view) {
        competitionSpinner = (Spinner) view.findViewById(R.id.leaderboards_competition_filter_dropdown);
        ArrayAdapter<CharSequence> competitionSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Competition_spinner_labels, android.R.layout.simple_spinner_item);
        competitionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        competitionSpinner.setAdapter(competitionSpinnerAdapter);

        distanceSpinner = (Spinner) view.findViewById(R.id.leaderboards_distance_filter_dropdown);
        ArrayAdapter<CharSequence> radiusSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Radius_spinner_labels, android.R.layout.simple_spinner_item);
        radiusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(radiusSpinnerAdapter);

        //needs a final declaration to be used in listener
        final View v = view;

        final AdapterView.OnItemSelectedListener filterResults = new AdapterView.OnItemSelectedListener() {
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
                            if (i > MAX_NUM_LEADERBOARD_ENTRIES_DISPLAYED - 1) {
                                break;
                            }

                            String starName = FirebaseHelper.getInstance().getStarStatusOfUser(records.get(i).username);
                            String userUnusualStarString = FirebaseHelper.getInstance().getUnusualStarStatusOfUser(records.get(i).username);

                            addEntryToLeaderboard(i + 1, records.get(i).username, records.get(i).score, v,starName, userUnusualStarString);
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
                        else if (distanceOption.equals("Within 100 miles")) {
                            targetDistance = 100.0;
                        } else {
                            targetDistance = 1000.0;
                        }

                        CurrentUser user = CurrentUser.getInstance();
                        int i = 0;
                        for (FirebaseHelper.CompeteRecord record : records) {
                            // For each record, grab record location
                            double distance = calculateDistance(user.getLatitude(), user.getLongitude(), record.latitude, record.longitude);
                            // If distance is within our target distance, display the record
                            if (distance < targetDistance) {
                                i++;
                                String starName = FirebaseHelper.getInstance().getStarStatusOfUser(record.username);
                                String userUnusualStarString = FirebaseHelper.getInstance().getUnusualStarStatusOfUser(record.username);
                                addEntryToLeaderboard(i, record.username, record.score, v, starName, userUnusualStarString);
                            }
                            if (CurrentUser.getInstance().getUsername().equals(record.username)) {
                                rank = i;
                                score = record.score;
                            }
                            //don't have more than 100 entries
                            if (i > MAX_NUM_LEADERBOARD_ENTRIES_DISPLAYED - 1) {
                                break;
                            }
                        }

                    }
                    String starName = FirebaseHelper.getInstance().getStarStatusOfUser(CurrentUser.getInstance().getUsername());
                    String userUnusualStarString = FirebaseHelper.getInstance().getUnusualStarStatusOfUser(CurrentUser.getInstance().getUsername());
                    if (rank == -1) {
                        addUserStaticRankToLeaderboard("N/A", CurrentUser.getInstance().getUsername(), 0 + "", v, starName, userUnusualStarString);
                    } else {
                        addUserStaticRankToLeaderboard(rank + "", CurrentUser.getInstance().getUsername(), score + "", v, starName, userUnusualStarString);
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

    private void checkToUnlockOnePercentBadge(int rank) {
        int numRecords = leaderboardTable.getChildCount() / 2;
        if (numRecords > 99 && rank < 11 && rank > 0 && !FirebaseHelper.getInstance().hasBadge(getActivity().getString(R.string.badge_one_percent))) {
            FirebaseHelper.getInstance().unlockBadge(getActivity().getString(R.string.badge_one_percent));
        }
    }

    static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a =(Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double d = earthRadius * c;
        double conversion = 0.6213711923; // convert km to mile
        return d * conversion;
    }

    private void addUserStaticRankToLeaderboard(String rank, String name, String score, View view, String star_name, String userUnusualStarString) {
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
        leaderboardName.setGravity(Gravity.START);
        leaderboardName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardName.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        TextView leaderboardScore = new TextView(getActivity());
        leaderboardScore.setText(score);
        leaderboardScore.setGravity(Gravity.END);
        leaderboardScore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardScore.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        userRow.addView(leaderboardRank);
        userRow.addView(leaderboardName);

        boolean isUnusual = false;
        int iconID = R.drawable.unusual_rainbow_86;
        if (!star_name.equals("none")) {
            if (star_name.equals("bronze")) {
                //if user has unlocked the unusual version
                if (userUnusualStarString.contains("1")) {
                    iconID = R.drawable.unusual_bronze_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.bronze_star_icon;
                }
            } else if (star_name.equals("silver")) {
                if (userUnusualStarString.contains("2")) {
                    iconID = R.drawable.unusual_silver_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.silver_star_icon;
                }
            } else if (star_name.equals("gold")) {
                if (userUnusualStarString.contains("3")) {
                    iconID = R.drawable.unusual_gold_86_v2;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.gold_star_icon;
                }
            } else { //shooting star
                if (userUnusualStarString.contains("4")) {
                    iconID = R.drawable.unusual_rainbow_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.shooting_star_icon;
                }
            }
        }

        //add either the gif or the icon to the row
        if (isUnusual) {
            PlayGifView unusualStarGifView = new PlayGifView(getContext(), null);
            unusualStarGifView.setImageResource(iconID);
            userRow.addView(unusualStarGifView);
        } else {
            ImageView starImageView = new ImageView(getActivity());
            starImageView.setImageResource(iconID);
            userRow.addView(starImageView);
        }

        userRow.addView(leaderboardScore);

        leaderboardUserRecordTable.removeAllViews();
        leaderboardUserRecordTable.addView(userRow);
    }

    //TODO factor out repeated code from above and below

    private void addEntryToLeaderboard(int rank, String name, long score, View view, String star_name, String userUnusualStarString) {
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
        leaderboardName.setGravity(Gravity.START);
        leaderboardName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardName.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        TextView leaderboardScore = new TextView(getActivity());
        leaderboardScore.setText(String.valueOf(score));
        leaderboardScore.setGravity(Gravity.END);
        leaderboardScore.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        leaderboardScore.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);

        leaderboardRow.addView(leaderboardRank);
        leaderboardRow.addView(leaderboardName);

        boolean isUnusual = false;
        if (!star_name.equals("none")) {
            int iconID;
            if (star_name.equals("bronze")) {
                //if user has unlocked the unusual version
                if (userUnusualStarString.contains("1")) {
                    iconID = R.drawable.unusual_bronze_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.bronze_star_icon;
                }
            } else if (star_name.equals("silver")) {
                if (userUnusualStarString.contains("2")) {
                    iconID = R.drawable.unusual_silver_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.silver_star_icon;
                }
            } else if (star_name.equals("gold")) {
                if (userUnusualStarString.contains("3")) {
                    iconID = R.drawable.unusual_gold_86_v2;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.gold_star_icon;
                }
            } else { //shooting star
                if (userUnusualStarString.contains("4")) {
                    iconID = R.drawable.unusual_rainbow_86;
                    isUnusual = true;
                } else {
                    iconID = R.drawable.shooting_star_icon;
                }
            }

            //add either the gif or the icon to the row
            if (isUnusual) {
                PlayGifView unusualStarGifView = new PlayGifView(getContext(), null);
                unusualStarGifView.setImageResource(iconID);
                leaderboardRow.addView(unusualStarGifView);
            } else {
                ImageView starImageView = new ImageView(getActivity());
                starImageView.setImageResource(iconID);
                leaderboardRow.addView(starImageView);
            }
        }
        leaderboardRow.addView(leaderboardScore);

        View dividerView = new View(getActivity());
        dividerView.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6);
        dividerView.setLayoutParams(lp);

        leaderboardTable.addView(leaderboardRow);
        leaderboardTable.addView(dividerView);
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