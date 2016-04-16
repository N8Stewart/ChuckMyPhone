package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    protected boolean hasLoadedInitialSnapshot;


    //needed to work asynchonously with new user and login activities
    private NewUserActivity newUserActivity;
    private LoginActivity loginActivity;
    private ForgotPasswordActivity forgotPasswordActivity;
    ChangePasswordFragment changePasswordFragment;
    private String loginEmail, loginPassword;

    public enum competitionType {
        CHUCK, DROP, SPIN
    };

    public class User {
        public ArrayList<Badge> badgeList;
        public CompeteRecord bestChuckRecord;
        public CompeteRecord bestDropRecord;
        public CompeteRecord bestSpinRecord;
        public String username;

        //TODO get users location here

        public User(String username, Context c) {
            this.username = username;

            this.badgeList = new ArrayList<>();

            //Add all badges here with no earned date
            badgeList.add(new Badge(c.getString(R.string.badge_chuck_level_one)));
            badgeList.add(new Badge(c.getString(R.string.badge_chuck_level_two)));
            badgeList.add(new Badge(c.getString(R.string.badge_chuck_level_three)));

            badgeList.add(new Badge(c.getString(R.string.badge_drop_level_one)));
            badgeList.add(new Badge(c.getString(R.string.badge_drop_level_two)));
            badgeList.add(new Badge(c.getString(R.string.badge_drop_level_three)));

            badgeList.add(new Badge(c.getString(R.string.badge_spin_level_one)));
            badgeList.add(new Badge(c.getString(R.string.badge_spin_level_two)));
            badgeList.add(new Badge(c.getString(R.string.badge_spin_level_three)));

            badgeList.add(new Badge(c.getString(R.string.badge_one_percent)));
            badgeList.add(new Badge(c.getString(R.string.badge_hidden)));

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

        public CompeteRecord(long score, double latitude, double longitude, competitionType competition, String username) {
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
        hasLoadedInitialSnapshot = false;
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("@@@@@@ DATA CHANGE @@@@@@" + snapshot.getValue());
                dataSnapshot = snapshot;

                hasLoadedInitialSnapshot = true;

                //if users data has appeared then get their score from it
                String userID = CurrentUser.getInstance().getUserId();
                if (userID != null) {
                    if (dataSnapshot.child("users").hasChild(userID)) {
                        CurrentUser.getInstance().loadUserScoreData();
                        CurrentUser.getInstance().loadUserBadgeData();
                    }
                }

                updateLeaderboard();

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public void createUser(String email, String password, String username, NewUserActivity activity) {
        newUserActivity = activity;
        CurrentUser.getInstance().assignUsername(username);
        myFirebaseRef.createUser(email, password, userCreationHandler);
    }

    public boolean isUsernameAvailable(String username) {
        boolean usernameIsAvailable = true;
        if (dataSnapshot != null) {
            for (DataSnapshot user : dataSnapshot.child("users").getChildren()) {
                if (user.hasChild("username")) {
                    if (username.equals(user.child("username").getValue())) {
                        usernameIsAvailable = false;
                    }
                }
            }
        }
        return usernameIsAvailable;
    }

    public boolean login(String email, String password, LoginActivity activity) {
        loginActivity = activity;
        loginEmail = email;
        loginPassword = password;
        boolean firebaseWasLoaded = false;
        if (myFirebaseRef != null) {
            firebaseWasLoaded = true;
            myFirebaseRef.authWithPassword(email, password, loginHandler);
        }
        return firebaseWasLoaded;
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
           if (dataSnapshot != null) {
                if (!dataSnapshot.hasChild("users/" + authData.getUid())) {
                    //create the record and insert it into Firebase
                    Firebase newUserRef = myFirebaseRef.child("users/" + authData.getUid());
                    newUserRef.setValue(new User(CurrentUser.getInstance().getUsername(), newUserActivity.getApplicationContext()));
                } else {
                    System.out.println("User logged into existing account, no data was changed");
                }
                System.out.println("Login handled: User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                CurrentUser.getInstance().loadUserMetaData(authData.getUid());
                loginActivity.onSuccessfulLogin(loginEmail, loginPassword, authData.getUid());
            } else {
                //TODO
                //need to do something here? maybe try to re-authenticate user?
            }
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
        String username = "";
        if (dataSnapshot.hasChild("users/"+userID)) {
            if (dataSnapshot.hasChild("users/"+userID+"/username")) {
                username = dataSnapshot.child("users/" + userID + "/username").getValue().toString();
            } else {
                //user doesn't have a username saved
            }
        }

        return username;
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

    protected ArrayList<Badge> getBadges() {
        Log.d("tag", "GETTING USERS BADGES##################");
        String userID = CurrentUser.getInstance().getUserId();
        if (dataSnapshot.hasChild("users/" + userID+"/badgeList")) {
            DataSnapshot usersSnapshot = dataSnapshot.child("users/" + userID+"/badgeList");
            ArrayList<Badge> badgeList = new ArrayList<>();
            for (DataSnapshot badgeSnapshot: usersSnapshot.getChildren()) {
                String badgeName = badgeSnapshot.child("name").getValue().toString();
                String unlockDate = badgeSnapshot.child("unlockDate").getValue().toString();

                badgeList.add(new Badge(badgeName, unlockDate));
            }
            return badgeList;
        }
        return new ArrayList<>();
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

    protected void addFakeChuckScoresToLeaderboard() {
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 100; i++) {
                String userID = "userC"+j+"_"+ i;
                String username = "usernameC"+j+"_"+ i;
                int score = i;
                int latitude = 40;
                int longitude = 84;
                myFirebaseRef.child("ChuckScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.CHUCK, username), score);
            }
        }
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addChuckScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        String username = CurrentUser.getInstance().getUsername();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("ChuckScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.CHUCK, CurrentUser.getInstance().getUsername()), score);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addSpinScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("SpinScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.SPIN, CurrentUser.getInstance().getUsername()), score);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    protected void addDropScoreToLeaderboard(long score, double latitude, double longitude) {
        String userID = CurrentUser.getInstance().getUserId();
        //priority set as inverse of the score, should order entries automatically
        myFirebaseRef.child("DropScores/" + userID).setValue(new CompeteRecord(score, latitude, longitude, competitionType.DROP, CurrentUser.getInstance().getUsername()), score);
    }

    protected void unlockBadge(String badgeName) {
        String userID = CurrentUser.getInstance().getUserId();
        for (int i = 0; i < 11; i++) {
            if (dataSnapshot.hasChild("users/" + userID + "/badgeList/"+i) && dataSnapshot.child("users/" + userID + "/badgeList/"+i+"/name").getValue().equals(badgeName)) {
                DateFormat df = new SimpleDateFormat("MM/dd/yy");
                Date dateobj = new Date();
                myFirebaseRef.child("users/" + userID + "/badgeList/"+i+"/unlockDate").setValue(df.format(dateobj));
                i = 20; //end this loop
            }
        }
    }

    protected boolean hasBadge(String badgeName) {
        boolean hasBadge = false;
        String userID = CurrentUser.getInstance().getUserId();
        if (dataSnapshot.hasChild("users/" + userID+"/badgeList")) {
            DataSnapshot usersSnapshot = dataSnapshot.child("users/" + userID+"/badgeList");
            ArrayList<Badge> badgeList = new ArrayList<>();
            for (DataSnapshot badgeSnapshot: usersSnapshot.getChildren()) {
                if (badgeSnapshot.child("name").getValue().toString().equals(badgeName)) {
                    String unlockDate = badgeSnapshot.child("unlockDate").getValue().toString();
                    if (unlockDate != "") {
                        hasBadge = true;
                        break;
                    }
                }
            }
        }
        return hasBadge;
    }

    protected void updateLeaderboard() {
        ArrayList<CompeteRecord> chuckLeaderboardGlobal = new ArrayList<>();
        ArrayList<CompeteRecord> spinLeaderboardGlobal = new ArrayList<>();
        ArrayList<CompeteRecord> dropLeaderboardGlobal = new ArrayList<>();

        //may be possible to query up until the users entry, but that can wait until later
        //Query queryRef = myFirebaseRef.orderByPriority().endAt(??);

        Query top100Chuck = myFirebaseRef.child("ChuckScores").orderByPriority();//.limitToLast(100);
        Query top100Spin = myFirebaseRef.child("SpinScores").orderByPriority();//.limitToLast(100);
        Query top100Drop = myFirebaseRef.child("DropScores").orderByPriority();//.limitToLast(100);

        //take one look at the data, pass it to the current user, and then throw it away
        top100Chuck.addListenerForSingleValueEvent(chuckLeaderboardValueEventListener);
        top100Spin.addListenerForSingleValueEvent(spinLeaderboardValueEventListener);
        top100Drop.addListenerForSingleValueEvent(dropLeaderboardValueEventListener);
    }

    ValueEventListener chuckLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> chuckRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                double latitude = (double) subSnapshot.child("latitude").getValue();
                double longitude = (double) subSnapshot.child("longitude").getValue();
                chuckRecords.add(new CompeteRecord(score, latitude, longitude, competitionType.CHUCK,username));
            }

            CurrentUser.getInstance().updateGlobalChuckLeaderboard(chuckRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    ValueEventListener spinLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> spinRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                double latitude = (double) subSnapshot.child("latitude").getValue();
                double longitude = (double) subSnapshot.child("longitude").getValue();
                spinRecords.add(new CompeteRecord(score, latitude, longitude, competitionType.SPIN, username));
            }

            CurrentUser.getInstance().updateGlobalSpinLeaderboard(spinRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    ValueEventListener dropLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> dropRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                String username = (String) subSnapshot.child("username").getValue();
                double latitude = (double) subSnapshot.child("latitude").getValue();
                double longitude = (double) subSnapshot.child("longitude").getValue();
                dropRecords.add(new CompeteRecord(score, latitude, longitude, competitionType.DROP, username));
            }

            CurrentUser.getInstance().updateGlobalDropLeaderboard(dropRecords);
        }

        @Override
        public void onCancelled(FirebaseError error) {
        }
    };

    public void changePassword(String loginEmail, String oldPassword, String newPassword, final ChangePasswordFragment changePasswordFragment) {
        this.changePasswordFragment = changePasswordFragment;
        myFirebaseRef.changePassword(loginEmail, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                changePasswordFragment.onSuccessfulPasswordChange();
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                changePasswordFragment.onUnsuccessfulPasswordChange(firebaseError);
            }
        });
    }

    public void resetPassword(String loginEmail, final ForgotPasswordActivity forgotPasswordActivity) {
        this.forgotPasswordActivity = forgotPasswordActivity;
        myFirebaseRef.resetPassword(loginEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                forgotPasswordActivity.onPasswordSuccessfullyReset();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                forgotPasswordActivity.onPasswordUnsuccessfullyReset(firebaseError);
            }
        });
    }

}