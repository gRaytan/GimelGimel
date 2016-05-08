package com.teamagam.gimelgimel.app.network.services;

import android.app.IntentService;
import android.preference.PreferenceManager;
import android.util.Log;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.model.ViewsModels.Message;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessagePubSub;
import com.teamagam.gimelgimel.app.utils.PreferenceUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * An {@link IntentService, AbsBasePeriodicalSevice}
 * subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * Polls for new messages from remote service on request.
 */
public class GGMessagePollingService
        extends AbsBasePeriodicalService<Message> {

    private static final String LOG_TAG = GGMessagePollingService.class.getSimpleName();

    //todo: move to config
    private static final int MSG_POLLING_SERVICE_TIME_BETWEEN_EXECUTION_MS = 5000;
    private static final String MESSAGE_POLLING_ACTION_NAME =
            "com.teamagam.gimelgimel.app.network.services.action.MESSAGE_POLLING_FROM_SERVER";

    public GGMessagePollingService() {
        super("GGMessagePollingService");
    }

    @Override
    protected String getActionName()
    {
        return MESSAGE_POLLING_ACTION_NAME;
    }

    @Override
    protected int getTimeBetweenExecutionsMs() {
        return MSG_POLLING_SERVICE_TIME_BETWEEN_EXECUTION_MS;
    }

    @Override
    protected void handleActionPolling()
    {
        PreferenceUtil prefUtils = new PreferenceUtil(getResources(),
                PreferenceManager.getDefaultSharedPreferences(this));
        //get latest synchronized date from shared prefs
        long synchronizedDateMs = prefUtils.getLong(
                R.string.pref_latest_received_message_date_in_ms, 0);

        Log.d(LOG_TAG,
                "Polling for new messages with synchronization date (ms): " + synchronizedDateMs);

        Collection<Message> messages = GGMessagingUtils.getMessagesSync(synchronizedDateMs);

        if (messages == null || messages.size() == 0) {
            Log.d(LOG_TAG, "No new messages available");
            return;
        }
        processNewItems(messages);

        Message maximumMessageDateMessage = getMaximumDateMessage(messages);
        long newSynchronizedDateMs = maximumMessageDateMessage.getCreatedAt().getTime();

        //Get latest date and write to shared preference for future polling
        prefUtils.commitLong(R.string.pref_latest_received_message_date_in_ms,
                newSynchronizedDateMs);
        Log.d(LOG_TAG, "New synchronization date (ms) set to " + newSynchronizedDateMs);
    }
    /**
     * Process given messages
     *
     * @param messages - messages to process
     */
    @Override
    protected void processNewItems(Collection<Message> messages) {
        Log.d(LOG_TAG, "MessagePolling service processing in" + getActionName() + messages.size() + " new messages");

        for (Message m :
                messages) {
            MessagePubSub.getInstance().publish(m);
        }
    }

    /**
     * Retrieves the latest date ctreatd of the messages
     * @param messages - messages to check
     * @return Message - represents the message with latest date
     */
    private Message getMaximumDateMessage(Collection<Message> messages) {
        Message m = Collections.max(messages, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return lhs.getCreatedAt().compareTo(rhs.getCreatedAt());
            }
        });

        return m;
    }
}
