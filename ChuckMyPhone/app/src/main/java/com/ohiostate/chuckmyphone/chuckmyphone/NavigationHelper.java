package com.ohiostate.chuckmyphone.chuckmyphone;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Joao Pedro on 4/2/2016.
 */
public class NavigationHelper {

    private static NavigationHelper ourInstance = new NavigationHelper();

    public static NavigationHelper getInstance(){ return ourInstance;}

    private Map<String, Integer> fragmentTagsToMenuID;
    private Map<String, Integer> fragmentTagsToID;
    private LinkedList<String> fragmentTags;

    private NavigationHelper(){
        fragmentTags = new LinkedList<>();
        fragmentTagsToMenuID = new HashMap<>(8);
        fragmentTagsToID = new HashMap<>(8);
        fillMaps();
    }

    private void fillMaps(){
        fragmentTagsToMenuID.put("'s Profile", 0);
        fragmentTagsToMenuID.put("Leaderboards", 1);
        fragmentTagsToMenuID.put("Chuck My Phone", 3);
        fragmentTagsToMenuID.put("Drop My Phone", 4);
        fragmentTagsToMenuID.put("Spin My Phone", 2);
        fragmentTagsToMenuID.put("Settings", 5);
        fragmentTagsToMenuID.put("About", 6);
        fragmentTagsToMenuID.put("Change Password", 7);
        fragmentTagsToID.put("Chuck My Phone", R.id.menu_hamburger_item_chuck);
        fragmentTagsToID.put("Drop My Phone", R.id.menu_hamburger_item_drop);
        fragmentTagsToID.put("Spin My Phone", R.id.menu_hamburger_item_spin);
        fragmentTagsToID.put("'s Profile", R.id.menu_hamburger_item_profile);
        fragmentTagsToID.put("Leaderboards", R.id.menu_hamburger_item_leaderboards);
        fragmentTagsToID.put("Change Password", R.id.menu_dot_item_change_password);
        fragmentTagsToID.put("About", R.id.menu_dot_item_about);
        fragmentTagsToID.put("Settings", R.id.menu_dot_item_settings);
    }

    public Fragment translateTagToFragment(String tag){
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
            default:
                break;
        }
        return resultantFragment;
    }

    public String previousFragmentTag(){
        if(noFragmentsLeft()) return null;
        fragmentTags.removeLast();
        if(noFragmentsLeft()) return null;
        return fragmentTags.getLast();
    }

    public boolean noFragmentsLeft(){
        return fragmentTags.isEmpty();
    }

    public String addNextFragmentTag (String nextFragmentTag){
        if(fragmentTags.contains(nextFragmentTag)) fragmentTags.remove(nextFragmentTag);
        fragmentTags.addLast(nextFragmentTag);
        return fragmentTags.getLast();
    }

    public Object lastMenuChoice(){
        if(noFragmentsLeft()) return null;
        return fragmentTagsToMenuID.get(fragmentTags.getLast());
    }

    public Object lastFragmentIDChoice(){
        if(noFragmentsLeft()) return null;
        return fragmentTagsToID.get(fragmentTags.getLast());
    }
}