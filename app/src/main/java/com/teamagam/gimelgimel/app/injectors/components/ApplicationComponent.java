package com.teamagam.gimelgimel.app.injectors.components;

import android.content.Context;

import com.teamagam.gimelgimel.app.control.sensors.LocationFetcher;
import com.teamagam.gimelgimel.app.injectors.modules.ApiModule;
import com.teamagam.gimelgimel.app.injectors.modules.ApplicationModule;
import com.teamagam.gimelgimel.app.injectors.modules.PreferencesModule;
import com.teamagam.gimelgimel.app.injectors.modules.RepositoryModule;
import com.teamagam.gimelgimel.app.utils.SecuredPreferenceUtil;
import com.teamagam.gimelgimel.data.map.repository.GeoEntitiesDataRepository;
import com.teamagam.gimelgimel.domain.base.executor.PostExecutionThread;
import com.teamagam.gimelgimel.domain.base.executor.ThreadExecutor;
import com.teamagam.gimelgimel.domain.location.respository.LocationRepository;
import com.teamagam.gimelgimel.domain.map.repository.DisplayedEntitiesRepository;
import com.teamagam.gimelgimel.domain.map.repository.GeoEntitiesRepository;
import com.teamagam.gimelgimel.domain.messages.poller.StartFetchingMessagesInteractor;
import com.teamagam.gimelgimel.domain.messages.poller.StopFetchingMessagesInteractor;
import com.teamagam.gimelgimel.domain.messages.repository.ImagesRepository;
import com.teamagam.gimelgimel.domain.messages.repository.MessagesRepository;
import com.teamagam.gimelgimel.domain.user.repository.UserPreferencesRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(
        modules = {
                ApplicationModule.class,
                PreferencesModule.class,
                RepositoryModule.class,
                ApiModule.class,
        })
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);

    //Exposed to sub-graphs.
    Context context();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    SecuredPreferenceUtil sharedPreferences();

    MessagesRepository messagesRepository();

    ImagesRepository imagesRepository();

    LocationFetcher locationFetcher();

    LocationRepository locationRepository();

    UserPreferencesRepository userPreferencesRepository();

    GeoEntitiesRepository geoEntitiesRepository();

    DisplayedEntitiesRepository displayedEntitiesRepository();
    StartFetchingMessagesInteractor startFetchingMessagesInteractor();
    StopFetchingMessagesInteractor stopFetchingMessagesInteractor();


}
