package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Joao Pedro on 4/2/2016.
 */
public class NavigationHelper {

    private static NavigationHelper ourInstance = new NavigationHelper("Chuck My Phone");

    public static NavigationHelper getInstance(){ return ourInstance;}

    private Stack<String> fragmentTags;
    private Map<String, Integer> fragmentTagsToID;
    
    public NavigationHelper(String firstTag){
        fragmentTags = new Stack<>();
        fragmentTags.push(firstTag);
        fragmentTagsToID = new HashMap<>(8);
        fillMap();
    }

    private void fillMap(){
        fragmentTagsToID.put("Chuck My Phone", R.id.menu_hamburger_item_chuck);
        fragmentTagsToID.put("Drop My Phone", R.id.menu_hamburger_item_drop);
        fragmentTagsToID.put("Spin My Phone", R.id.menu_hamburger_item_spin);
        fragmentTagsToID.put("'s Profile", R.id.menu_hamburger_item_profile);
        fragmentTagsToID.put("Leaderboards", R.id.menu_hamburger_item_leaderboards);
    }

    public String previousFragmentTag(){
        fragmentTags.pop();
        return fragmentTags.lastElement();
    }

    public String currentFragmentTag (String nextFragmentTag){
        fragmentTags.push(nextFragmentTag);
        return fragmentTags.lastElement();
    }

    public Object lastMenuChoice(){
      return fragmentTagsToID.get(fragmentTags.lastElement());
    }
}