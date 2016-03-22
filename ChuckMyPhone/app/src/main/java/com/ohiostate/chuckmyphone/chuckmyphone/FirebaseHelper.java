package com.ohiostate.chuckmyphone.chuckmyphone;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 3/11/2016.
 */
public class FirebaseHelper {

    private static FirebaseHelper ourInstance = new FirebaseHelper();

    public static FirebaseHelper getInstance() {
        return ourInstance;
    }

    private FirebaseHelper() {
    }

    private DataSnapshot dataSnapshot;

    //needed to work asynchonously with new user and login activities
    private NewUserActivity newUserActivity;
    private LoginActivity loginActivity;
    private String loginEmail, loginPassword;

    public enum competitionType {
        CHUCK, DROP, SPIN
    };

    public class User {
        public Map<String, Object> badgeStatusMap;
        public Map<String, Object> badgeEarnedOnMap;
        public CompeteRecord bestChuckRecord;
        public CompeteRecord bestDropRecord;
        public CompeteRecord bestSpinRecord;
        public String username;

        //TODO get users location here

        public User(String username) {
            this.badgeStatusMap = new HashMap<String, Object>();
            this.badgeEarnedOnMap = new HashMap<String, Object>();
            this.username = username;

            //Add all badges here with no earned date
            badgeStatusMap.put("badge1", "false");
            badgeEarnedOnMap.put("badge1", "1/1/2016");

            bestChuckRecord = new CompeteRecord(competitionType.CHUCK, username);
            bestSpinRecord = new CompeteRecord(competitionType.SPIN, username);
            bestDropRecord = new CompeteRecord(competitionType.DROP, username);
        }
    }

    public class CompeteRecord {
        public long score;
        public double longitude;
        public double latitude;
        public competitionType competition;
        public String username;

        public CompeteRecord(competitionType competition, String username) {
            this.score = 0;
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.competition = competition;
            this.username = username;
        }

        public CompeteRecord(long score, double longitude, double latitude, competitionType competition, String username) {
            this.score = score;
            this.longitude = longitude;
            this.latitude = latitude;
            this.competition = competition;
            this.username = username;
        }
    }

    private Firebase myFirebaseRef;

    //firebase initializer, must be called before any other firebase logic is
    public void create() {
        myFirebaseRef = new Firebase("https://amber-inferno-6835.firebaseio.com/");

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("@@@@@@ DATA CHANGE @@@@@@" + snapshot.getValue());
                dataSnapshot = snapshot;

                //if users data has appeared then get their score from it
                String userID = CurrentUser.getInstance().getUserId();
                if (userID != null){
                        if(dataSnapshot.child("users").hasChild(userID)) {
                            CurrentUser.getInstance().loadUserScoreData();
                        }
                }

                updateLeaderboard();

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public void createUserWithoutFacebook(String email, String password, String username, NewUserActivity activity) {
        newUserActivity = activity;
        CurrentUser.getInstance().assignUsername(username);
        myFirebaseRef.createUser(email, password, userCreationHandler);
    }

    public void loginWithoutFacebook(String email, String password, LoginActivity activity) {
        loginActivity = activity;
        loginEmail = email;
        loginPassword = password;
        myFirebaseRef.authWithPassword(email, password, loginHandler);
    }

    Firebase.ValueResultHandler<Map<String, Object>> userCreationHandler = new Firebase.ValueResultHandler<Map<String, Object>>() {
        //Event driven: called when user creation succeeds
        @Override
        public void onSuccess(Map<String, Object> result) {
            System.out.println("Successfully created user account with uid: " + result.get("uid"));
            newUserActivity.accountWasCreated();
        }

        //Event driven: called when user creation fails
        @Override
        public void onError(FirebaseError firebaseError) {
            System.out.println("Error on user creation: " + firebaseError.toString());
            newUserActivity.accountWasNotCreated(firebaseError.getMessage());
        }
    };

   Firebase.AuthResultHandler loginHandler = new Firebase.AuthResultHandler() {
       //Event driven: called when user login succeeds
       @Override
        public void onAuthenticated(AuthData authData) {
            if (!dataSnapshot.hasChild("users/"+authData.getUid())) {
                //create the record and insert it into Firebase
                Firebase newUserRef = myFirebaseRef.child("users/"+authData.getUid());
                newUserRef.setValue(new User(CurrentUser.getInstance().getUsername()));
            } else {
                System.out.println("User logged into existing account, no data was changed");
            }
            System.out.println("Login handled: User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            CurrentUser.getInstance().loadUserMetaData(authData.getUid(), authData.getProvider());
            CurrentUser.getInstance().assignUsername(getUsername(authData.getUid()));
            loginActivity.onSuccessfulLogin(loginEmail, loginPassword);
        }

       //Event driven: called when user login fails
       @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            // there was an error
            System.out.println("Error logging into Firebase: "+ firebaseError.toString());
            loginActivity.onUnsuccessfulLogin(firebaseError.getMessage());
        }
    };

    //ACCESSOR METHODS FOR GETTING DATA

    public String getUsername(String userID) {
        if (dataSnapshot.hasChild("users/"+userID)) {
            return dataSnapshot.child("users/"+userID+"/username").getValue().toString();
        }
        return ""; //user doesn't exist
    }

    //Need user to be logged in before this may be called
    protected long getBestSpinScore() {
        String userID = CurrentUser.getInstance().getUserId();
        if (dataSnapshot.hasChild("users/" + userID + "/bestSpinRecord/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("users/" + userID + "/bestSpinRecord/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    protected long getBestChuckScore() {
        String userID = CurrentUser.getInstance().getUserId();
        if (dataSnapshot.hasChild("users/" + userID + "/bestChuckRecord/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("users/" + userID + "/bestChuckRecord/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    protected long getBestDropScore() {
        String userID = CurrentUser.getInstance().getUserId();
        if (dataSnapshot.hasChild("users/" + userID + "/bestDropRecord/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("users/" + userID + "/bestDropRecord/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    //SETTING METHODS FOR SAVING SCORES
    protected void updateBestChuckScore(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        myFirebaseRef.child("users/" + userID + "/bestChuckRecord/score").setValue(score);

        addChuckScoreToLeaderboard(score, latitude, longitude);
    }

    protected void updateBestSpinScore(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        myFirebaseRef.child("users/" + userID + "/bestSpinRecord/score").setValue(score);

        addSpinScoreToLeaderboard(score, latitude, longitude);
    }

    protected void updateBestDropScore(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        myFirebaseRef.child("users/" + userID + "/bestDropRecord/score").setValue(score);

        addDropScoreToLeaderboard(score, latitude, longitude);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addChuckScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("ChuckScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.CHUCK, CurrentUser.getInstance().getUsername()), 999999999-score);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addSpinScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("SpinScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.SPIN, CurrentUser.getInstance().getUsername()), 999999999-score);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addDropScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("DropScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.DROP, CurrentUser.getInstance().getUsername()), 999999999-score);
    }

    protected void updateLeaderboard() {
        ArrayList<CompeteRecord> chuckLeaderboardGlobal = new ArrayList<CompeteRecord>();
        ArrayList<CompeteRecord> spinLeaderboardGlobal = new ArrayList<CompeteRecord>();
        ArrayList<CompeteRecord> dropLeaderboardGlobal = new ArrayList<CompeteRecord>();

        //may be possible to query up until the users entry, but that can wait until later
        //Query queryRef = myFirebaseRef.orderByPriority().endAt(??);
        Query top100Chuck = myFirebaseRef.child("ChuckScores").orderByPriority().limitToLast(100);
        Query top100Spin = myFirebaseRef.child("SpinScores").orderByPriority().limitToLast(100);
        Query top100Drop = myFirebaseRef.child("DropScores").orderByPriority().limitToLast(100);

        //take one look at the data, pass it to the current user, and then throw it away
        top100Chuck.addListenerForSingleValueEvent(chuckLeaderboardValueEventListener);
        top100Spin.addListenerForSingleValueEvent(spinLeaderboardValueEventListener);
        top100Drop.addListenerForSingleValueEvent(dropLeaderboardValueEventListener);
    }

    ValueEventListener chuckLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> chuckRecords = new ArrayList<CompeteRecord>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                chuckRecords.add(new CompeteRecord(score, 0.0, 0.0, competitionType.CHUCK,username));
            }

            CurrentUser.getInstance().updateChuckLeaderboard(chuckRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    ValueEventListener spinLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> spinRecords = new ArrayList<CompeteRecord>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                spinRecords.add(new CompeteRecord(score, 0.0, 0.0, competitionType.SPIN, username));
            }

            CurrentUser.getInstance().updateSpinLeaderboard(spinRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    ValueEventListener dropLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> dropRecords = new ArrayList<CompeteRecord>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                dropRecords.add(new CompeteRecord(score, 0.0, 0.0, competitionType.DROP, username));
            }

            CurrentUser.getInstance().updateDropLeaderboard(dropRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };
}



