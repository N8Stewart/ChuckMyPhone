package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joao Pedro on 3/11/2016.
 */
public class SharedPreferencesHelper {

    private static final String PREFS_USER = "com.ohiostate.chuckmyphone.chuckmyphone.PREFS_USER";
    private static final String MSG_KEY = "The user does not have this key";

    private SharedPreferences sharedData;

    public SharedPreferencesHelper(Context context){
        sharedData = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
    }

    private void setStringValue(String key, String value){
        sharedData.edit().putString(key, value).commit();
    }

    private void setBooleanValue(String key, boolean value){
        sharedData.edit().putBoolean(key, value).commit();
    }

    public void setEmail(String email){
        setStringValue("email", email);
    }

    public void setUsername(String username){
        setStringValue("username", username);
    }

    public void setPassword(String password){
        setStringValue("password", password);
    }

    public void setBadges(String badges){
        setStringValue("badges", badges);
    }

    public void setBestDrop(String dropScore){
        setStringValue("drop", dropScore);
    }

    public void setBestSpin(String spinScore){
        setStringValue("spin", spinScore);
    }

    public void setBestChuck(String chuckScore){
        setStringValue("drop", chuckScore);
    }

    public void setSoundEnabled(boolean value){
        setBooleanValue("sound", value);
    }

    public void setTutorialMessages(boolean tutorialMessages) {
        setBooleanValue("tutorial", tutorialMessages);
    }

    private String getStringValue(String key){
        return sharedData.getString(key, MSG_KEY);
    }

    private boolean getBooleanValue(String key, boolean defValue){
        return sharedData.getBoolean(key, defValue);
    }

    public String getUsername(){
        return getStringValue("username");
    }

    public String getPassword(){
        return getStringValue("password");
    }

    public String getBadges(){
        return getStringValue("badges");
    }

    public String getBestDrop(){
        return getStringValue("drop");
    }

    public String getBestSpin(){
        return getStringValue("spin");
    }

    public String getBestChuck(){
        return getStringValue("chuck");
    }

    public boolean getSoundEnabled(){
        return getBooleanValue("sound", true);
    }

    public String getEmail() {
        return getStringValue("email");
    }

    public boolean getTutorialMessages() {
        return getBooleanValue("tutorial", false);
    }

    protected boolean hasSharedData(){
        return sharedData.contains("email");
    }

    protected boolean clearSharedData(){
        return sharedData.edit().clear().commit();
    }

    public void createSharedPreferencesData(String email, String password, String username) {
        //TODO get saved data from Firebase for badge, score info
        // recreate data after new user activity
        this.setEmail(email);
        this.setPassword(password);
        this.setUsername(username);
        this.setBadges("00000000000");
        this.setBestDrop("0");
        this.setBestSpin("0");
        this.setBestChuck("0");
        this.setSoundEnabled(false);
        this.setTutorialMessages(true);
    }

    public void reCreateSharedPreferencesData(String email, String password) {
        // recreate data after login activity
        this.setEmail(email);
        this.setPassword(password);
        this.setUsername("");
        this.setBadges("00000000000");
        this.setBestDrop("0");
        this.setBestSpin("0");
        this.setBestChuck("0");
        this.setSoundEnabled(false);
        this.setTutorialMessages(true);
    }
}