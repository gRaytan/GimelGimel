package com.teamagam.gimelgimel.app;

import android.app.Application;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.teamagam.gimelgimel.app.common.RepeatedBackoffTaskRunner;
import com.teamagam.gimelgimel.app.common.logging.LoggerFactory;
import com.teamagam.gimelgimel.app.control.receivers.GpsStatusBroadcastReceiver;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageMapEntitiesViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.messages.ContainerMessagesViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.messages.ImageMessageDetailViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.messages.LatLongMessageDetailViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.messages.MessagesViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.messages.TextMessageDetailViewModel;
import com.teamagam.gimelgimel.app.model.entities.messages.InMemory.InMemoryMessagesModel;
import com.teamagam.gimelgimel.app.model.entities.messages.InMemory.InMemoryMessagesReadStatusModel;
import com.teamagam.gimelgimel.app.model.entities.messages.InMemory.InMemorySelectedMessageModel;
import com.teamagam.gimelgimel.app.model.entities.messages.MessagesModel;
import com.teamagam.gimelgimel.app.model.entities.messages.MessagesReadStatusModel;
import com.teamagam.gimelgimel.app.model.entities.messages.SelectedMessageModel;
import com.teamagam.gimelgimel.app.network.services.message_polling.RepeatedBackoffMessagePolling;
import com.teamagam.gimelgimel.app.utils.BasicStringSecurity;
import com.teamagam.gimelgimel.app.utils.SecuredPreferenceUtil;

public class GGApplication extends Application {

    private SecuredPreferenceUtil mPrefs;
    private GpsStatusBroadcastReceiver mGpsStatusBroadcastReceiver;
    private char[] mPrefSecureKey = ("GGApplicationSecuredKey!!!").toCharArray();
    private RepeatedBackoffMessagePolling mRepeatedBackoffMessagePolling;
    private MessagesModel mMessagesModel;
    private MessagesReadStatusModel mMessagesReadStatusModel;
    private SelectedMessageModel mSelectedMessageModel;
    private MessagesViewModel mMessagesViewModel;
    private ImageMessageDetailViewModel mImageMessageDetailViewModel;
    private TextMessageDetailViewModel mTextMessageDetailViewModel;
    private LatLongMessageDetailViewModel mLatLongMessageDetailViewModel;
    private ContainerMessagesViewModel mContainerMessagesViewModel;
    private MessageMapEntitiesViewModel mMessageMapEntitiesViewModel;


    @Override
    public void onCreate() {
        super.onCreate();

        init();
        registerBroadcasts();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterBroadcasts();
    }

    public SecuredPreferenceUtil getPrefs() {
        if (mPrefs == null) {
            // Set up a preferences manager (with basic security)
            mPrefs = new SecuredPreferenceUtil(getResources(),
                    PreferenceManager.getDefaultSharedPreferences(this),
                    new BasicStringSecurity(mPrefSecureKey));
        }

        return mPrefs;
    }

    private void init() {
        compositeModels();
        compositeViewModels();

        mGpsStatusBroadcastReceiver = new GpsStatusBroadcastReceiver(this);

        mRepeatedBackoffMessagePolling = RepeatedBackoffMessagePolling.create(this);

        LoggerFactory.init(this);

        // Initialize the fresco plugin.
        // Should be here instead of each activity
        Fresco.initialize(this);
    }

    private void compositeViewModels() {
        mMessagesViewModel = new MessagesViewModel(mMessagesModel, mSelectedMessageModel,
                mMessagesReadStatusModel);
        mContainerMessagesViewModel = new ContainerMessagesViewModel(mSelectedMessageModel,
                mMessagesReadStatusModel, mMessagesModel);
        mImageMessageDetailViewModel = new ImageMessageDetailViewModel(mSelectedMessageModel);
        mTextMessageDetailViewModel = new TextMessageDetailViewModel(mSelectedMessageModel);
        mLatLongMessageDetailViewModel = new LatLongMessageDetailViewModel(mSelectedMessageModel);
        mMessageMapEntitiesViewModel = new MessageMapEntitiesViewModel(mSelectedMessageModel);
    }

    private void compositeModels() {
        mMessagesModel = new InMemoryMessagesModel();
        mMessagesReadStatusModel = new InMemoryMessagesReadStatusModel();
        mSelectedMessageModel = new InMemorySelectedMessageModel();
    }

    private void registerBroadcasts() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(
                GpsStatusBroadcastReceiver.BROADCAST_GPS_STATUS_ACTION);

        localBroadcastManager.registerReceiver(mGpsStatusBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcasts() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        localBroadcastManager.unregisterReceiver(mGpsStatusBroadcastReceiver);
    }

    public RepeatedBackoffTaskRunner getRepeatedBackoffMessagePolling() {
        return mRepeatedBackoffMessagePolling;
    }

    public MessagesViewModel getMessagesViewModel() {
        return mMessagesViewModel;
    }

    public ContainerMessagesViewModel getContainerMessagesViewModel() {
        return mContainerMessagesViewModel;
    }

    public ImageMessageDetailViewModel getImageMessageDetailViewModel() {
        return mImageMessageDetailViewModel;
    }

    public TextMessageDetailViewModel getTextMessageDetailViewModel() {
        return mTextMessageDetailViewModel;
    }

    public LatLongMessageDetailViewModel getLatLongMessageDetailViewModel() {
        return mLatLongMessageDetailViewModel;
    }

    public MessageMapEntitiesViewModel getMessageMapEntitiesViewModel() {
        return mMessageMapEntitiesViewModel;
    }

    public MessagesModel getMessagesModel() {
        return mMessagesModel;
    }
}