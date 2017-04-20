package com.teamagam.gimelgimel.data.message.repository;

import com.teamagam.gimelgimel.data.message.adapters.MessageDataMapper;
import com.teamagam.gimelgimel.data.message.repository.cache.MessageCache;
import com.teamagam.gimelgimel.data.message.repository.cloud.CloudMessagesSource;
import com.teamagam.gimelgimel.domain.messages.entity.Message;
import com.teamagam.gimelgimel.domain.messages.repository.MessagesRepository;

import rx.Observable;

public class MessagesDataRepository implements MessagesRepository {

    private final CloudMessagesSource mSource;
    private final MessageCache mCache;
    private final SelectedMessageRepository mSelectedRepo;
    private final MessageDataMapper mMessageDataMapper;

    public MessagesDataRepository(CloudMessagesSource cloudMessagesSource,
                                  MessageCache messageCache,
                                  SelectedMessageRepository selectedMessageRepository,
                                  MessageDataMapper messageDataMapper) {
        mSource = cloudMessagesSource;
        mCache = messageCache;
        mMessageDataMapper = messageDataMapper;
        mSelectedRepo = selectedMessageRepository;
    }

    @Override
    public Observable<Message> getMessagesObservable() {
        return mCache.getMessagesObservable();
    }

    @Override
    public Observable<Message> getSelectedMessageObservable() {
        return mSelectedRepo.getSelectedMessageObservable();
    }

    @Override
    public Message getSelectedMessage() {
        return mSelectedRepo.getSelectedMessage();
    }

    @Override
    public void putMessage(Message message) {
        mCache.saveMessage(message);
    }

    @Override
    public Observable<Message> sendMessage(Message message) {
        return mSource.sendMessage(mMessageDataMapper.transformToData(message))
                .map(mMessageDataMapper::transform);
    }

    @Override
    public Message getMessage(String messageId) {
        return mCache.getMessage(messageId);
    }

    @Override
    public void selectMessage(Message message) {
        mSelectedRepo.select(message);
    }
}