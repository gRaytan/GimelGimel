package com.teamagam.gimelgimel.app.common.base.ViewModels;

import android.databinding.BaseObservable;

import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.domain.base.interactors.Interactor;

/**
 * Created on 10/10/2016.
 */

public abstract class BaseViewModel<V> extends BaseObservable implements
        ViewModel<V> {

    protected AppLogger sLogger = AppLoggerFactory.create(((Object) this).getClass());

    protected V mView;

    @Override
    public void setView(V v) {
        mView = v;
    }

    @Override
    public void init() {
        //no-op
    }

    @Override
    public void start() {
        //no-op
    }

    @Override
    public void stop() {
        //no-op
    }

    @Override
    public void destroy() {
        //no-op
    }

    protected void execute(Interactor... interactors) {
        for (Interactor interactor : interactors) {
            execute(interactor);
        }
    }

    protected void unsubscribe(Interactor... interactors) {
        for (Interactor interactor : interactors) {
            unsubscribe(interactor);
        }
    }

    private void execute(Interactor interactor) {
        if (interactor != null) {
            interactor.execute();
        }
    }

    private void unsubscribe(Interactor interactor) {
        if (interactor != null) {
            interactor.unsubscribe();
        }
    }
}
