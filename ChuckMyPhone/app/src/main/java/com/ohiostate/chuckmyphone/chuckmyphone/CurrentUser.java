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

    private ArrayList<FirebaseHelper.CompeteRecord> chuckLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> spinLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> dropLeaderboardGlobal;

    //load userID, high scores
    public void loadUserMetaData(String userId, String provider) {
        this.userId = userId;
        this.provider = provider;
        if (this.username == null) {
            this.username = "USERNAME NOT ASSIGNED";
        }
        isLoaded = true;

        tutorialMessagesEnabled = true;
        soundEnabled = false;

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

    public void unloadData() {
        isLoaded = false;
    }

    //updates high score in firebase
    public void updateChuckScore(long score) {
        FirebaseHelper.getInstance().updateBestChuckScore(score, 0.0, 0.0);
    }

    //updates high score in firebase
    public void updateDropScore(long score) {
        FirebaseHelper.getInstance().updateBestDropScore(score, 0.0, 0.0);
    }

    //updates high score in firebase
    public void updateSpinScore(long score) {
        FirebaseHelper.getInstance().updateBestSpinScore(score, 0.0, 0.0);
    }

    public void updateTutorialMessagesEnabled(boolean value) {
        tutorialMessagesEnabled = value;
    }

    public boolean getTutorialMessagesEnabled() {
        return tutorialMessagesEnabled;
    }

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
}
