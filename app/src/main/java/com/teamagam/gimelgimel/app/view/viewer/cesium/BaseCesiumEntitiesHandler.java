package com.teamagam.gimelgimel.app.view.viewer.cesium;

import com.teamagam.gimelgimel.app.view.viewer.IEntitiesVisitor;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Point;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Polygon;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Polyline;

/**
 * Created by Bar on 07-Mar-16.
 */
public class BaseCesiumEntitiesHandler implements IEntitiesVisitor {
    //TODO: handle entities symbology

    private String mMethodPrefix;
    private String mLayerJsName;
    private CesiumVectorLayersBridge.JavascriptCommandExecutor mJsExecutor;

    public BaseCesiumEntitiesHandler(String methodPrefix, String layerJsName,
                                     CesiumVectorLayersBridge.JavascriptCommandExecutor executor) {
        this.mMethodPrefix = methodPrefix;
        this.mLayerJsName = layerJsName;
        this.mJsExecutor = executor;
    }

    @Override
    public void visit(Point point) {
        String pointLocationJson = CesiumUtils.getLocationJson(point);
        String symbolJson = CesiumUtils.getBillboardJson(point);

        String jsLine = String.format("%s.%sMarker(\"%s\",%s, %s);",
                mLayerJsName, mMethodPrefix, point.getId(), pointLocationJson, symbolJson);

        mJsExecutor.executeJsCommand(jsLine);
    }

    @Override
    public void visit(Polyline polyline) {
        String locationsJson = CesiumUtils.getLocationsJson(polyline);

        String jsLine = String.format("%s.%sPolyline(\"%s\",%s);",
                mLayerJsName, mMethodPrefix, polyline.getId(), locationsJson);

        mJsExecutor.executeJsCommand(jsLine);
    }

    @Override
    public void visit(Polygon polygon) {
        String locationsJson = CesiumUtils.getLocationsJson(polygon);

        String jsLine = String.format("%s.%sPolygon(\"%s\",%s);",
                mLayerJsName, mMethodPrefix, polygon.getId(), locationsJson);

        mJsExecutor.executeJsCommand(jsLine);
    }
}