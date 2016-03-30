package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joao Pedro on 3/11/2016.
 */

public class SharedPreferencesHelper {

    private static final String PREFS_USER = "chuckmyphone.PREFS_USER";
    private static final String MSG_KEY = "The user does not have this key";

    public static class Keys {
        public static final String keyUsername = "username";
        public static final String keyPassword = "password";
        public static final String keyEmail = "email";
        public static final String keyTutorial = "tutorial";
        public static final String keySound = "sound";
    }

    public SharedPreferencesHelper(){}

    private static void setStringValue(Context context, String key, String value){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedData.edit();
        editor.putString(key, value).commit();
    }

    private static void setBooleanValue(Context context, String key, boolean value){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedData.edit();
        editor.putBoolean(key, value).commit();
    }

    public static void setEmail(Context context, String email){
        setStringValue(context, Keys.keyEmail, email);
    }

    public static void setUsername(Context context, String username){
        setStringValue(context, Keys.keyUsername, username);
    }

    public static void setPassword(Context context, String password){
        setStringValue(context, Keys.keyPassword, password);
    }

    public static void setSoundEnabled(Context context, boolean value){
        setBooleanValue(context, Keys.keySound, value);
    }

    public static void setTutorialMessages(Context context, boolean tutorialMessages) {
        setBooleanValue(context, Keys.keyTutorial, tutorialMessages);
    }

    private static String getStringValue(Context context, String key){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.getString(key, MSG_KEY);
    }

    private static boolean getBooleanValue(Context context, String key, boolean defValue){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.getBoolean(key, defValue);
    }

    public static String getUsername(Context context){
        return getStringValue(context, Keys.keyUsername);
    }

    public static String getPassword(Context context){
        return getStringValue(context, Keys.keyPassword);
    }

    public static boolean getSoundEnabled(Context context){
        return getBooleanValue(context, Keys.keySound, false);
    }

    public static String getEmail(Context context) {
        return getStringValue(context, Keys.keyEmail);
    }

    public static boolean getTutorialMessages(Context context) {
        return getBooleanValue(context, Keys.keyTutorial, true);
    }

    protected static boolean hasSharedData(Context context){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.contains(Keys.keyEmail);
    }

    protected static boolean clearSharedData(Context context){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedData.edit();
        return editor.clear().commit();
    }

    public static void createSharedPreferencesData(Context context, String email, String password, String username) {
        setEmail(context, email);
        setPassword(context, password);
        setUsername(context, username);
        setSoundEnabled(context, false);
        setTutorialMessages(context, true);
    }

    public static void setSharedPreferencesData(Context context, String email, String password, String username) {
        setEmail(context, email);
        setPassword(context, password);
        setUsername(context, username);
    }
}