package com.teamagam.gimelgimel.app.view.viewer.cesium;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.teamagam.gimelgimel.BuildConfig;
import com.teamagam.gimelgimel.app.view.viewer.GGMapView;
import com.teamagam.gimelgimel.app.view.viewer.data.GGLayer;
import com.teamagam.gimelgimel.app.view.viewer.data.LayerChangedEventArgs;
import com.teamagam.gimelgimel.app.view.viewer.data.VectorLayer;
import com.teamagam.gimelgimel.app.view.viewer.data.entities.Entity;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;

import java.util.Collection;
import java.util.HashMap;

/**
 * Wrapper view class for a WebView-based Cesium viewer
 */
public class CesiumMapView extends WebView implements GGMapView, VectorLayer.LayerChangedListener {

    public static final String FILE_ANDROID_ASSET_VIEWER = "file:///android_asset/cesiumHelloWorld.html";

    private HashMap<String, GGLayer> mVectorLayers;
    private CesiumVectorLayersBridge mCesiumVectorLayersBridge;
    private CesiumMapBridge mCesiumMapBridge;
    private CesiumKMLBridge mCesiumKMLBridge;

    public CesiumMapView(Context context) {
        super(context);
        init(null, 0);
    }

    public CesiumMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CesiumMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mVectorLayers = new HashMap<>();
        CesiumBaseBridge.JavascriptCommandExecutor JSCommandExecutor = new CesiumBaseBridge.JavascriptCommandExecutor() {
            @Override
            public void executeJsCommand(String line) {
                loadUrl(String.format("javascript:%s", line));
            }

            @Override
            public void executeJsCommandForResult(String line, ValueCallback<String> callback) {
                evaluateJavascript(line, callback);
            }
        };

        mCesiumVectorLayersBridge = new CesiumVectorLayersBridge(JSCommandExecutor);
        mCesiumMapBridge = new CesiumMapBridge(JSCommandExecutor);
        mCesiumKMLBridge = new CesiumKMLBridge(JSCommandExecutor);

        WebSettings thisWebSettings = getSettings();
        thisWebSettings.setAllowUniversalAccessFromFileURLs(true);
        thisWebSettings.setAllowFileAccessFromFileURLs(true);
        thisWebSettings.setJavaScriptEnabled(true);

        //TODO: is necessary ?
        thisWebSettings.setUseWideViewPort(true);
        thisWebSettings.setLoadWithOverviewMode(true);
        setWebViewClient(new WebViewClient());
        //

        //For debug only
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true);
        }

        this.loadUrl(FILE_ANDROID_ASSET_VIEWER);
    }


    @Override
    public void addLayer(GGLayer layer) {
        String layerId = layer.getId();
        if (mVectorLayers.containsKey(layerId)) {
            throw new IllegalArgumentException("A layer with this id already exists!");
        }
        mVectorLayers.put(layerId, layer);

        if (layer instanceof VectorLayer) {
            mCesiumVectorLayersBridge.addLayer(layer);
            ((VectorLayer) layer).addLayerChangedListener(this);
        }
        else {//KML
            mCesiumKMLBridge.addLayer(layer);
        }


    }

    @Override
    public void removeLayer(GGLayer layer) {
        if (layer instanceof VectorLayer) {
            mCesiumVectorLayersBridge.removeLayer(layer);
            ((VectorLayer) layer).removeLayerChangedListener(this);
        }
        else {//KML
            mCesiumKMLBridge.removeLayer(layer);
        }

        String layerId = layer.getId();
        if (mVectorLayers.containsKey(layerId)) {
            mVectorLayers.remove(layerId);
        }
    }

    @Override
    public Collection<GGLayer> getLayers() {
        return mVectorLayers.values();
    }

    @Override
    public void setExtent(float west, float south, float east, float north) {
        mCesiumMapBridge.setExtent(west, south, east, north);
    }

    @Override
    public void setExtent(Collection<Entity> entities) {
        mCesiumMapBridge.setExtent(entities);
    }

    @Override
    public void zoomTo(float x, float y, float z) {
        mCesiumMapBridge.zoomTo(x, y, z);
    }

    @Override
    public void zoomTo(float x, float y) { mCesiumMapBridge.zoomTo(x,y);}

    @Override
    public void readAsyncPosition(ValueCallback<String> callback){
        if(callback == null)
            callback = new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value == null)
                        Log.w("Cesium Bridge", "no value returned");
                    else if (value.equals(""))
                        Log.w("Cesium Bridge", "empty returned");
                    PointGeometry point = CesiumUtils.getPointFromJson(value);
                    Log.d("Cesium Bridge", String.format("%s%s", point.latitude, point.longitude));
                    Toast.makeText(getContext(), String.format("N:%.4f E:%.4f", point.latitude, point.longitude), Toast.LENGTH_SHORT).show();
                }

                };
        mCesiumMapBridge.getPosition(callback);
    }


    @Override
    public void LayerChanged(LayerChangedEventArgs eventArgs) {
        switch (eventArgs.eventType) {
            case LayerChangedEventArgs.LAYER_CHANGED_EVENT_TYPE_ADD: {
                mCesiumVectorLayersBridge.addEntity(eventArgs.layerId, eventArgs.entity);
                break;
            }
            case LayerChangedEventArgs.LAYER_CHANGED_EVENT_TYPE_UPDATE: {
                mCesiumVectorLayersBridge.updateEntity(eventArgs.layerId, eventArgs.entity);
                break;
            }
            case LayerChangedEventArgs.LAYER_CHANGED_EVENT_TYPE_REMOVE: {
                mCesiumVectorLayersBridge.removeEntity(eventArgs.layerId, eventArgs.entity);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported layer changed event type!");
            }
        }
    }
}
