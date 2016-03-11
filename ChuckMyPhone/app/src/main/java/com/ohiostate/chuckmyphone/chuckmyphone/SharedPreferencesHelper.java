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

    public void setNotificationsEnabled(boolean value){
        setBooleanValue("notifications", value);
    }

    public void setImperialSystem(boolean value){
        setBooleanValue("system", value);
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

    public boolean getNotificationsEnabled(){
        return getBooleanValue("notifications", true);
    }

    public boolean getImperialSystem(){
        return getBooleanValue("system", true);
    }

    protected boolean hasSharedData(){
        return sharedData.contains("username");
    }

    protected boolean clearSharedData(){
        return sharedData.edit().clear().commit();
    }
}