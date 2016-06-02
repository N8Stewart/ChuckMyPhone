package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener,
        CompeteFragment.OnFragmentInteractionListener,
        LeaderboardsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ChangePasswordFragment.OnFragmentInteractionListener,
        TipsFragment.OnFragmentInteractionListener {

    private static final long GPS_THREAD_SLEEP_TIME = 4000;

    private final String TAG = this.getClass().getSimpleName();

    private static MainActivity main;

    private GPSHelper mGPSHelper;

    // Views
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

        mGPSHelper = new GPSHelper(this);

        NavigationHelper.getInstance().addNextFragmentTag("Chuck My Phone");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_fragment_content, new CompeteChuckFragment(), "Chuck My Phone").commit();
        mNavigationView.setCheckedItem(R.id.menu_hamburger_item_chuck);

        getSupportActionBar().setTitle("Chuck My Phone");
    }

    @Override
    public void onResume() {
        super.onResume();
        main = this;
        Thread t = new Thread(updateGPSRequestRunnable);
        t.start();
        Log.d(TAG, "onResume() called");
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
        mGPSHelper.stopGPS(this);
        SharedPreferencesHelper.setLatitude(getApplicationContext(), CurrentUser.getInstance().getLatitude());
        SharedPreferencesHelper.setLongitude(getApplicationContext(), CurrentUser.getInstance().getLongitude());
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

        //null object ref on int?
        if((int)NavigationHelper.getInstance().lastFragmentIDChoice() != id) {
            switch (id) {
                case R.id.menu_dot_item_about:
                    fragmentClass = AboutFragment.class;
                    nextFragmentTag = "About";
                    break;
                case R.id.menu_dot_item_change_password:
                    fragmentClass = ChangePasswordFragment.class;
                    nextFragmentTag = "Change Password";
                    break;
                case R.id.menu_dot_item_tips:
                    fragmentClass = TipsFragment.class;
                    nextFragmentTag = "Tips";
                    break;
                case R.id.menu_dot_item_settings:
                    fragmentClass = SettingsFragment.class;
                    nextFragmentTag = "Settings";
                    break;
                default: // logout button
                    //wipe shared preferences so it doesn't auto login on this account anymore
                    SharedPreferencesHelper.clearSharedData(getApplicationContext());
                    FirebaseHelper.getInstance().logout();

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
                NavigationHelper.getInstance().addNextFragmentTag(nextFragmentTag);
                fragmentManager.beginTransaction().
                        replace(R.id.activity_main_fragment_content, fragment).commit();
                unmarkAllItemsOnMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        if((int)NavigationHelper.getInstance().lastFragmentIDChoice()!=id) {
            //don't let pop ups from previous fragment appear in new fragment
            MiscHelperMethods.setUserNavigatedAway(true);

            switch (id) {
                case R.id.menu_hamburger_item_profile:
                    fragmentClass = ProfileFragment.class;
                    nextFragmentTag = "'s Profile";
                    getSupportActionBar().setTitle(CurrentUser.getInstance().getUsername() + nextFragmentTag);
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
                NavigationHelper.getInstance().addNextFragmentTag(nextFragmentTag);
                fragmentManager.beginTransaction().
                        replace(R.id.activity_main_fragment_content, fragment).commit();
                markHamburgerMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // method to deal with back button pressed
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!NavigationHelper.getInstance().noFragmentsLeft()) {
                String previousTag = NavigationHelper.getInstance().previousFragmentTag();
                if (previousTag != null){
                    if (previousTag.equals("'s Profile")) getSupportActionBar().setTitle(CurrentUser.getInstance().getUsername() + previousTag);
                    else getSupportActionBar().setTitle(previousTag);
                    Fragment fragment = NavigationHelper.getInstance().translateTagToFragment(previousTag);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().
                            replace(R.id.activity_main_fragment_content, fragment).commit();
                    markHamburgerMenu();
                }
            }

            if(NavigationHelper.getInstance().noFragmentsLeft()) super.onBackPressed();
        }
    }

    private void markHamburgerMenu(){
        // method to highlight the correct item in the hamburger menu
        unmarkAllItemsOnMenu();
        Object choice = NavigationHelper.getInstance().lastMenuChoice();
        if(choice!=null){
            int c = (Integer) choice;
            if(c < 2){
                // to mark first part of menu
                mNavigationView.getMenu().getItem(c).setChecked(true);
            } else if(c < 5) {
                // to mark second part of menu (competitions)
                mNavigationView.getMenu().getItem(2).getSubMenu().getItem(NavigationHelper.translateMenuIDToSubMenuID(c)).setChecked(true);
            }
        }
    }

    private void unmarkAllItemsOnMenu(){
        // method to undo the highlighting of all items in the hamburger menu
        mNavigationView.getMenu().getItem(0).setChecked(false);
        mNavigationView.getMenu().getItem(1).setChecked(false);
        SubMenu subMenu = mNavigationView.getMenu().getItem(2).getSubMenu();
        subMenu.getItem(0).setChecked(false);
        subMenu.getItem(1).setChecked(false);
        subMenu.getItem(2).setChecked(false);
    }

    private final Runnable updateGPSRequestRunnable = new Runnable() {
        @Override
        public void run() {
            // runnable run to keep trying to request gps location until gps is enabled
            Looper.prepare();
            while (!CurrentUser.getInstance().isLocationUpdated()) {
                Log.d("coords", "thread loop");
                if (mGPSHelper.isGPSEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.d("coords", "thread if");
                    mGPSHelper.requestLocation(main, LocationManager.NETWORK_PROVIDER);
                    break;
                }
                try {
                    Thread.sleep(GPS_THREAD_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Looper.loop();
            Log.d("coords", "thread end");
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        // method to listen to location changes found by gps
        mGPSHelper.setLocation(location);
        Log.d("coords", "changed");
        CurrentUser.getInstance().updateLocationUpdated(true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
        // method to listen to gps enablement
        Log.d("coords", provider + " enabled");
        CurrentUser.getInstance().updateGPSEnabled(true);
        mGPSHelper.setToLastLocation(this, provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // method to listen to gps disablement
        Log.d("coords", provider + " disabled");
        CurrentUser.getInstance().updateGPSEnabled(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        Toast.makeText(getApplicationContext(), "OnActivityResultCalled", Toast.LENGTH_LONG).show();

        // Pass on the activity result to the helper for handling
        if (!AboutFragment.handleOnActivityResult(requestCode, resultCode, data)) {
            Toast.makeText(getApplicationContext(), "OnActivityResultCalled, did not get handled by IAB", Toast.LENGTH_LONG).show();

            // put logic for handling non-inAppBilling logic here. The call above will handle all in app billing requests
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}