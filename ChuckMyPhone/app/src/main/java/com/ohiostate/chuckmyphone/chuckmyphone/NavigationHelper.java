package com.ohiostate.chuckmyphone.chuckmyphone;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Joao Pedro on 4/2/2016.
 */
public class NavigationHelper {

    private static NavigationHelper ourInstance = new NavigationHelper();

    public static NavigationHelper getInstance(){ return ourInstance;}

    private Stack<String> fragmentTags;
    private Map<String, Integer> fragmentTagsToID;
    
    private NavigationHelper(){
        fragmentTags = new Stack<>();
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
        if(noFragmentsLeft()) return null;
        fragmentTags.pop();
        if(noFragmentsLeft()) return null;
        return fragmentTags.lastElement();
    }

    public boolean noFragmentsLeft(){
        return fragmentTags.isEmpty();
    }

    public String addNextFragmentTag (String nextFragmentTag){
        fragmentTags.push(nextFragmentTag);
        return fragmentTags.lastElement();
    }

    public Object lastMenuChoice(){
        if(noFragmentsLeft()) return null;
        return fragmentTagsToID.get(fragmentTags.lastElement());
    }
}