package com.teamagam.gimelgimel.app.view.viewer.cesium;

import com.teamagam.gimelgimel.app.view.viewer.cesium.bridges.BaseCesiumEntitiesHandler;
import com.teamagam.gimelgimel.app.view.viewer.cesium.bridges.CesiumVectorLayersBridge;

/**
 * Created by Bar on 06-Mar-16.
 */
public class CesiumEntitiesUpdater extends BaseCesiumEntitiesHandler {

    public CesiumEntitiesUpdater(String layerJsName,
                                 CesiumVectorLayersBridge.JavascriptCommandExecutor executor) {
        super("update", layerJsName, executor);
    }
}
