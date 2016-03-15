package com.ohiostate.chuckmyphone.chuckmyphone;

/**
 * Created by Tim on 3/13/2016.
 */
public class CurrentUser {
    private static CurrentUser ourInstance = new CurrentUser();

    public static CurrentUser getInstance() {
        return ourInstance;
    }

    private CurrentUser() {
    }

    private boolean isLoaded = false;
    private String userId;
    private String provider;

    private double chuckScore;
    private double spinScore;
    private double dropScore;

    private boolean tutorialMessagesEnabled;
    private boolean soundEnabled;

    public void loadUserMetaData(String userId, String provider) {
        this.userId = userId;
        this.provider = provider;
        isLoaded = true;

        tutorialMessagesEnabled = true;
        soundEnabled = false;

        chuckScore = 0.0;
        spinScore = 0.0;
        dropScore =  0.0;

        loadUserScoreData();
    }

    public void loadUserScoreData() {
        //CompeteChuckFragment competeChuck = new CompeteChuckFragment();
        //competeChuck.receiveBestScore(FirebaseHelper.getInstance().getBestChuckScore());

        //CompeteDropFragment competeDrop = new CompeteDropFragment();
        //competeDrop.receiveBestScore(FirebaseHelper.getInstance().getBestDropScore());

        //CompeteSpinFragment competeSpin = new CompeteSpinFragment();
        //competeSpin.receiveBestScore(FirebaseHelper.getInstance().getBestSpinScore());

        chuckScore = FirebaseHelper.getInstance().getBestChuckScore();
        spinScore = FirebaseHelper.getInstance().getBestSpinScore();
        dropScore =  FirebaseHelper.getInstance().getBestDropScore();
    }

    public void unloadData() {
        isLoaded = false;
    }

    public void updateChuckScore(double score) {
        chuckScore = score;
        FirebaseHelper.getInstance().updateBestChuckScore(truncateDouble(score));
    }

    public void updateDropScore(double score) {
        dropScore = score;
        FirebaseHelper.getInstance().updateBestDropScore(truncateDouble(score));
    }

    public void updateSpinScore(double score) {
        spinScore = score;
        FirebaseHelper.getInstance().updateBestSpinScore(truncateDouble(score));
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

    public double getChuckScore() {
        return chuckScore;
    }

    public double getSpinScore() {
        return spinScore;
    }

    public double getDropScore() {
        return dropScore;
    }

    private double truncateDouble(double score) {
        return Math.floor(score * 1000) / 1000;
    }
}
