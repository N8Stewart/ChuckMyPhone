package com.ohiostate.chuckmyphone.chuckmyphone;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tim on 3/13/2016.
 * Used as a singleton to store important run time data such as high scores, settings, and userID. Interfaces with Firebase helper for gathering data from firebase and interfaces with compete fragments for
 * getting high scores and setting high scores through firebase
 */
public class CurrentUser {
    private final static CurrentUser ourInstance = new CurrentUser();

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    private CurrentUser() {
        chuckLeaderboardGlobal = new ArrayList<>();
        spinLeaderboardGlobal = new ArrayList<>();
        dropLeaderboardGlobal = new ArrayList<>();
    }

    private String userId;
    private String username;

    private long chuckScore;
    private long spinScore;
    private long dropScore;

    private double latitude;
    private double longitude;

    private boolean gpsEnabled;
    private boolean locationUpdated;
    private boolean sawBoardOnceWithoutGps;
    private boolean sawBoardOnceWithGps;
    private boolean playedOnceWithoutGps;
    private boolean playedOnceWithGps;
    private boolean tutorialMessagesEnabled;
    private boolean soundEnabled;
    private boolean badgeUnlockNotificationsEnabled;
    private boolean goofySoundEnabled;

    private ArrayList<FirebaseHelper.CompeteRecord> chuckLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> spinLeaderboardGlobal;
    private ArrayList<FirebaseHelper.CompeteRecord> dropLeaderboardGlobal;

    private ArrayList<Badge> badgeList;

    //load userID, high scores
    public void loadUserMetaData(String userId) {
        this.userId = userId;
        if (this.username == null) {
            this.username = "USERNAME NOT ASSIGNED";
        }

        soundEnabled = false;
        tutorialMessagesEnabled = true;
        badgeUnlockNotificationsEnabled = true;
        goofySoundEnabled = false;

        chuckScore = 0;
        spinScore = 0;
        dropScore =  0;

        latitude = 0.0;
        longitude = 0.0;

        gpsEnabled = false;
        locationUpdated = false;

        sawBoardOnceWithoutGps = false;
        sawBoardOnceWithGps = false;
        playedOnceWithoutGps = false;
        playedOnceWithGps = false;

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

    //updates high score in firebase
    public void updateChuckScore(long score) {
        FirebaseHelper.getInstance().updateBestScore(score, latitude, longitude, "ChuckScores");
        chuckScore = score;
    }

    //updates high score in firebase
    public void updateDropScore(long score) {
        FirebaseHelper.getInstance().updateBestScore(score, latitude, longitude, "DropScores");
        dropScore = score;
    }

    //updates high score in firebase
    public void updateSpinScore(long score) {
        FirebaseHelper.getInstance().updateBestScore(score, latitude, longitude, "SpinScores");
        spinScore = score;
    }

    public void logout() {
        this.username = "USERNAME NOT ASSIGNED";
        //TODO need to do more here?
    }

    public void updateTutorialMessagesEnabled(boolean value) {
        tutorialMessagesEnabled = value;
    }

    public void updateSoundEnabled(boolean value) {
        soundEnabled = value;
    }

    public void updateBadgeNotificationsEnabled(boolean value) { badgeUnlockNotificationsEnabled = value; }

    public void updateGoofySoundEnabled(boolean value) { goofySoundEnabled = value; }

    private void updateLatitude(double value) {
        latitude = value;
    }

    private void updateLongitude(double value) {
        longitude = value;
    }

    public void updateGPSEnabled(boolean value){
        gpsEnabled = value;
    }

    public void updateLocationUpdated(boolean value){
        locationUpdated = value;
    }

    public boolean getTutorialMessagesEnabled() {
        return tutorialMessagesEnabled;
    }

    public boolean getSoundEnabled() {
        return soundEnabled;
    }

    public boolean getGoofySoundEnabled() {
        return goofySoundEnabled;
    }

    public boolean getBadgeNotificationsEnabled() { return badgeUnlockNotificationsEnabled; }

    public boolean isGPSEnabled(){
        return gpsEnabled;
    }

    public boolean isLocationUpdated(){
        return locationUpdated;
    }

    public String getUserId() {
        return this.userId;
    }

    public void updateSawBoardOnceWithoutGps(){
        sawBoardOnceWithoutGps = true;
    }

    public void updateSawBoardOnceWithGps(){
        sawBoardOnceWithGps = true;
    }

    public boolean sawBoardOnceWithoutGps() {
        return sawBoardOnceWithoutGps;
    }

    public boolean sawBoardOnceWithGps() {
        return sawBoardOnceWithGps;
    }

    public void updatePlayedOnceWithoutGps(){
        playedOnceWithoutGps = true;
    }

    public void updatePlayedOnceWithGps(){
        playedOnceWithGps = true;
    }

    public boolean playedOnceWithoutGps() {
        return playedOnceWithoutGps;
    }

    public boolean playedOnceWithGps() {
        return playedOnceWithGps;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public ArrayList<Badge> getBadgeList() {
        return badgeList;
    }

    void assignUsername(String username) {
        this.username = username;
    }

    String getUsername() {
        return this.username;
    }

    void updateGlobalChuckLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        chuckLeaderboardGlobal = newLeaderboard;
        Collections.reverse(chuckLeaderboardGlobal);
    }

    void updateGlobalSpinLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        spinLeaderboardGlobal = newLeaderboard;
        Collections.reverse(spinLeaderboardGlobal);
    }

    void updateGlobalDropLeaderboard(ArrayList<FirebaseHelper.CompeteRecord> newLeaderboard) {
        dropLeaderboardGlobal = newLeaderboard;
        Collections.reverse(dropLeaderboardGlobal);
    }

    ArrayList<FirebaseHelper.CompeteRecord> getChuckLeaderboard() {
        return chuckLeaderboardGlobal;
    }

    ArrayList<FirebaseHelper.CompeteRecord> getSpinLeaderboard() {
        return spinLeaderboardGlobal;
    }

    ArrayList<FirebaseHelper.CompeteRecord> getDropLeaderboard() {
        return dropLeaderboardGlobal;
    }

    public boolean needToUpdateLocation(){
        return !locationUpdated && gpsEnabled;
    }

    public void loadUserSettings(boolean tutorialEnabled, boolean soundEnabled, boolean badgeUnlockNotificationsEnabled, boolean goofySoundEnabled) {
        updateTutorialMessagesEnabled(tutorialEnabled);
        updateSoundEnabled(soundEnabled);
        updateBadgeNotificationsEnabled(badgeUnlockNotificationsEnabled);
        updateGoofySoundEnabled(goofySoundEnabled);
    }

    public void loadUserLocation(double latitude, double longitude) {
        updateLatitude(latitude);
        updateLongitude(longitude);
    }
}