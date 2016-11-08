package com.teamagam.gimelgimel.app.map.viewModel;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.common.logging.LoggerFactory;
import com.teamagam.gimelgimel.app.control.sensors.LocationFetcher;
import com.teamagam.gimelgimel.app.injectors.scopes.PerActivity;
import com.teamagam.gimelgimel.app.map.cesium.MapEntityClickedListener;
import com.teamagam.gimelgimel.app.map.model.EntityUpdateEventArgs;
import com.teamagam.gimelgimel.app.map.model.entities.Entity;
import com.teamagam.gimelgimel.app.map.model.geometries.PointGeometryApp;
import com.teamagam.gimelgimel.app.map.view.GGMapView;
import com.teamagam.gimelgimel.app.map.view.ViewerFragment;
import com.teamagam.gimelgimel.app.map.viewModel.adapters.GeoEntityTransformer;
import com.teamagam.gimelgimel.app.map.viewModel.gestures.GGMapGestureListener;
import com.teamagam.gimelgimel.app.map.viewModel.gestures.OnMapGestureListener;
import com.teamagam.gimelgimel.app.message.model.contents.LocationSample;
import com.teamagam.gimelgimel.app.message.view.SendMessageDialogFragment;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageMapEntitiesViewModel;
import com.teamagam.gimelgimel.app.model.ViewsModels.UsersLocationViewModel;
import com.teamagam.gimelgimel.app.utils.Constants;
import com.teamagam.gimelgimel.app.view.Navigator;
import com.teamagam.gimelgimel.domain.base.logging.Logger;
import com.teamagam.gimelgimel.domain.base.subscribers.SimpleSubscriber;
import com.teamagam.gimelgimel.domain.map.GetMapVectorLayersInteractorFactory;
import com.teamagam.gimelgimel.domain.map.LoadViewerCameraInteractor;
import com.teamagam.gimelgimel.domain.map.LoadViewerCameraInteractorFactory;
import com.teamagam.gimelgimel.domain.map.SaveViewerCameraInteractorFactory;
import com.teamagam.gimelgimel.domain.map.SyncMapVectorLayersInteractor;
import com.teamagam.gimelgimel.domain.map.SyncMapVectorLayersInteractorFactory;
import com.teamagam.gimelgimel.domain.map.ViewerCameraController;
import com.teamagam.gimelgimel.domain.map.entities.ViewerCamera;
import com.teamagam.gimelgimel.domain.map.entities.geometries.Geometry;
import com.teamagam.gimelgimel.domain.map.entities.geometries.PointGeometry;
import com.teamagam.gimelgimel.domain.map.entities.mapEntities.GeoEntity;
import com.teamagam.gimelgimel.domain.notifications.entity.GeoEntityNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * View Model that handles map related callbacks from user in {@link ViewerFragment}.
 * Also handles it's view {@link GGMapView}.
 * <p>
 * Controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class MapViewModel implements ViewerCameraController, MapEntityClickedListener {
//implements SendGeographicMessageDialog.SendGeographicMessageDialogInterface

    private IMapView mMapView;

    //interactors
    private SyncMapVectorLayersInteractor mSyncMapEntitiesInteractor;

    //injects
    @Inject
    MessageMapEntitiesViewModel mMessageLocationVM;

    @Inject
    UsersLocationViewModel mUserLocationsVM;

    @Inject
    GeoEntityTransformer mGeoEntityTransformer;

    //factories
    @Inject
    GetMapVectorLayersInteractorFactory getMapEntitiesInteractorFactory;

    @Inject
    SyncMapVectorLayersInteractorFactory syncMapEntitiesInteractorFactory;

    @Inject
    LoadViewerCameraInteractorFactory mLoadFactory;

    @Inject
    SaveViewerCameraInteractorFactory mSaveFactory;

    @Inject
    Navigator mNavigator;

    private final Activity mActivity;

    private Context mContext;

    //logger
    private Logger sLogger = LoggerFactory.create(getClass());
    private ViewerCamera mCurrentViewerCamera;
    private LoadViewerCameraInteractor mLoadViewerCameraInteractor;
    private List<String> mVectorLayers;

    @Inject
    public MapViewModel(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mVectorLayers = new ArrayList<>();
    }

    public void setMapView(IMapView mapView) {
        mMapView = mapView;
    }

    public void start() {
    }

    public void resume() {

    }

    public void pause() {

    }

    public void stop() {
        saveCurrentViewerCamera();
    }

    public void destroy() {
        if (mSyncMapEntitiesInteractor != null) {
            mSyncMapEntitiesInteractor.unsubscribe();
        }
        if (mLoadViewerCameraInteractor != null) {
            mLoadViewerCameraInteractor.unsubscribe();
        }
    }

    public void sendMessageClicked() {
        sLogger.userInteraction("Send message button clicked");
        new SendMessageDialogFragment()
                .show(mActivity.getFragmentManager(), "sendMessageDialog");
    }

    public void zoomToLastKnownLocation() {
        sLogger.userInteraction("Locate me button clicked");

        LocationSample lastKnownLocation = LocationFetcher.getInstance(
                mContext).getLastKnownLocation();

        if (lastKnownLocation == null) {
            Toast.makeText(mContext, R.string.locate_me_fab_no_known_location,
                    Toast.LENGTH_SHORT).show();
        } else {
            PointGeometryApp location = lastKnownLocation.getLocation();

            location.altitude = Constants.LOCATE_ME_BUTTON_ALTITUDE_METERS;
            mMapView.lookAt(location);
        }
    }

    public void mapReady() {
        mLoadViewerCameraInteractor = mLoadFactory.create(this);
        mLoadViewerCameraInteractor.execute();

        getMapEntitiesInteractorFactory.create(new GetMapVectorLayersSubscriber()).execute();

        mSyncMapEntitiesInteractor = syncMapEntitiesInteractorFactory.create(
                new SyncMapVectorLayersSubscriber());
        mSyncMapEntitiesInteractor.execute();

        mMapView.getViewerCameraObservable().subscribe(new SimpleSubscriber<ViewerCamera>() {
            @Override
            public void onNext(ViewerCamera viewerCamera) {
                mCurrentViewerCamera = viewerCamera;
            }
        });
    }

    private void drawAll(Collection<GeoEntity> geoEntities) {
        for (GeoEntity geoEntity : geoEntities) {
            updateMapEntity(GeoEntityNotification.createAdd(geoEntity));
        }
    }

    private void updateMapEntity(GeoEntityNotification geoEntityNotification) {
        String layerTag = geoEntityNotification.getGeoEntity().getLayerTag();
        createVectorLayerIfNeeded(layerTag);

        Entity entity = mGeoEntityTransformer.transform(geoEntityNotification.getGeoEntity());
        int eventType;
        EntityUpdateEventArgs entityUpdateEventArgs = null;
        switch (geoEntityNotification.getAction()) {
            case GeoEntityNotification.ADD:
                eventType = EntityUpdateEventArgs.LAYER_CHANGED_EVENT_TYPE_ADD;
                entityUpdateEventArgs = new EntityUpdateEventArgs(layerTag, entity, eventType);
                break;
            case GeoEntityNotification.REMOVE:
                eventType = EntityUpdateEventArgs.LAYER_CHANGED_EVENT_TYPE_REMOVE;
                entityUpdateEventArgs = new EntityUpdateEventArgs(layerTag, entity, eventType);
                break;
            case GeoEntityNotification.UPDATE:
                eventType = EntityUpdateEventArgs.LAYER_CHANGED_EVENT_TYPE_UPDATE;
                entityUpdateEventArgs = new EntityUpdateEventArgs(layerTag, entity, eventType);
                break;
            default:
        }
        mMapView.updateMapEntity(entityUpdateEventArgs);
    }

    @Override
    public void setViewerCamera(Geometry geometry) {
        PointGeometry pg = (PointGeometry) geometry;
        PointGeometryApp pointGeometry = PointGeometryApp.create(pg);

        mMapView.lookAt(pointGeometry);
    }

    @Override
    public void setViewerCamera(ViewerCamera viewerCamera) {
        mMapView.setCameraPosition(viewerCamera);
    }

    private void saveCurrentViewerCamera() {
        mSaveFactory.create(mCurrentViewerCamera).execute();
    }

    public OnMapGestureListener getGestureListener() {
        return new GGMapGestureListener(this, mMapView);
    }

    public ViewerCamera getViewerCamera() {
        return mCurrentViewerCamera;
    }

    public void openSendGeoDialog(PointGeometryApp pointGeometry) {
        mNavigator.navigateToSendGeoMessage(pointGeometry, mActivity);
    }

    private void createVectorLayerIfNeeded(String layerTag) {
        if (!mVectorLayers.contains(layerTag)) {
            mVectorLayers.add(layerTag);
            mMapView.addLayer(layerTag);
        }
    }

    @Override
    public void entityClicked(String layerId, String entityId) {
        //todo: complete
        Toast.makeText(mActivity, "entity clicked: " + entityId + ": " + layerId, Toast
                .LENGTH_LONG).show();
    }

    private class GetMapVectorLayersSubscriber extends SimpleSubscriber<Collection<GeoEntity>> {
        @Override
        public void onNext(Collection<GeoEntity> geoEntities) {
            drawAll(geoEntities);
        }
    }

    private class SyncMapVectorLayersSubscriber extends SimpleSubscriber<GeoEntityNotification> {

        @Override
        public void onNext(GeoEntityNotification geoEntityNotification) {
            updateMapEntity(geoEntityNotification);
        }

        @Override
        public void onError(Throwable e) {
            sLogger.e("point next error: ", e);
        }
    }
}
