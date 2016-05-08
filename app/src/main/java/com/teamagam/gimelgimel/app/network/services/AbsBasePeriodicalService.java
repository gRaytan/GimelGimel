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

    protected static final int FIRST_DELAY = 0;

    protected AbsBasePeriodicalService(String name) {
        super(name);
    }

    /**
     * Starts this service to perform polling action. If the service is
     * already performing a task this action will be queued.
     *
     * @param context
     * @see IntentService
     */
    private void startActionPolling(Context context) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(getActionName());
        context.startService(intent);
    }

    /**
     * Sets this service to periodically start performing polling action
     *
     * @param context - to be used to construct every new action intent
     */
    public void startPollingPeriodically(final Context context) {

        if (getTimeBetweenExecutionsMs() < 0){
            throw new IllegalArgumentException("Time betweem executions cant be negative in startPollingPeriodically");
        }
        startTimerAndSchedule(context, FIRST_DELAY);
    }

    /**
     * Starts a Timer and TimerTask to perform service launching after a delay
     *
     * @param context - to be used to construct every new action intent
     * @param delay - to define how much time we need to wait berfore exectution
     */
    protected void startTimerAndSchedule(final Context context, int delay){
        Timer t = new Timer("pollingTimer", true /*isDaemon*/);

        TimerTask pollingTask = new TimerTask() {
            @Override
            public void run() {
                startActionPolling(context);
            }
        };
        t.schedule(pollingTask,delay);
    }
    /**
     * Executes on new intent received by service.
     * Maps between given intent action and its appropriate action.
     * handles the action by invoking handleActionPolling
     * *finaly, starts a new scheduler for a new request
     *
     * @param intent - injected externally by Android with generating intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (getActionName().equals(action)) {
                handleActionPolling();
            }
            startTimerAndSchedule(getBaseContext(), getTimeBetweenExecutionsMs());
        }
    }

    /**
     * setters for the Constants (by Sub classes)
     */
    abstract protected String getActionName();

    abstract protected int getTimeBetweenExecutionsMs();

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
