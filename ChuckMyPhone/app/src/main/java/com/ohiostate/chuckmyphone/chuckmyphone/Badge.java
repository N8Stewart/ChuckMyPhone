package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 3/25/2016.
 */
public class Badge {
    private String unlockDate;
    private String name;

    public static Map<String, String> badgeNameToDescriptionMap = new HashMap<String, String>();

    public void initializeDescriptionMappings(Context c) {
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_chuck_level_one), "Unlock this by doing your first chuck trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_chuck_level_two), "Unlock this by getting a score of " + BADGE_CHUCK_LEVEL_2_SCORE +" or more in a chuck trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_chuck_level_three), "Unlock this by getting a score of " + BADGE_CHUCK_LEVEL_3_SCORE +" or more in a chuck trial");

        badgeNameToDescriptionMap.put(c.getString(R.string.badge_drop_level_one), "Unlock this by doing your first drop trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_drop_level_two), "Unlock this by getting a score of " + BADGE_DROP_LEVEL_2_SCORE +" or more in a drop trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_drop_level_three), "Unlock this by getting a score of " + BADGE_DROP_LEVEL_3_SCORE +" or more in a drop trial");

        badgeNameToDescriptionMap.put(c.getString(R.string.badge_spin_level_one), "Unlock this by doing your first spin trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_spin_level_two), "Unlock this by getting a score of " + BADGE_SPIN_LEVEL_2_SCORE +" or more in a spin trial");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_spin_level_three), "Unlock this by getting a score of " + BADGE_SPIN_LEVEL_3_SCORE + " or more in a spin trial");

        badgeNameToDescriptionMap.put(c.getString(R.string.badge_one_percent), "Unlock this by getting in the top 10 for any leaderboard category (Minimum of 100 participants)");
        badgeNameToDescriptionMap.put(c.getString(R.string.badge_hidden), "Unlocked by viewing the credits, Thank you so much for playing our game! \n    - Tim, Nate, Joao");
    }

    private static final int BADGE_DROP_LEVEL_1_SCORE = 1;
    private static final int BADGE_DROP_LEVEL_2_SCORE = 600;
    private static final int BADGE_DROP_LEVEL_3_SCORE = 1000;

    private static final int BADGE_CHUCK_LEVEL_1_SCORE = 1;
    private static final int BADGE_CHUCK_LEVEL_2_SCORE = 3000;
    private static final int BADGE_CHUCK_LEVEL_3_SCORE = 5000;

    private static final int BADGE_SPIN_LEVEL_1_SCORE = 1;
    private static final int BADGE_SPIN_LEVEL_2_SCORE = 2500;
    private static final int BADGE_SPIN_LEVEL_3_SCORE = 4000;

    public Badge(String name) {
        unlockDate = "";
        this.name = name;
    }

    public Badge(String name, String unlockDate) {
        this.unlockDate = unlockDate;
        this.name = name;
    }

    public String getUnlockDate() {
        return unlockDate;
    }

    public String getName() {
        return name;
    }

    public String UnlockedDescription() {
        return badgeNameToDescriptionMap.get(name);
    }

    public String LockedDescription(Context c) {
        if (this.name.equals(c.getString(R.string.badge_hidden))) {
            return "This is a hidden badge, no hints at how to unlock";
        }
        return badgeNameToDescriptionMap.get(name);
    }

    public boolean unlocked() {
        return (!unlockDate.equals(""));
    }

    public static int BADGE_DROP_LEVEL_1_SCORE() {
        return BADGE_DROP_LEVEL_1_SCORE;
    }
    public static int BADGE_DROP_LEVEL_2_SCORE() {
        return BADGE_DROP_LEVEL_2_SCORE;
    }
    public static int BADGE_DROP_LEVEL_3_SCORE() {
        return BADGE_DROP_LEVEL_3_SCORE;
    }
    public static int BADGE_CHUCK_LEVEL_1_SCORE() {
        return BADGE_CHUCK_LEVEL_1_SCORE;
    }
    public static int BADGE_CHUCK_LEVEL_2_SCORE() {
        return BADGE_CHUCK_LEVEL_2_SCORE;
    }
    public static int BADGE_CHUCK_LEVEL_3_SCORE() {
        return BADGE_CHUCK_LEVEL_3_SCORE;
    }
    public static int BADGE_SPIN_LEVEL_1_SCORE() {
        return BADGE_SPIN_LEVEL_1_SCORE;
    }
    public static int BADGE_SPIN_LEVEL_2_SCORE() {
        return BADGE_SPIN_LEVEL_2_SCORE;
    }
    public static int BADGE_SPIN_LEVEL_3_SCORE() {
        return BADGE_SPIN_LEVEL_3_SCORE;
    }
}
