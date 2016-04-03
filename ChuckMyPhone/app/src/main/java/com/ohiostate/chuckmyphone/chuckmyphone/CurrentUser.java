package com.ohiostate.chuckmyphone.chuckmyphone;

import java.util.ArrayList;

/**
 * Created by Tim on 3/13/2016.
 * Used as a singleton to store important run time data such as high scores, settings, and userID. Interfaces with Firebase helper for gathering data from firebase and interfaces with compete fragments for
 * getting high scores and setting high scores through firebase
 */
public class CurrentUser {
    private static CurrentUser ourInstance = new CurrentUser();

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    private CurrentUser() {
        chuckLeaderboardGlobal = new ArrayList<FirebaseHelper.CompeteRecord>();
        spinLeaderboardGlobal = new ArrayList<FirebaseHelper.CompeteRecord>();
        dropLeaderboardGlobal = new ArrayList<FirebaseHelper.CompeteRecord>();
    }

    private boolean isLoaded = false;
    private String userId;
    private String username;
    private String provider;

    private long chuckScore;
    private long spinScore;
    private long dropScore;

    private boolean tutorialMessagesEnabled;
    private boolean soundEnabled;
    private boolean badgeUnlockNotificationsEnabled;

    private ArrayList<FirebaseHelper.CompeteRecord> chuckLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> spinLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> dropLeaderboardGlobal;

    private ArrayList<Badge> badgeList;

    //load userID, high scores
    public void loadUserMetaData(String userId, String provider) {
        this.userId = userId;
        this.provider = provider;
        if (this.username == null) {
            this.username = "USERNAME NOT ASSIGNED";
        }
        isLoaded = true;

        soundEnabled = false;
        tutorialMessagesEnabled = true;
        badgeUnlockNotificationsEnabled = true;

        chuckScore = 0;
        spinScore = 0;
        dropScore =  0;

        loadUserScoreData();
    }

    public void loadUserScoreData() {
        chuckScore = FirebaseHelper.getInstance().getBestChuckScore();
        spinScore = FirebaseHelper.getInstance().getBestSpinScore();
        dropScore =  FirebaseHelper.getInstance().getBestDropScore();
    }

    public void loadUserBadgeData() {
        badgeList = FirebaseHelper.getInstance().getBadges();
    }

    public void unloadData() {
        isLoaded = false;
    }

    //updates high score in firebase
    public void updateChuckScore(long score, double latitude, double longitude) {
        FirebaseHelper.getInstance().updateBestChuckScore(score, latitude, longitude);
    }

    //updates high score in firebase
    public void updateDropScore(long score, double latitude, double longitude) {
        FirebaseHelper.getInstance().updateBestDropScore(score, latitude, longitude);
    }

    //updates high score in firebase
    public void updateSpinScore(long score, double latitude, double longitude) {
        FirebaseHelper.getInstance().updateBestSpinScore(score, latitude, longitude);
    }

    public void updateTutorialMessagesEnabled(boolean value) {
        tutorialMessagesEnabled = value;
    }

    public boolean getTutorialMessagesEnabled() {
        return tutorialMessagesEnabled;
    }

    public void updateSoundEnabled(boolean value) {
        soundEnabled = value;
    }

    public void updateBadgeNotificationsEnabled(boolean value) { badgeUnlockNotificationsEnabled = value; }

    public boolean getSoundEnabled() {
        return soundEnabled;
    }

    public boolean getBadgeNotificationsEnabled() { return badgeUnlockNotificationsEnabled; }

    public String getUserId() {
        return this.userId;
    }

    public boolean isDataLoaded() {
        return isLoaded;
    }

    public long getChuckScore() {
        return chuckScore;
    }

    public long getSpinScore() {
        return spinScore;
    }

    public long getDropScore() {
        return dropScore;
    }

    public ArrayList<Badge> getBadgeList() {
        return badgeList;
    }

    protected void assignUsername(String username) {
        this.username = username;
    }

    protected String getUsername() {
        return this.username;
    }

    protected void updateChuckLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        chuckLeaderboardGlobal = newLeaderboard; //pass by reference problems?
    }

    protected void updateSpinLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        spinLeaderboardGlobal = newLeaderboard; //pass by reference problems?
    }

    protected void updateDropLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        dropLeaderboardGlobal = newLeaderboard; //pass by reference problems?
    }

    protected ArrayList<FirebaseHelper.CompeteRecord> getChuckLeaderboard() {
        return chuckLeaderboardGlobal;
    }

    protected ArrayList<FirebaseHelper.CompeteRecord> getSpinLeaderboard() {
        return spinLeaderboardGlobal;
    }

    protected ArrayList<FirebaseHelper.CompeteRecord> getDropLeaderboard() {
        return dropLeaderboardGlobal;
    }

    public void loadSettings(boolean tutorialEnabled, boolean soundEnabled, boolean badgeUnlockNotificationsEnabled) {
        updateTutorialMessagesEnabled(tutorialEnabled);
        updateSoundEnabled(soundEnabled);
        updateBadgeNotificationsEnabled(badgeUnlockNotificationsEnabled);
    }
}