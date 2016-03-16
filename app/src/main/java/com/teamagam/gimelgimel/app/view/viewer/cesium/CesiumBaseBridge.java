package com.teamagam.gimelgimel.app.view.viewer.cesium;

import android.util.Log;
import android.webkit.ValueCallback;

/**
 * Created by Yoni on 3/7/2016.
 */
public abstract class CesiumBaseBridge {

    protected JavascriptCommandExecutor mJsExecutor;
    protected final String LOG_TAG = this.getClass().getSimpleName();

    public CesiumBaseBridge(JavascriptCommandExecutor javascriptCommandExecutor) {
        Log.d(LOG_TAG, "starting JS Bridge");
        mJsExecutor = javascriptCommandExecutor;
    }

    /***
     * An interface used to inject {@link CesiumVectorLayersBridge}
     * with Javascript execution capability
     */
    public interface JavascriptCommandExecutor {

        void executeJsCommand(String line);
        void executeJsCommandForResult(String line, ValueCallback<String> callback);
    }

}
