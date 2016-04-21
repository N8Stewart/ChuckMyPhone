package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Joao Pedro on 3/11/2016.
 */

class SharedPreferencesHelper {

    private static final String PREFS_USER = "chuckmyphone.PREFS_USER";
    private static final String MSG_KEY = "The user does not have this key";

    public static class Keys {
        public static final String keyUsername = "username";
        public static final String keyPassword = "password";
        public static final String keyEmail = "email";
        public static final String keyTutorial = "tutorial";
        public static final String keySound = "sound";
        public static final String keyBadgeNotifications = "badge";
        public static final String keyGoofySound = "goofy";
        public static final String keyLatitude = "latitude";
        public static final String keyLongitude = "longitude";
    }

    public SharedPreferencesHelper(){}

    private static void setBooleanValue(Context context, String key, boolean value){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedData.edit();
        editor.putBoolean(key, value).commit();
    }

    private static void setStringValue(Context context, String key, String value){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedData.edit();
        editor.putString(key, value).commit();
    }

    private static void setEmail(Context context, String email){
        setStringValue(context, Keys.keyEmail, email);
    }

    public static void setPassword(Context context, String password){
        setStringValue(context, Keys.keyPassword, password);
    }

    private static void setUsername(Context context, String username){
        setStringValue(context, Keys.keyUsername, username);
    }

    public static void setSoundEnabled(Context context, boolean value){
        setBooleanValue(context, Keys.keySound, value);
    }

    public static void setGoofySoundEnabled(Context context, boolean value){
        setBooleanValue(context, Keys.keyGoofySound, value);
    }

    public static void setBadgeNotificationsEnabled(Context context, boolean value){
        setBooleanValue(context, Keys.keyBadgeNotifications, value);
    }

    public static void setTutorialMessages(Context context, boolean tutorialMessages) {
        setBooleanValue(context, Keys.keyTutorial, tutorialMessages);
    }

    public static void setLatitude(Context context, double latitude) {
        setStringValue(context, Keys.keyLatitude, latitude + "");
    }

    public static void setLongitude(Context context, double longitude) {
        setStringValue(context, Keys.keyLongitude, longitude + "");
    }

    private static boolean getBooleanValue(Context context, String key, boolean defValue){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.getBoolean(key, defValue);
    }

    private static String getStringValue(Context context, String key){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.getString(key, MSG_KEY);
    }

    public static String getEmail(Context context) {
        return getStringValue(context, Keys.keyEmail);
    }

    public static String getPassword(Context context){
        return getStringValue(context, Keys.keyPassword);
    }

    public static String getUsername(Context context){
        return getStringValue(context, Keys.keyUsername);
    }

    public static boolean getSoundEnabled(Context context){
        return getBooleanValue(context, Keys.keySound, false);
    }

    public static boolean getGoofySoundEnabled(Context context){
        return getBooleanValue(context, Keys.keyGoofySound, false);
    }

    public static boolean getTutorialMessages(Context context) {
        return getBooleanValue(context, Keys.keyTutorial, true);
    }

    public static boolean getBadgeNotifications(Context context) {
        return getBooleanValue(context, Keys.keyBadgeNotifications, true);
    }

    public static double getLatitude(Context context) {
        return Double.parseDouble(getStringValue(context, Keys.keyLatitude));
    }

    public static double getLongitude(Context context) {
        return Double.parseDouble(getStringValue(context, Keys.keyLongitude));
    }

    static boolean hasSharedData(Context context){
        SharedPreferences sharedData = context.getSharedPreferences(PREFS_USER, Activity.MODE_PRIVATE);
        return sharedData.contains(Keys.keyEmail);
    }

    static boolean clearSharedData(Context context){
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
        setGoofySoundEnabled(context, false);
        setLatitude(context, 0.0);
        setLongitude(context, 0.0);
    }

    public static void setSharedPreferencesData(Context context, String email, String password, String username) {
        setEmail(context, email);
        setPassword(context, password);
        setUsername(context, username);
    }
}