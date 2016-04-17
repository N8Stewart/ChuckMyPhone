package com.ohiostate.chuckmyphone.chuckmyphone;

import android.content.Context;
import android.test.ServiceTestCase;

import com.firebase.client.Firebase;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTests {
    @Test
    public void isUsernameAvailableWhenDataNotLoaded() throws Exception {
        assertEquals(FirebaseHelper.getInstance().isUsernameAvailable("tim"), false);
    }

    @Test
    public void isValidUsernameOnInvalidUsername() throws Exception {
        NewUserActivity a = new NewUserActivity();
        boolean actual = a.isValidUsername("$$$$$$$$$$$$$$$$$$$$$$$$");
        assertEquals(actual, false);
    }

    @Test
    public void initialFirebaseSnapshotNotLoadedByDefault() throws Exception {
        assertEquals(FirebaseHelper.getInstance().hasLoadedInitialSnapshot, false);
    }

}