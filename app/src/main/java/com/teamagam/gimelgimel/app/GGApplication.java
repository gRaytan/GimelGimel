package com.teamagam.gimelgimel.app;

import android.app.Application;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.injectors.components.ApplicationComponent;
import com.teamagam.gimelgimel.app.injectors.components.DaggerApplicationComponent;
import com.teamagam.gimelgimel.app.injectors.modules.ApplicationModule;
import com.teamagam.gimelgimel.data.common.ExternalDirProvider;
import com.teamagam.gimelgimel.domain.base.logging.LoggerFactory;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

public class GGApplication extends Application {

    @Inject
    ExternalDirProvider mExternalDirProvider;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void initializeInjector() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    private void init() {
        initializeRealm();
        initializeInjector();
        initializeLoggers();
        initializeMessagePolling();

        mApplicationComponent.displayUserLocationsInteractor().execute();
        mApplicationComponent.displaySensorsOnMapInteractor().execute();
        mApplicationComponent.loadAllCachedLayersInteractor().execute();
        mApplicationComponent.loadIntermediateRastersInteractor().execute();
        mApplicationComponent.update3GConnectivityStatusInteractor().execute();
    }

    private void initializeRealm() {
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    private void initializeMessagePolling() {
        resetMessageSynchronizationTime();
        mApplicationComponent.startFetchingMessagesInteractor().execute();
    }

    private void initializeLoggers() {
        AppLoggerFactory.init(mExternalDirProvider);

        LoggerFactory.initialize(AppLoggerFactory::create);
    }

    private void resetMessageSynchronizationTime() {
        String latestReceivedDateKey = getResources().getString(
                R.string.pref_latest_received_message_date_in_ms);
        mApplicationComponent.userPreferencesRepository().setPreference(latestReceivedDateKey,
                (long) 0);
    }

    public void startSendingLocation() {
        mApplicationComponent.sendMyLocationInteractor().execute();
    }
}