package com.ohiostate.chuckmyphone.chuckmyphone;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTests {
    @Test
    public void isUsernameAvailableWhenDataNotLoaded() throws Exception {
        assertFalse(FirebaseHelper.getInstance().isUsernameAvailable("tim"));
    }

    @Test
    public void isValidUsernameOnInvalidUsername() throws Exception {
        NewUserActivity a = new NewUserActivity();
        boolean actual = a.isValidUsername("$$$$$$$$$$$$$$$$$$$$$$$$");
        assertFalse(actual);
    }

    @Test
    public void initialFirebaseSnapshotNotLoadedByDefault() throws Exception {
        assertFalse(FirebaseHelper.getInstance().hasLoadedInitialSnapshot);
    }

    @Test
    public void isValidUsernameOnValidUsername() throws Exception {
        NewUserActivity a = new NewUserActivity();
        boolean actual = a.isValidUsername("MYNAME9");
        assertTrue(actual);
    }

    @Test
    public void isUserLocationLoadedCorrectly() throws Exception {
        double latitude = 5.2705, longitude = 27.566;
        CurrentUser.getInstance().loadUserLocation(latitude, longitude);
        assertEquals(latitude, CurrentUser.getInstance().getLatitude(), 0.0);
        assertEquals(longitude, CurrentUser.getInstance().getLongitude(), 0.0);
    }

    @Test
    public void isUserSettingsLoadedCorrectly() throws Exception {
        boolean tutorialEnabled = false, soundEnabled = true;
        boolean badgeUnlockNotificationsEnabled = true, goofySoundEnabled = true;
        CurrentUser.getInstance().loadUserSettings(tutorialEnabled, soundEnabled, badgeUnlockNotificationsEnabled, goofySoundEnabled);
        assertTrue(tutorialEnabled == CurrentUser.getInstance().getTutorialMessagesEnabled() &&
                    soundEnabled == CurrentUser.getInstance().getSoundEnabled() &&
                    badgeUnlockNotificationsEnabled == CurrentUser.getInstance().getBadgeNotificationsEnabled() &&
                    goofySoundEnabled == CurrentUser.getInstance().getGoofySoundEnabled());
    }
}