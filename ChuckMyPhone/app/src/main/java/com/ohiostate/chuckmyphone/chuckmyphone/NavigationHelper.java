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
        fragmentTagsToID = new HashMap<>(5);
        fillMap();
    }

    private void fillMap(){
        fragmentTagsToID.put("Chuck My Phone", 10);
        fragmentTagsToID.put("Drop My Phone", 11);
        fragmentTagsToID.put("Spin My Phone", 12);
        fragmentTagsToID.put("'s Profile", 0);
        fragmentTagsToID.put("Leaderboards", 1);
    }

    public String previousFragmentTag(){
        fragmentTags.pop();
        return fragmentTags.lastElement();
    }

    public String addNextFragmentTag (String nextFragmentTag){
        fragmentTags.push(nextFragmentTag);
        return fragmentTags.lastElement();
    }

    public Object lastMenuChoice(){
      return fragmentTagsToID.get(fragmentTags.lastElement());
    }
}