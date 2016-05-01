package com.teamagam.gimelgimel.app.network.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gil.Raytan on 19-Apr-16.
 */
public abstract class AbsBasePeriodicalService<T> extends IntentService {

    protected static String LOG_TAG;
    protected static String ACTION_NAME;
    protected static Class<?> CURRENT_CLASS;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AbsBasePeriodicalService(String name) {
        super(name);
    }

    /**
     * Starts this service to perform polling action. If the service is
     * already performing a task this action will be queued.
     *
     * @param context
     * @see IntentService
     */
    public static void startActionPolling(Context context) {
        Intent intent = new Intent(context, CURRENT_CLASS);
        intent.setAction(ACTION_NAME);
        context.startService(intent);
    }

    /**
     * Sets this service to periodically start performing polling action
     *
     * @param context - to be used to construct every new action intent
     * @param period  - amount of time in milliseconds between subsequent executions.
     */
    public static void startPollingPeriodically(final Context context, long period) {
        Timer t = new Timer("pollingTimer", true /*isDaemon*/);

        TimerTask pollingTask = new TimerTask() {
            @Override
            public void run() {
                startActionPolling(context);
            }
        };

        t.scheduleAtFixedRate(pollingTask, 0, period);
    }

    /**
     * Executes on new intent received by service.
     * Maps between given intent action and its appropriate action.
     *
     * @param intent - injected externally by Android with generating intent
     */
    @Override
    abstract protected void onHandleIntent(Intent intent);

    /**
     * Handle items polling action in a background thread (provided by the
     * {@link IntentService} class).
     */
    abstract protected void handleActionPolling();
    /**
     * Process given items
     *
     * @param items - items to process
     */
    abstract protected void processNewItems(Collection<T> items);

}
