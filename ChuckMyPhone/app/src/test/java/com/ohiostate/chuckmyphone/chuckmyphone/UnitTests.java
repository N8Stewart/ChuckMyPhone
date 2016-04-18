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

}