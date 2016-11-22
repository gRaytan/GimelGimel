/*
package com.teamagam.gimelgimel.app.network.services.message_polling;

import com.teamagam.gimelgimel.BuildConfig;
import com.teamagam.gimelgimel.app.message.model.MessageApp;
import com.teamagam.gimelgimel.app.network.rest.GGMessagingAPI;
import com.teamagam.gimelgimel.app.utils.PreferenceUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class MessageLongPollerTest {

    private MessageLongPoller mMessagePoller;
    private GGMessagingAPI mGGMessagingAPIMock;
    private IPolledMessagesProcessor mPolledMessagesProcessorMock;
    private PreferenceUtil mPreferenceUtilMock;
    private Call<List<MessageApp>> mCallListMessagesMock;
    private List<MessageApp> mResponseList;
    private Response<List<MessageApp>> mSuccessfulResponse;

    @Before
    public void setUp() throws Exception {

        mGGMessagingAPIMock = mock(GGMessagingAPI.class);
        mPolledMessagesProcessorMock = mock(IPolledMessagesProcessor.class);
        mPreferenceUtilMock = mock(PreferenceUtil.class);

        mMessagePoller = new MessageLongPoller(mGGMessagingAPIMock, mPolledMessagesProcessorMock,
                mPreferenceUtilMock);

        mCallListMessagesMock = mock(Call.class);

        mResponseList = new ArrayList<>();

        mSuccessfulResponse = Response.success(mResponseList);
        when(mCallListMessagesMock.execute()).thenReturn(mSuccessfulResponse);
    }

    @Test(expected = IMessagePoller.ConnectionException.class)
    public void testPoll_onBadConnection_shouldThrowConnectionException() throws Exception {
        //Arrange
        when(mCallListMessagesMock.execute()).thenThrow(IOException.class);
        when(mGGMessagingAPIMock.getMessagesFromDate(0)).thenReturn(mCallListMessagesMock);

        //Act
        mMessagePoller.poll();
    }

    @Test(expected = RuntimeException.class)
    public void testPoll_onUnknownRuntimeException_shouldThrowRuntimeException() throws Exception {
        //Arrange
        when(mCallListMessagesMock.execute()).thenThrow(RuntimeException.class);
        when(mGGMessagingAPIMock.getMessages()).thenReturn(mCallListMessagesMock);

        //Act
        mMessagePoller.poll();
    }

    @Test
    public void testPoll_noMessages_synchronizationDateStaysTheSame() throws Exception {
        //Arrange
        long syncDate = 123;
        when(mGGMessagingAPIMock.getMessagesFromDate(syncDate)).thenReturn(mCallListMessagesMock);
        when(mPreferenceUtilMock.getLong(anyInt(), anyLong())).thenReturn(syncDate);

        //Act
        mMessagePoller.poll();

        //Assert
        verify(mPreferenceUtilMock, never()).commitLong(anyInt(), anyFloat());
    }

    @Test
    public void testPoll_noMessages_shouldntProcessAnything() throws Exception {
        //Arrange
        long syncDate = 123;
        when(mGGMessagingAPIMock.getMessagesFromDate(syncDate)).thenReturn(mCallListMessagesMock);
        when(mPreferenceUtilMock.getLong(anyInt(), anyLong())).thenReturn(syncDate);

        //Act
        mMessagePoller.poll();

        //Assert
        verify(mPolledMessagesProcessorMock, never()).process(any(Collection.class));
    }

    @Test
    public void testPoll_polledMessagesNotEmpty_shouldUpdateSynchronizationToMaxMessageDate() throws Exception {
        //Arrange
        long syncDate = 123;
        when(mGGMessagingAPIMock.getMessagesFromDate(syncDate)).thenReturn(mCallListMessagesMock);
        when(mPreferenceUtilMock.getLong(anyInt(), anyLong())).thenReturn(syncDate);

        MessageApp messageMock1 = mock(MessageApp.class);
        MessageApp messageMock2 = mock(MessageApp.class);
        MessageApp messageMock3 = mock(MessageApp.class);
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 1000);
        Date date3 = new Date(date2.getTime() + 1000);
        when(messageMock1.getCreatedAt()).thenReturn(date1);
        when(messageMock2.getCreatedAt()).thenReturn(date2);
        when(messageMock3.getCreatedAt()).thenReturn(date3);
        mResponseList.add(messageMock1);
        mResponseList.add(messageMock2);
        mResponseList.add(messageMock3);

        //Act
        mMessagePoller.poll();

        //Assert
        verify(mPreferenceUtilMock, times(1)).commitLong(anyInt(), eq(date3.getTime()));
    }

    @Test
    public void testPoll_polledMessagesNotEmpty_shouldProcessAll() throws Exception {
        //Arrange
        long syncDate = 123;
        when(mGGMessagingAPIMock.getMessagesFromDate(syncDate)).thenReturn(mCallListMessagesMock);
        when(mPreferenceUtilMock.getLong(anyInt(), anyLong())).thenReturn(syncDate);

        MessageApp messageMock1 = mock(MessageApp.class);
        MessageApp messageMock2 = mock(MessageApp.class);
        MessageApp messageMock3 = mock(MessageApp.class);
        Date date = new Date();
        when(messageMock1.getCreatedAt()).thenReturn(date);
        when(messageMock2.getCreatedAt()).thenReturn(date);
        when(messageMock3.getCreatedAt()).thenReturn(date);
        mResponseList.add(messageMock1);
        mResponseList.add(messageMock2);
        mResponseList.add(messageMock3);

        //Act
        mMessagePoller.poll();

        //Assert
        verify(mPolledMessagesProcessorMock, times(1)).process(mResponseList);
    }
}*/
