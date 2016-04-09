package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CompeteFragment.OnFragmentInteractionListener,
        LeaderboardsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();

    private DrawerLayout mDrawerLayout;

    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CurrentUser.getInstance().loadUserScoreData();
        Log.d(TAG, "onCreate() called");
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        NavigationHelper.getInstance().addNextFragmentTag("Chuck My Phone");


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_content, new CompeteChuckFragment(), "Chuck My Phone").commit();
        mNavigationView.setCheckedItem(R.id.menu_hamburger_item_chuck);

        getSupportActionBar().setTitle("Chuck My Phone");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if(!NavigationHelper.getInstance().noFragmentsLeft()) {
                String previousTag = NavigationHelper.getInstance().previousFragmentTag();
                if (previousTag != null){
                    if (previousTag.equals("'s Profile")) getSupportActionBar().setTitle(CurrentUser.getInstance().getUsername() + previousTag);
                    else getSupportActionBar().setTitle(previousTag);
                    markHamburgerMenu();
                }
            }
        }
    }

    private void markHamburgerMenu(){
        unmarkAllItemsOnMenu();
        Object choice = NavigationHelper.getInstance().lastMenuChoice();
        if(choice!=null){
            int c = (Integer) choice;
            if(c < 10){
                mNavigationView.getMenu().getItem(c).setChecked(true);
            } else {
                mNavigationView.getMenu().getItem(2).getSubMenu().getItem(c%10).setChecked(true);
            }
        }
    }

    private void unmarkAllItemsOnMenu(){
        mNavigationView.getMenu().getItem(0).setChecked(false);
        mNavigationView.getMenu().getItem(1).setChecked(false);
        SubMenu subMenu = mNavigationView.getMenu().getItem(2).getSubMenu();
        subMenu.getItem(0).setChecked(false);
        subMenu.getItem(1).setChecked(false);
        subMenu.getItem(2).setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String nextFragmentTag = "";

        Fragment fragment = null;

        Class fragmentClass = null;

        switch(id) {
            case R.id.menu_dot_item_about:
                fragmentClass = AboutFragment.class;
                nextFragmentTag = "About";
                break;
            case R.id.menu_dot_item_change_password:
                fragmentClass = ChangePasswordFragment.class;
                nextFragmentTag = "Change Password";
                break;
            case R.id.menu_dot_item_settings:
                fragmentClass = SettingsFragment.class;
                nextFragmentTag = "Settings";
                break;
            default: // logout button
                //wipe shared preferences so it doesn't auto login on this account anymore
                SharedPreferencesHelper.clearSharedData(getApplicationContext());

                //go back to login screen
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return super.onOptionsItemSelected(item);
        }

        getSupportActionBar().setTitle(nextFragmentTag);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(NavigationHelper.getInstance().addNextFragmentTag(nextFragmentTag)).
                    replace(R.id.activity_main_fragment_content, fragment).commit();
            unmarkAllItemsOnMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        String nextFragmentTag = "";

        Fragment fragment = null;

        Class fragmentClass = null;

        switch(id) {
            case R.id.menu_hamburger_item_profile:
                fragmentClass = ProfileFragment.class;
                nextFragmentTag = "'s Profile";
                getSupportActionBar().setTitle(CurrentUser.getInstance().getUsername()+ nextFragmentTag);
                break;
            case R.id.menu_hamburger_item_leaderboards:
                fragmentClass = LeaderboardsFragment.class;
                nextFragmentTag = "Leaderboards";
                getSupportActionBar().setTitle(nextFragmentTag);
                break;
            case R.id.menu_hamburger_item_drop:
                fragmentClass = CompeteDropFragment.class;
                nextFragmentTag = "Drop My Phone";
                getSupportActionBar().setTitle(nextFragmentTag);
                break;
            case R.id.menu_hamburger_item_spin:
                fragmentClass = CompeteSpinFragment.class;
                nextFragmentTag = "Spin My Phone";
                getSupportActionBar().setTitle(nextFragmentTag);
                break;
            default:
                fragmentClass = CompeteChuckFragment.class;
                nextFragmentTag = "Chuck My Phone";
                getSupportActionBar().setTitle(nextFragmentTag);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(NavigationHelper.getInstance().addNextFragmentTag(nextFragmentTag)).
                    replace(R.id.activity_main_fragment_content, fragment).commit();
            markHamburgerMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}