package com.teamagam.gimelgimel.domain.messages;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.teamagam.gimelgimel.domain.base.executor.ThreadExecutor;
import com.teamagam.gimelgimel.domain.base.interactors.BaseDataInteractor;
import com.teamagam.gimelgimel.domain.base.interactors.DataSubscriptionRequest;
import com.teamagam.gimelgimel.domain.messages.repository.UnreadMessagesCountRepository;

import java.util.Collections;
import java.util.Date;

import rx.Observable;

@AutoFactory
public class UpdateMessagesReadInteractor extends BaseDataInteractor {

    private final UnreadMessagesCountRepository mUnreadMessagesCountRepository;
    private final Date mDate;

    public UpdateMessagesReadInteractor(
            @Provided ThreadExecutor threadExecutor,
            @Provided UnreadMessagesCountRepository unreadMessagesCountRepository,
            Date date) {
        super(threadExecutor);
        mUnreadMessagesCountRepository = unreadMessagesCountRepository;
        mDate = date;
    }

    @Override
    protected Iterable<SubscriptionRequest> buildSubscriptionRequests(
            DataSubscriptionRequest.SubscriptionRequestFactory factory) {
        DataSubscriptionRequest readAllMessages = factory.create(
                Observable.just(mDate),
                dateObservable ->
                        dateObservable
                                .filter(this::isNewerThanLastVisit)
                                .doOnNext(mUnreadMessagesCountRepository::updateLastVisit)
        );

        return Collections.singletonList(readAllMessages);
    }

    private boolean isNewerThanLastVisit(Date date) {
        return date.after(mUnreadMessagesCountRepository.getLastVisitTimestamp());
    }
}
