package com.teamagam.gimelgimel.presentation.presenters;

import com.teamagam.gimelgimel.domain.geometries.entities.PointGeometry;
import com.teamagam.gimelgimel.domain.images.SendImageMessageInteractor;
import com.teamagam.gimelgimel.domain.messages.entity.MessageImage;
import com.teamagam.gimelgimel.domain.messages.entity.contents.ImageMetadata;
import com.teamagam.gimelgimel.presentation.presenters.base.AbstractPresenter;
import com.teamagam.gimelgimel.presentation.presenters.base.BaseView;
import com.teamagam.gimelgimel.presentation.rx.subscribers.BaseSubscriber;

import javax.inject.Inject;

import rx.Subscriber;

public class SendImageMessagePresenter extends AbstractPresenter {

    private final static String IMAGE_SOURCE = "User";

    private View mView;
    private SendImageMessageInteractor mImageMessageInteractor;

    @Inject
    public SendImageMessagePresenter(SendImageMessageInteractor interactor, View view) {
        mImageMessageInteractor = interactor;
        mView = view;
    }

    @Override
    public void resume() {
        mView.hideProgress();
    }

    @Override
    public void pause() {
        mView.hideProgress();
    }

    @Override
    public void stop() {
        mView.hideProgress();
    }

    @Override
    public void destroy() {
        mView.hideProgress();
    }

    public void sendImageMessage(String senderId, String imagePath, PointGeometry geometry) {
        mView.showProgress();

        Subscriber subscriber = new SendImageMessageSubscriber();
        ImageMetadata metadata =
                new ImageMetadata(System.currentTimeMillis(), imagePath, geometry, IMAGE_SOURCE);

        if (geometry != null) {
            metadata.setLocation(geometry);
        }

        MessageImage message = new MessageImage(senderId, metadata);

        mImageMessageInteractor.sendImageMessage(subscriber, message, imagePath);
    }

    public interface View extends BaseView {
        void displayMessageStatus();
    }

    private class SendImageMessageSubscriber extends BaseSubscriber<MessageImage> {
        @Override
        public void onError(Throwable e) {
            //Exceptions.propagate(e);
            mView.showError(e.getMessage());
        }

        @Override
        public void onNext(MessageImage message) {
            mView.displayMessageStatus();
        }
    }
}
