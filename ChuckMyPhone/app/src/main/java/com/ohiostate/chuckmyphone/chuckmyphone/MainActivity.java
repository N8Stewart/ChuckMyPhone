package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
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

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        CompeteFragment.OnFragmentInteractionListener,
        LeaderboardsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnFragmentInteractionListener {

    private final String TAG = this.getClass().getSimpleName();

    private DrawerLayout mDrawerLayout;
    private SharedPreferencesHelper mSharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate() called");

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_content, new CompeteChuckFragment()).commit();
        navigationView.setCheckedItem(R.id.menu_hamburger_item_chuck);

        getSupportActionBar().setTitle("Chuck My Phone");

        mSharedPreferencesHelper = new SharedPreferencesHelper(this);
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
        }
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

        Fragment fragment = null;

        Class fragmentClass = null;

        switch(id) {
            case R.id.menu_dot_item_about:
                fragmentClass = AboutFragment.class;
                getSupportActionBar().setTitle("About");
                break;
            case R.id.menu_dot_item_change_password:
                fragmentClass = ChangePasswordFragment.class;
                getSupportActionBar().setTitle("Change Password");
                break;
            case R.id.menu_dot_item_settings:
                fragmentClass = SettingsFragment.class;
                getSupportActionBar().setTitle("Settings");
                break;
            default: // logout button
                //wipe shared preferences so it doesn't auto login on this account anymore
                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
                sharedPreferencesHelper.clearSharedData();

                //go back to login screen
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return super.onOptionsItemSelected(item);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_content, fragment).commit();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;

        Class fragmentClass = null;

        switch(id) {
            case R.id.menu_hamburger_item_profile:
                fragmentClass = ProfileFragment.class;
                getSupportActionBar().setTitle(mSharedPreferencesHelper.getUsername());
                break;
            case R.id.menu_hamburger_item_leaderboards:
                fragmentClass = LeaderboardsFragment.class;
                getSupportActionBar().setTitle("Leaderboards");
                break;
            case R.id.menu_hamburger_item_drop:
                fragmentClass = CompeteDropFragment.class;
                getSupportActionBar().setTitle("Drop My Phone");
                break;
            case R.id.menu_hamburger_item_spin:
                fragmentClass = CompeteSpinFragment.class;
                getSupportActionBar().setTitle("Spin My Phone");
                break;
            default:
                fragmentClass = CompeteChuckFragment.class;
                getSupportActionBar().setTitle("Chuck My Phone");
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_content, fragment).commit();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}