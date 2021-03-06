package com.teamagam.gimelgimel.domain.messages;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.teamagam.gimelgimel.domain.base.executor.PostExecutionThread;
import com.teamagam.gimelgimel.domain.base.executor.ThreadExecutor;
import com.teamagam.gimelgimel.domain.base.interactors.BaseDisplayInteractor;
import com.teamagam.gimelgimel.domain.base.interactors.DisplaySubscriptionRequest;
import com.teamagam.gimelgimel.domain.map.repository.DisplayedEntitiesRepository;
import com.teamagam.gimelgimel.domain.messages.entity.GeoEntityHolder;
import com.teamagam.gimelgimel.domain.messages.entity.Message;
import com.teamagam.gimelgimel.domain.messages.repository.MessagesRepository;
import com.teamagam.gimelgimel.domain.messages.repository.NewMessageIndicationRepository;
import com.teamagam.gimelgimel.domain.messages.repository.ObjectMessageMapper;
import com.teamagam.gimelgimel.domain.utils.MessagesUtil;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Named;

import rx.Observable;

@AutoFactory
public class DisplayMessagesInteractor extends BaseDisplayInteractor {

    private final DisplayedEntitiesRepository mDisplayedEntitiesRepository;
    private final NewMessageIndicationRepository mNewMessageIndicationRepository;
    private final Displayer mDisplayer;
    private final MessagesRepository mMessagesRepository;
    private final MessagesUtil mMessagesUtil;
    private final ObjectMessageMapper mMapper;

    DisplayMessagesInteractor(
            @Provided ThreadExecutor threadExecutor,
            @Provided PostExecutionThread postExecutionThread,
            @Provided MessagesRepository messagesRepository,
            @Provided MessagesUtil messagesUtil,
            @Provided DisplayedEntitiesRepository displayedEntitiesRepository,
            @Provided @Named("Entity") ObjectMessageMapper mapper,
            @Provided NewMessageIndicationRepository newMessageIndicationRepository,
            Displayer displayer) {
        super(threadExecutor, postExecutionThread);
        mMessagesRepository = messagesRepository;
        mMessagesUtil = messagesUtil;
        mDisplayedEntitiesRepository = displayedEntitiesRepository;
        mMapper = mapper;
        mNewMessageIndicationRepository = newMessageIndicationRepository;
        mDisplayer = displayer;
    }

    @Override
    protected Iterable<SubscriptionRequest> buildSubscriptionRequests(
            DisplaySubscriptionRequest.DisplaySubscriptionRequestFactory factory) {

        DisplaySubscriptionRequest displayMessages = factory.create(
                mMessagesRepository.getMessagesObservable(),
                this::transformToPresentation,
                mDisplayer::show);

        DisplaySubscriptionRequest notifyChangedMessages = factory.create(
                mDisplayedEntitiesRepository.getObservable(),
                observable -> observable
                        .map(notification -> notification.getGeoEntity().getId())
                        .map(mMapper::getMessageId)
                        .map(mMessagesRepository::getMessage)
                        .filter(m -> m != null)
                        .map(this::createMessagePresentation),
                mDisplayer::show);

        return Arrays.asList(displayMessages, notifyChangedMessages);
    }

    private Observable<MessagePresentation> transformToPresentation(
            Observable<Message> messageObservable) {
        return messageObservable
                .map(this::createMessagePresentation);
    }

    private MessagePresentation createMessagePresentation(Message message) {
        boolean isShownOnMap = isShownOnMap(message);
        boolean isFromSelf = mMessagesUtil.isMessageFromSelf(message);
        boolean isNotified = isBefore(message.getCreatedAt(),
                mNewMessageIndicationRepository.get());
        boolean isSelected = isSelected(message);

        return new MessagePresentation.Builder(message)
                .setIsFromSelf(isFromSelf)
                .setIsShownOnMap(isShownOnMap)
                .setIsNotified(isNotified)
                .setIsSelected(isSelected)
                .build();
    }

    private boolean isShownOnMap(Message message) {
        if (!(message instanceof GeoEntityHolder)) {
            return false;
        }
        GeoEntityHolder bmg = (GeoEntityHolder) message;
        return mDisplayedEntitiesRepository.isShown(bmg.getGeoEntity());
    }

    private boolean isBefore(Date date, Date other) {
        return date.compareTo(other) <= 0;
    }
    private boolean isSelected(Message message) {
        Message selectedMessage = mMessagesRepository.getSelectedMessage();

        return selectedMessage != null &&
                message.getMessageId().equals(selectedMessage.getMessageId());

    }

    public interface Displayer {
        void show(MessagePresentation message);
    }
}
