package com.ohiostate.chuckmyphone.chuckmyphone;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class LocationTests extends LeaderboardsFragment {

    @Test
    public void testSmallDistance() {

        double lat1 = 40.001889;
        double lon1 = -83.019724;
        double lat2 = 39.999563;
        double lon2 = -83.012203;

        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        assertTrue(distance < 0.5 && distance > 0.3);

    }

    @Test
    public void testMileDistance() {

        double lat1 = 40.0089946;
        double lon1 = -83.00165;
        double lat2 = 39.996501;
        double lon2 = -83.012303;

        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        assertTrue(distance < 1.1 && distance > 1.0);

    }

    @Test
    public void testTenMileDistance() {

        double lat1 = 40.1234914;
        double lon1 = -82.9130384;
        double lat2 = 39.999563;
        double lon2 = -83.012203;

        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        assertTrue(distance < 10.2 && distance > 10.0);

    }

    @Test
    public void testTwentyMileDistance() {

        double lat1 = 38.196922;
        double lon1 = -113.299613;
        double lat2 = 38.369361;
        double lon2 = -113.000372;

        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        System.out.println(distance);
        assertTrue(distance < 21.0 && distance > 20.0);

    }

    @Test
    public void testCrossCountry() {

        double lat1 = 47.195614;
        double lon1 = -123.605987;
        double lat2 = 29.570944;
        double lon2 = -82.836556;

        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        System.out.println(distance);
        assertTrue(distance < 2490 && distance > 2480);

    }

}