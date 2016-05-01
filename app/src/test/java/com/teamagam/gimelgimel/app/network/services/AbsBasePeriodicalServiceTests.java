package com.teamagam.gimelgimel.app.network.services;

import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowIntentService;

import static org.mockito.Mockito.mock;

/**
 * Created by Gil.Raytan on 20-Apr-16.
 */
@RunWith(RobolectricTestRunner.class)
public class AbsBasePeriodicalServiceTests extends InstrumentationTestCase {

    AbsBasePeriodicalService mMockAbsService;
    Context mContext;
    @Before
    public void setUp() throws Exception {

        mMockAbsService = mock(AbsBasePeriodicalService.class);
        mContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartPolling_negativeArgument_shouldThrow() throws Exception {
        //Arrange
        int period = -1;

        //Act
        mMockAbsService.startPollingPeriodically(mContext, period);
    }


    @Test
    public void validateIntentSent() {
        // User action that results in an external "phone" activity being launched.

        int period = 1000;

        AbsBasePeriodicalService service = mock(AbsBasePeriodicalService.class);
        service.CURRENT_CLASS = "test".getClass();
        service.startPollingPeriodically(mContext, period);

        Intent expectedIntent = new Intent(mContext, "test".getClass());
        ShadowIntentService resultService = Shadows.shadowOf(service);

        // Using a canned RecordedIntentMatcher to validate that an intent resolving
        // to the "phone" activity has been sent.

//        ActivityManager manager = (ActivityManager) mContext.getSystemService(AbsBasePeriodicalService.class)
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("test".getClass().equals(service.service.getClassName())) {
//
//            }
//        }
//        return false;
    }

}