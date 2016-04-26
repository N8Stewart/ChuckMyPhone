package com.ohiostate.chuckmyphone.chuckmyphone;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Joao Pedro on 4/2/2016.
 *
 * This class is intended to keep track of fragments that were opened, using their tags
 * These tags are used to set the title in the support bar
 * There are also maps to help with other navigational issues
 */
public class NavigationHelper {

    private final static NavigationHelper ourInstance = new NavigationHelper();

    public static NavigationHelper getInstance(){ return ourInstance;}

    private static final int numberOfSubMenuItems = 3;

    // variable to map fragment tags to integers:
    // - used to highlight the menu option chosen
    // - used to translate tags into fragments
    private final Map<String, Integer> fragmentTagsToMenuID;

    // variable to map fragment tags to ids used to prevent users from going to the same fragment they are in
    private final Map<String, Integer> fragmentTagsToID;

    // list holding fragment tags to keep the order of navigation
    private final LinkedList<String> fragmentTags;

    private NavigationHelper(){
        fragmentTags = new LinkedList<>();
        fragmentTagsToMenuID = new HashMap<>(8);
        fragmentTagsToID = new HashMap<>(8);
        fillMaps();
    }

    private void fillMaps(){
        // method to fill maps with values
        fragmentTagsToMenuID.put("'s Profile", 0);
        fragmentTagsToMenuID.put("Leaderboards", 1);
        fragmentTagsToMenuID.put("Chuck My Phone", 3);
        fragmentTagsToMenuID.put("Drop My Phone", 4);
        fragmentTagsToMenuID.put("Spin My Phone", 2);
        fragmentTagsToMenuID.put("Settings", 5);
        fragmentTagsToMenuID.put("About", 6);
        fragmentTagsToMenuID.put("Change Password", 7);
        fragmentTagsToMenuID.put("Tips", 8);
        fragmentTagsToID.put("Chuck My Phone", R.id.menu_hamburger_item_chuck);
        fragmentTagsToID.put("Drop My Phone", R.id.menu_hamburger_item_drop);
        fragmentTagsToID.put("Spin My Phone", R.id.menu_hamburger_item_spin);
        fragmentTagsToID.put("'s Profile", R.id.menu_hamburger_item_profile);
        fragmentTagsToID.put("Leaderboards", R.id.menu_hamburger_item_leaderboards);
        fragmentTagsToID.put("Change Password", R.id.menu_dot_item_change_password);
        fragmentTagsToID.put("About", R.id.menu_dot_item_about);
        fragmentTagsToID.put("Settings", R.id.menu_dot_item_settings);
        fragmentTagsToID.put("Tips", R.id.menu_dot_item_tips);
    }

    public static int translateMenuIDToSubMenuID(int id){
        return id%numberOfSubMenuItems;
    }

    public Fragment translateTagToFragment(String tag){
        // method to return an instance of the fragment related to the given tag
        int id = fragmentTagsToMenuID.get(tag);

        Fragment resultantFragment = null;

        switch(id){
            case 0:
                resultantFragment = ProfileFragment.newInstance();
                break;
            case 1:
                resultantFragment =LeaderboardsFragment.newInstance();
                break;
            case 2:
                resultantFragment = CompeteSpinFragment.newInstance();
                break;
            case 3:
                resultantFragment = CompeteChuckFragment.newInstance();
                break;
            case 4:
                resultantFragment = CompeteDropFragment.newInstance();
                break;
            case 5:
                resultantFragment = SettingsFragment.newInstance();
                break;
            case 6:
                resultantFragment = AboutFragment.newInstance();
                break;
            case 7:
                resultantFragment = ChangePasswordFragment.newInstance();
                break;
            case 8:
                resultantFragment = TipsFragment.newInstance();
                break;
            default:
                break;
        }
        return resultantFragment;
    }

    public String previousFragmentTag(){
        // method to return the last but one tag in the list
        if(noFragmentsLeft()) return null;
        fragmentTags.removeLast();
        if(noFragmentsLeft()) return null;
        return fragmentTags.getLast();
    }

    public boolean noFragmentsLeft(){
        return fragmentTags.isEmpty();
    }

    public String addNextFragmentTag (String nextFragmentTag){
        // method to add a tag into the list
        if(fragmentTags.contains(nextFragmentTag)) fragmentTags.remove(nextFragmentTag);
        fragmentTags.addLast(nextFragmentTag);
        return fragmentTags.getLast();
    }

    public Object lastMenuChoice(){
        // method to retrieve the menu id of the last fragment in the list
        if(noFragmentsLeft()) return null;
        return fragmentTagsToMenuID.get(fragmentTags.getLast());
    }

    public Object lastFragmentIDChoice(){
        // method to retrieve the id of the last fragment in the list
        if(noFragmentsLeft()) return null;
        return fragmentTagsToID.get(fragmentTags.getLast());
    }
}