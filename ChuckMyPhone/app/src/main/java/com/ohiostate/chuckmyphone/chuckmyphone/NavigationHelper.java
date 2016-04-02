package com.ohiostate.chuckmyphone.chuckmyphone;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Stack;

/**
 * Created by Joao Pedro on 4/2/2016.
 */
public class NavigationHelper {

    private static NavigationHelper ourInstance = new NavigationHelper("Chuck My Phone");

    public static NavigationHelper getInstance(){ return ourInstance;}

    private Stack<String> fragmentTags;
    
    public NavigationHelper(String firstTag){
        fragmentTags = new Stack<>();
        fragmentTags.push(firstTag);
    }

    public String previousFragmentTag(){
        fragmentTags.pop();
        return fragmentTags.lastElement();
    }

    public String currentFragmentTag (String nextFragmentTag){
        fragmentTags.push(nextFragmentTag);
        return fragmentTags.lastElement();
    }
}