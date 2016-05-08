package com.teamagam.gimelgimel.app.network.services;

import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

/**
 * Created by Gil.Raytan on 20-Apr-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class AbsBasePeriodicalServiceTests extends InstrumentationTestCase {

    public static final String ACTION_NAME = "ActionName";
    public static final int DELAY_PERIOD = 1000;
    AbsBasePeriodicalService mMockAbsService;
    Context mContext;
    @Before
    public void setUp() throws Exception {

        //mocking the abs class
        mMockAbsService = mock(AbsBasePeriodicalService.class, Mockito.CALLS_REAL_METHODS);



        //context creation
        mContext = RuntimeEnvironment.application.getApplicationContext();

//        Mockito.when(myClassSpy.onHandleIntent(intent).thenReturn(myResults);
//        doAnswer(new Answer<Void>() {
//            public Void answer(InvocationOnMock invocation) {
//                Object[] args = invocation.getArguments();
//                System.out.println("called with arguments: " + Arrays.toString(args));
//                return null;
//            }
//        }).when(mMockAbsService).startPollingPeriodically(mContext);





//        when(abs.onHandleIntent(intent)).thenAnswer(new Answer<String>() {
//            @Override
//            public String onHandleIntent(Intent intent)  {
//                return (String) args[0];
//            }
//        });

//        assertEquals("someString",mock.myFunction("someString"));
//        assertEquals("anotherString",mock.myFunction("anotherString"));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartPolling_negativeArgument_shouldThrow() throws Exception {
        //Arrange
        when(mMockAbsService.getActionName()).thenReturn(ACTION_NAME);
        when(mMockAbsService.getTimeBetweenExecutionsMs()).thenReturn(-1);

        //Act
        mMockAbsService.startPollingPeriodically(mContext);
    }


    @Test
    public void testStartPolling_testWhetherServiceLaunched_shouldVerifyMethodInvoked() {
        //act
        mMockAbsService.startPollingPeriodically(mContext);

        //assert
        verify(mMockAbsService,after(10000).atLeast(1)).handleActionPolling();
    }

    @Test
    public void testStartPolling_testWhetherServiceLaunchedSeveralTimes_shouldVerifyMethodInvoked() {
//        //arrange
//        mMockAbsService.startPollingPeriodically(mContext);
//        Intent intent = new Intent();
//
//        //verify
//        verify(mMockAbsService, times(1)).onHandleIntent(intent);
    }

//
//    public void testStartPolling_validArguments_shouldStartHandleWork() throws Exception {
//        //Arrange
//        AbsBasePeriodicalService service = mock(AbsBasePeriodicalService.class);
//
//        //Act
//        service.startPollingPeriodically(mContext);
//
//        //Wait period time
//        verify(mMockAbsService, times(1)).handleActionPolling();
//
//    }

//    @Test
//    public void validateIntentSent() {
//        // User action that results in an external "phone" activity being launched.
//
//        int period = 1000;
//
//        AbsBasePeriodicalService service = mock(AbsBasePeriodicalService.class);
//        service.mCurrentClass = "test".getClass();
//        service.startPollingPeriodically(mContext, period);
//
//        Intent expectedIntent = new Intent(mContext, "test".getClass());
//        ShadowIntentService resultService = Shadows.shadowOf(service);

        // Using a canned RecordedIntentMatcher to validate that an intent resolving
        // to the "phone" activity has been sent.

//        ActivityManager manager = (ActivityManager) mContext.getSystemService(AbsBasePeriodicalService.class)
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if ("test".getClass().equals(service.service.getClassName())) {
//
//            }
//        }
//        return false;
//    }

}