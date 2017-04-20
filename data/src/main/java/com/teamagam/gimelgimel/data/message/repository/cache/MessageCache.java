package com.teamagam.gimelgimel.data.message.repository.cache;


import com.teamagam.gimelgimel.domain.messages.entity.Message;

import java.util.List;

import rx.Observable;

public interface MessageCache {

    Message getMessage(String messageId);

    List<Message> getMessages();

    Observable<Message> getMessagesObservable();

    Observable<Integer> getNumMessagesObservable();

    void saveMessage(Message message);

    void saveMessage(List<Message> messages);
}
