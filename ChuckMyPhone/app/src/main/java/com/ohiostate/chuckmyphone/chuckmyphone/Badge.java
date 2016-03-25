package com.ohiostate.chuckmyphone.chuckmyphone;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 3/25/2016.
 */
public class Badge {
    private boolean unlocked;
    private String unlockDate;
    private String name;

    private static Map<String, String> badgeNameToDescriptionMap = new HashMap<String, String>();

    public void initializeDescriptionMappings() {
        badgeNameToDescriptionMap.put("Noodle Arm", "Unlock this by doing your first chuck trial");
        badgeNameToDescriptionMap.put("Flop Drop", "Unlock this by doing your first drop trial");
        badgeNameToDescriptionMap.put("Inelastic Gymnastics", "Unlock this by doing your first spin trial");

        badgeNameToDescriptionMap.put("Rocket Arm", "Unlock this by getting a score of XXX or more in a chuck trial");
        badgeNameToDescriptionMap.put("Countertop Drop", "Unlock this by getting a score of XXX or more in a drop trial");
        badgeNameToDescriptionMap.put("Enthusiastic Gymnastics", "Unlock this by getting a score of XXX or more in a spin trial");

        badgeNameToDescriptionMap.put("Faster Than Light", "Unlock this by getting a score of XXX or more in a chuck trial");
        badgeNameToDescriptionMap.put("Atmospheric Drop", "Unlock this by getting a score of XXX or more in a drop trial");
        badgeNameToDescriptionMap.put("Bombastic Gymnastics", "Unlock this by getting a score of XXX or more in a spin trial");

        badgeNameToDescriptionMap.put("The One Percent", "Unlock this by getting in the top 10 for any leaderboard category (Minimum of 100 participants)");
        badgeNameToDescriptionMap.put("The Kindness Badge", "Unlocked by viewing the credits, Thank you so much for playing our game!        - Tim, Nate, Joao");
    }

    public Badge(String name) {
        this.unlocked = false;
        unlockDate = "";
        this.name = name;
    }

    public Badge(String name, String unlockDate, boolean isUnlocked) {
        this.unlocked = isUnlocked;
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
        initializeDescriptionMappings();

        return badgeNameToDescriptionMap.get(name);
    }

    public String LockedDescription() {
        initializeDescriptionMappings();

        if (this.name.equals("The Kindness Badge")) {
            return "This is a hidden badge, no hints at how to unlock";
        }
        return badgeNameToDescriptionMap.get(name);
    }

    public boolean isUnlocked() {
        return unlocked;
    }

}
