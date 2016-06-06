package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

public class FirebaseHelper {

    private final static FirebaseHelper ourInstance = new FirebaseHelper();

    public static FirebaseHelper getInstance() {
        return ourInstance;
    }

    private FirebaseHelper() {}

    private CurrentUser currentUser;

    private DataSnapshot dataSnapshot;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabaseRef;

    boolean hasLoadedInitialSnapshot;

    private String loginEmail, loginPassword;

    public class User {
        public final ArrayList<Badge> badgeList;
        public final String username;
        public final String starIconName; // tells what the current displaying icon is
        public final String highestStarIconEarned; // tells what the highest level icon they may display is

        //TODO get users location here

        public User() {
            badgeList = null;
            username = "";
            starIconName = "none";
            highestStarIconEarned = "none";
        }

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

            starIconName = "none";
            highestStarIconEarned = "none";
        }
    }

    public class CompeteRecord {
        public final String username;
        public final long score;
        public final double longitude;
        public final double latitude;

        public CompeteRecord(String username, long score, double latitude, double longitude) {
            this.username = username;
            this.score = score;
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    //firebase initializer, must be called before any other firebase logic is
    public void create() {
        currentUser = CurrentUser.getInstance();

        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
        hasLoadedInitialSnapshot = false;
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataSnapshot = snapshot;
                hasLoadedInitialSnapshot = true;

                //if users data has appeared then get their score from it
                String userID = currentUser.getUserId();
                if (userID != null) {
                    if (dataSnapshot.child("users").hasChild(userID)) {
                        currentUser.loadUserScoreData();
                        currentUser.loadUserBadgeData();
                    }
                }

                updateLeaderboard();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void createUser(String email, String password, String username, final NewUserActivity activity) {
        currentUser.assignUsername(username);
        Task<AuthResult> userCreationTask = firebaseAuth.createUserWithEmailAndPassword(email, password);
        userCreationTask.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                System.out.println("Created user account in firebase");
                activity.accountWasCreated();
            }
        });

        userCreationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failed to create user account in firebase");
                activity.accountWasNotCreated(e.getMessage());
            }
        });
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
        } else {
            usernameIsAvailable = false;
        }
        return usernameIsAvailable;
    }

    public void changeUsername(String newUsername) {
        String userID = currentUser.getUserId();

        //change username in the chuck, spin and drop records
        String[] competeRecordStrings = {"ChuckScores/", "SpinScores/", "DropScores/"};
        for (String competeRecordString : competeRecordStrings) {
            if (dataSnapshot.hasChild(competeRecordString + userID)) {
                firebaseDatabaseRef.child(competeRecordString + userID + "/username").setValue(newUsername);
            }
        }

        //change username in the users records
        firebaseDatabaseRef.child("users/" + userID + "/username").setValue(newUsername);
    }

    public boolean login(String email, String password, final LoginActivity activity) {
        loginEmail = email;
        loginPassword = password;
        boolean firebaseWasLoaded = false;
        if (firebaseAuth != null) {
            firebaseWasLoaded = true;
            Task<AuthResult> loginResult = firebaseAuth.signInWithEmailAndPassword(email, password);

            loginResult.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    if (dataSnapshot != null) {
                        if (!dataSnapshot.hasChild("users/" + uid)) {
                            //create the record and insert it into Firebase
                            System.out.println("Created user account in firebase");
                            User newUser = new User(currentUser.getUsername(), activity.getApplicationContext());
                            firebaseDatabaseRef.child("users/" + uid + "/username").setValue(newUser.username);
                            firebaseDatabaseRef.child("users/" + uid + "/highestStarIconEarned").setValue(newUser.highestStarIconEarned);
                            firebaseDatabaseRef.child("users/" + uid + "/starIconName").setValue(newUser.starIconName);
                            firebaseDatabaseRef.child("users/" + uid + "/badgeList").setValue(newUser.badgeList);

                            //firebaseDatabaseRef.child("users/" + uid).setValue(new User(currentUser.getUsername(), activity.getApplicationContext()));
                        } else {
                            System.out.println("User logged into existing account, no data was changed");
                        }
                        System.out.println("Login handled: User ID: " + uid);
                        currentUser.loadUserMetaData(uid);
                        activity.onSuccessfulLogin(loginEmail, loginPassword, firebaseAuth.getCurrentUser().getUid());
                    } else {
                        //do nothing, need user to try again
                    }
                }
            });

            loginResult.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    activity.onUnsuccessfulLogin(e);
                }
            });
        }
        return firebaseWasLoaded;
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

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
    long getBestSpinScore() {
        String userID = currentUser.getUserId();
        if (dataSnapshot.hasChild("SpinScores/" + userID + "/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("SpinScores/" + userID + "/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    long getBestChuckScore() {
        String userID = currentUser.getUserId();
        if (dataSnapshot.hasChild("ChuckScores/" + userID + "/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("ChuckScores/" + userID + "/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    long getBestDropScore() {
        String userID = currentUser.getUserId();
        if (dataSnapshot.hasChild("DropScores/" + userID + "/score")) {
            DataSnapshot yourScoreSnapshot = dataSnapshot.child("DropScores/" + userID + "/score");
            return Long.parseLong(yourScoreSnapshot.getValue().toString());
        }
        return 0;
    }

    ArrayList<Badge> getBadges() {
        Log.d("tag", "GETTING USERS BADGES##################");
        String userID = currentUser.getUserId();
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
    void updateBestScore(long score, double latitude, double longitude, String competeLabelString) {
        String userID = currentUser.getUserId();
        DatabaseReference ref = firebaseDatabaseRef.child(competeLabelString + "/" + userID );

        //priority set as inverse of the score, should order entries automatically
        ref.setPriority(score);

        addScoreToLeaderboard(score, latitude, longitude, ref);
    }

    protected void addFakeChuckScoresToLeaderboard(String userID, int score) {
        //String userID = "1";
        //String username = "bob";
        //int score = 600;

        int latitude = 0;
        double longitude = 0;
        firebaseDatabaseRef.child("ChuckScores/" + userID).setValue(new CompeteRecord(currentUser.getUsername(), score, latitude, longitude), score);
    }

    //does a sorted insert of the users score into the list of user scores. List is sorted so that retrieval for leaderboard is easier
    private void addScoreToLeaderboard(long score, double latitude, double longitude, DatabaseReference firebaseDatabaseReferenceSpot) {
        String userID = currentUser.getUserId();
        firebaseDatabaseReferenceSpot.child("username").setValue(currentUser.getUsername());
        firebaseDatabaseReferenceSpot.child("score").setValue(score);
        firebaseDatabaseReferenceSpot.child("latitude").setValue(latitude);
        firebaseDatabaseReferenceSpot.child("longitude").setValue(longitude);
    }

    void unlockBadge(String badgeName) {
        String userID = currentUser.getUserId();
        for (int i = 0; i < 11; i++) {
            if (dataSnapshot.hasChild("users/" + userID + "/badgeList/"+i) && dataSnapshot.child("users/" + userID + "/badgeList/"+i+"/name").getValue().equals(badgeName)) {
                DateFormat df = new SimpleDateFormat("MM/dd/yy");
                Date dateobj = new Date();
                firebaseDatabaseRef.child("users/" + userID + "/badgeList/" + i + "/unlockDate").setValue(df.format(dateobj));
                i = 20; //end this loop
            }
        }
    }

    boolean hasBadge(String badgeName) {
        boolean hasBadge = false;
        String userID = currentUser.getUserId();
        if (dataSnapshot.hasChild("users/" + userID+"/badgeList")) {
            DataSnapshot usersSnapshot = dataSnapshot.child("users/" + userID+"/badgeList");
            for (DataSnapshot badgeSnapshot: usersSnapshot.getChildren()) {
                if (badgeSnapshot.child("name").getValue().toString().equals(badgeName)) {
                    String unlockDate = badgeSnapshot.child("unlockDate").getValue().toString();
                    if (!unlockDate.equals("")) {
                        hasBadge = true;
                        break;
                    }
                }
            }
        }
        return hasBadge;
    }

    public String getStarStatusOfUser(String username) {
        String iconName = "none";
        if (dataSnapshot != null) {
            for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()) {
                if (userSnapshot.child("username").getValue().equals(username)) {
                    iconName = userSnapshot.child("starIconName").getValue().toString();
                    break;
                }
            }
        }
        return iconName;
    }

    public String getHighestStarStatusOfUser(String username) {
        String iconName = "none";
        if (dataSnapshot != null) {
            for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()) {
                if (userSnapshot.child("username").getValue().equals(username)) {
                    iconName = userSnapshot.child("highestStarIconEarned").getValue().toString();
                    break;
                }
            }
        }
        return iconName;
    }

    public boolean hasUnlockedChangingUsername() {
        String starStatus = getHighestStarStatusOfUser(currentUser.getUsername());
        return (starStatus.equals("gold") || starStatus.equals("shooting"));
    }

    public boolean hasUnlockedSpecialCharactersInUsername() {
        String starStatus = getHighestStarStatusOfUser(currentUser.getUsername());
        return (starStatus.equals("shooting"));
    }

    public void updateStarStatusOfUser(String starIconName) {
        String iconName = "none";
        String userID = currentUser.getUserId();

        firebaseDatabaseRef.child("users/" + userID + "/starIconName").setValue(starIconName);
    }

    public void updateHighestStarEarnedOfUser(String starIconName) {
        String iconName = "none";
        String userID = currentUser.getUserId();

        firebaseDatabaseRef.child("users/" + userID + "/highestStarIconEarned").setValue(starIconName);
    }

    String getPublicKey() {
        return dataSnapshot.child("PublicKey").getValue().toString();
    }

    private void updateLeaderboard() {
        Query top100Chuck = firebaseDatabaseRef.child("ChuckScores").orderByPriority();//.limitToLast(100);
        Query top100Spin = firebaseDatabaseRef.child("SpinScores").orderByPriority();//.limitToLast(100);
        Query top100Drop = firebaseDatabaseRef.child("DropScores").orderByPriority();//.limitToLast(100);

        //take one look at the data, pass it to the current user, and then throw it away
        top100Chuck.addListenerForSingleValueEvent(chuckLeaderboardValueEventListener);
        top100Spin.addListenerForSingleValueEvent(spinLeaderboardValueEventListener);
        top100Drop.addListenerForSingleValueEvent(dropLeaderboardValueEventListener);
    }

    int getPercentOfUsersEarnedBadge(String badgeName) {
        double numberUsersEarned = 0;
        int numberUsers = 0;

        for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()) {
            numberUsers++;
            for (DataSnapshot badgeSnapshot: userSnapshot.child("badgeList").getChildren()) {
                //check if user has unlocked this badge
                if (badgeSnapshot.child("name").getValue().toString().equals(badgeName)) {
                    String unlockDate = badgeSnapshot.child("unlockDate").getValue().toString();
                    if (!unlockDate.equals("")) {
                        numberUsersEarned++;
                        break;
                    }
                }
            }
        }

        if (numberUsers == 0) {
            return 0;
        }

        return (int) (100.0*numberUsersEarned / numberUsers);
    }

    private final ValueEventListener chuckLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> chuckRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                double latitude = (double) subSnapshot.child("latitude").getValue();
                double longitude = (double) subSnapshot.child("longitude").getValue();
                String username = subSnapshot.child("username").getValue().toString();
                chuckRecords.add(new CompeteRecord(username, score, latitude, longitude));
            }

            currentUser.updateGlobalChuckLeaderboard(chuckRecords);
        }

        @Override
        public void onCancelled(DatabaseError error) {
        }
    };

    private final ValueEventListener spinLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> spinRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                //firebase saves 0's as longs, so you have to check if it is a long and convert it to a double if appropriate
                double latitude = MiscHelperMethods.getDoubleValue(subSnapshot.child("latitude").getValue());
                double longitude = MiscHelperMethods.getDoubleValue(subSnapshot.child("longitude").getValue());
                String username = subSnapshot.child("username").getValue().toString();
                spinRecords.add(new CompeteRecord(username, score, latitude, longitude));
            }

            currentUser.updateGlobalSpinLeaderboard(spinRecords);
        }

        @Override
        public void onCancelled(DatabaseError error) {
        }
    };

    private final ValueEventListener dropLeaderboardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot querySnapshot) {
            ArrayList<CompeteRecord> dropRecords = new ArrayList<>();
            for (DataSnapshot subSnapshot : querySnapshot.getChildren()) {
                long score = (long) subSnapshot.child("score").getValue();
                double latitude = (double) subSnapshot.child("latitude").getValue();
                double longitude = (double) subSnapshot.child("longitude").getValue();
                String username = subSnapshot.child("username").getValue().toString();
                dropRecords.add(new CompeteRecord(username, score, latitude, longitude));
            }

            currentUser.updateGlobalDropLeaderboard(dropRecords);
        }

        @Override
        public void onCancelled(DatabaseError error) {
        }
    };

    public void changePassword(String newPassword, final ChangePasswordFragment changePasswordFragment) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            changePasswordFragment.onSuccessfulPasswordChange();
                        } else {
                            changePasswordFragment.onUnsuccessfulPasswordChange(task.getException());
                        }
                    }
                });
    }

    public void resetPassword(String loginEmail, final ForgotPasswordActivity forgotPasswordActivity) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(loginEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            forgotPasswordActivity.onPasswordSuccessfullyReset();
                        } else {
                            forgotPasswordActivity.onPasswordUnsuccessfullyReset(task.getException());
                        }
                    }
                });
    }
}