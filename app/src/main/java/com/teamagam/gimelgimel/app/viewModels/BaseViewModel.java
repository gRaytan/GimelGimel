package com.teamagam.gimelgimel.app.viewModels;

import android.databinding.BaseObservable;

import com.teamagam.gimelgimel.app.common.logging.LoggerFactory;
import com.teamagam.gimelgimel.domain.base.logging.Logger;

/**
 * Created on 10/10/2016.
 */

public abstract class BaseViewModel<V> extends BaseObservable implements
        ViewModel<V> {

    protected Logger sLogger = LoggerFactory.create(((Object) this).getClass());

    protected V mView;

    @Override
    public void setView(V v){
        mView = v;
    }

    @Override
    public void stop() {
        //no-op
    }

    @Override
    public void start() {
        //no-op
    }
}
