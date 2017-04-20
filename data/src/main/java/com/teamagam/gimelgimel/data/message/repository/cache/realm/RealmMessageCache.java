package com.teamagam.gimelgimel.data.message.repository.cache.realm;

import com.teamagam.gimelgimel.data.message.adapters.MessageDataMapper;
import com.teamagam.gimelgimel.data.message.entity.MessageData;
import com.teamagam.gimelgimel.data.message.repository.cache.MessageCache;
import com.teamagam.gimelgimel.domain.messages.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;


public class RealmMessageCache implements MessageCache {

    private Realm mRealm;
    private MessageDataMapper mMessageMapper;
    private ExecutorService mExecutorService;

    public RealmMessageCache(ExecutorService executorService, MessageDataMapper messageMapper) {
        mExecutorService = executorService;
        mMessageMapper = messageMapper;
        mRealm = doRealmWork(Realm::getDefaultInstance);
    }

    @Override
    public Message getMessage(String messageId) {
        MessageData result = doRealmWork(() -> mRealm
                .where(MessageData.class)
                .equalTo("messageId", messageId)
                .findFirst());

        return mMessageMapper.transform(result);
    }

    @Override
    public List<Message> getMessages() {
        List<MessageData> list = doRealmWork(() -> mRealm
                .where(MessageData.class)
                .findAllSorted("createdAt", Sort.DESCENDING));

        return mMessageMapper.transform(list);
    }

    @Override
    public Observable<Message> getMessagesObservable() {
        return doRealmWork(() -> mRealm
                .where(MessageData.class)
                .findAllSortedAsync("createdAt", Sort.DESCENDING)
                .asObservable()
                .flatMapIterable(results -> results)
                .map(m -> mMessageMapper.transform(m)));
    }

    @Override
    public Observable<Integer> getNumMessagesObservable() {
        return doRealmWork(() -> mRealm
                .where(MessageData.class)
                .findAllAsync()
                .asObservable()
                .map(results -> results.size()));
    }

    @Override
    public void saveMessage(Message message) {
        final MessageData messageData = mMessageMapper.transformToData(message);
        doRealmWork(() ->
                mRealm.executeTransaction(
                        realm ->
                                realm.copyToRealmOrUpdate(messageData)));
    }

    @Override
    public void saveMessage(List<Message> messages) {
        List<MessageData> messagesData = new ArrayList<>(messages.size());
        for (Message message : messages) {
            messagesData.add(mMessageMapper.transformToData(message));
        }

        doRealmWork(() ->
                mRealm.executeTransaction(
                        realm ->
                                mRealm.copyToRealm(messagesData)
                ));
    }

    private <T> T doRealmWork(Callable<T> callable) {
        try {
            Future<T> futureObj = mExecutorService.submit(callable);
            return futureObj.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void doRealmWork(Runnable runnable) {
        mExecutorService.submit(runnable);
    }
}
