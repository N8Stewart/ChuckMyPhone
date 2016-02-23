package com.ohiostate.chuckmyphone.chuckmyphone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

public class CompeteDropActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    SensorManager sensManager;
    Sensor linearAccelerometer;

    private boolean userHasSensor;
    private boolean isRecording;
    private long lastUpdate;
    private float speed;
    private float maxSpeed;

    TextView yourBestTextView;
    TextView currentSpeedTextView;
    ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compete_drop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set title at top of screen to "Drop My Phone"
        getSupportActionBar().setTitle("Drop My Phone");

        initializeSensors();
        initializeViews();

        isRecording = false;
        maxSpeed = 0;
        speed = 0;
        lastUpdate = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        sensManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        //make the sensor start listening again
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compete_drop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (isRecording && mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 10) {
                //long dt = (curTime - lastUpdate);
                lastUpdate = curTime;

                //not actually speed, but that is hard to derive
                speed = Math.abs(ay);
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void initializeSensors() {
        //set up sensor overhead
        sensManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        linearAccelerometer = sensManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //make the sensor start listening
        userHasSensor = sensManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initializeViews() {
        currentSpeedTextView = (TextView) findViewById(R.id.CompeteDropActivityCurrentSpeedTextView);
        yourBestTextView = (TextView) findViewById(R.id.CompeteDropActivityYourBestTextBox);
        playButton = (ImageButton) findViewById(R.id.CompeteDropActivityPlayButton);

        if (!userHasSensor) {
            yourBestTextView.setText("Your phone does not have the necessary sensors for this activity");
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userHasSensor) {
                    isRecording = !isRecording;
                    if (isRecording) {
                        playButton.setImageResource(R.drawable.compete_stop);
                    } else {
                        playButton.setImageResource(R.drawable.compete_play);
                    }
                    Thread mythread = new Thread(updateViewRunnable);
                    mythread.start();
                }
            }
        });
    }

    //create a updateViewRunnable thread to run to listen for and update current rotationSpeed
    Runnable updateViewRunnable = new Runnable() {
        public void run() {
            int count = 1;
            while (isRecording && count < 50000) {
                count++;

                //This code updates the UI, needs to be separate because on the original thread can touch the views
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentSpeedTextView.setText("" + speed + " m/s");
                        yourBestTextView.setText("Your best: " + maxSpeed + " m/s");
                    }
                });
            }

            //once the loop is done, stop recording and switch the image back to the play button
            isRecording = false;
            //This code updates the UI, needs to be separate because on the original thread can touch the views
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playButton.setImageResource(R.drawable.compete_play);
                }
            });
        }
    };
}