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
    private Map<String, Integer> fragmentTagsToMenuID;
    private Map<String, Integer> fragmentTagsToID;
    private int[] fragmentPositions;

    public NavigationHelper(){
        fragmentTags = new Stack<>();
        fragmentTagsToMenuID = new HashMap<>(5);
        fragmentTagsToID = new HashMap<>(8);
        fragmentPositions = new int[]{-1,-1,-1,-1,-1,-1,-1,-1};
        fillMaps();
    }

    private void fillMaps(){
        fragmentTagsToMenuID.put("'s Profile", 0);
        fragmentTagsToMenuID.put("Leaderboards", 1);
        fragmentTagsToMenuID.put("Chuck My Phone", 2);
        fragmentTagsToMenuID.put("Drop My Phone", 3);
        fragmentTagsToMenuID.put("Spin My Phone", 4);
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

    public String previousFragmentTag(){
        if(noFragmentsLeft()) return null;
        String current = fragmentTags.lastElement();
        fragmentTags.pop();
        fragmentPositions[fragmentTagsToMenuID.get(current)] = -1;
        if(noFragmentsLeft()) return null;
        return fragmentTags.lastElement();
    }

    public Stack<String> getStringStack(){
        return fragmentTags;
    }

    public boolean noFragmentsLeft(){
        return fragmentTags.isEmpty();
    }

    public void rebuildStack(String fragmentTag){
        int position = fragmentPositions[fragmentTagsToMenuID.get(fragmentTag)];
        Stack<String> auxStack = new Stack<>();
        for(int i = fragmentTags.size()-1; i > position; i--){
            auxStack.push(fragmentTags.lastElement());
            fragmentTags.pop();
        }
        fragmentTags.pop();
        while(!auxStack.empty()){
            String lastTag = auxStack.lastElement();
            fragmentTags.push(lastTag);
            fragmentPositions[fragmentTagsToMenuID.get(lastTag)]-=1;
            auxStack.pop();
        }
    }

    public int getFragmentPositionInStack(String tag){
        return fragmentPositions[fragmentTagsToMenuID.get(tag)];
    }

    public String addNextFragmentTag (String nextFragmentTag){
        //if(fragmentTags.contains(nextFragmentTag)) rebuildStack(nextFragmentTag);
        fragmentTags.push(nextFragmentTag);
        //fragmentPositions[fragmentTagsToMenuID.get(nextFragmentTag)] = fragmentTags.size()-1;
        return fragmentTags.lastElement();
    }

    public Object lastMenuChoice(){
        if(noFragmentsLeft()) return null;
        return fragmentTagsToMenuID.get(fragmentTags.lastElement());
    }

    public Object lastFragmentIDChoice(){
        if(noFragmentsLeft()) return null;
        return fragmentTagsToID.get(fragmentTags.lastElement());
    }
}