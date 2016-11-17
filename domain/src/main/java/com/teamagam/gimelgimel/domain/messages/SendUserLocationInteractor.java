package com.teamagam.gimelgimel.domain.messages;


import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.teamagam.gimelgimel.domain.base.executor.ThreadExecutor;
import com.teamagam.gimelgimel.domain.messages.entity.MessageUserLocation;
import com.teamagam.gimelgimel.domain.messages.entity.contents.LocationSample;
import com.teamagam.gimelgimel.domain.messages.repository.MessagesRepository;
import com.teamagam.gimelgimel.domain.user.repository.UserPreferencesRepository;

import rx.Observable;

@AutoFactory
public class SendUserLocationInteractor extends CreateMessageInteractor<MessageUserLocation> {

    private MessagesRepository mMessagesRepository;
    private LocationSample mLocationSample;

    public SendUserLocationInteractor(@Provided ThreadExecutor threadExecutor,
                                      @Provided UserPreferencesRepository userPreferences,
                                      @Provided MessagesRepository messagesRepository,
                                      LocationSample locationSample) {
        super(threadExecutor, userPreferences);
        mMessagesRepository = messagesRepository;
        mLocationSample = locationSample;
    }

    @Override
    protected Observable<MessageUserLocation> buildUseCaseObservable() {
        return super.buildUseCaseObservable()
                .doOnNext(mMessagesRepository::sendMessage);
    }

    @Override
    protected MessageUserLocation createMessage(String senderId) {
        return new MessageUserLocation(null, senderId, null, mLocationSample);
    }
}
