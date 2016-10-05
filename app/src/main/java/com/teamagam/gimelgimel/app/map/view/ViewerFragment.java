package com.teamagam.gimelgimel.app.map.view;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.GGApplication;
import com.teamagam.gimelgimel.app.map.cesium.OnGGMapReadyListener;
import com.teamagam.gimelgimel.app.map.model.VectorLayer;
import com.teamagam.gimelgimel.app.map.model.geometries.PointGeometry;
import com.teamagam.gimelgimel.app.map.viewModel.IMapView;
import com.teamagam.gimelgimel.app.map.viewModel.MapViewModel;
import com.teamagam.gimelgimel.app.map.viewModel.gestures.GGMapGestureListener;
import com.teamagam.gimelgimel.app.message.view.SendGeographicMessageDialog;
import com.teamagam.gimelgimel.app.model.ViewsModels.Message;
import com.teamagam.gimelgimel.app.utils.Constants;
import com.teamagam.gimelgimel.app.view.MainActivity;
import com.teamagam.gimelgimel.app.view.fragments.BaseFragment;
import com.teamagam.gimelgimel.databinding.FragmentCesiumBinding;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Viewer Fragmant that handles all map events.
 */
public class ViewerFragment extends BaseFragment<GGApplication> implements OnGGMapReadyListener,
        IMapView {

    @BindView(R.id.gg_map_view)
    GGMapView mGGMapView;

    private boolean mIsRestored;

    @Inject
    MapViewModel mMapViewModel;

    @Override
    @NotNull
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        FragmentCesiumBinding bind = DataBindingUtil.bind(rootView);

        ((MainActivity) getActivity()).getMainActivityComponent().inject(this);
        mMapViewModel.setMapView(this);

        bind.setMap(mMapViewModel);

        mGGMapView.setGGMapGestureListener(new GGMapGestureListener(mGGMapView, this));

        if (savedInstanceState != null) {
            mGGMapView.restoreViewState(savedInstanceState);
            mIsRestored = true;
        } else {
            mIsRestored = false;
        }

        secureGGMapViewInitialization();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapViewModel.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapViewModel.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapViewModel.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mGGMapView.saveViewState(outState);
    }


    private void secureGGMapViewInitialization() {
        if (mGGMapView.isReady()) {
            onGGMapViewReady();
        } else {
            mGGMapView.setOnReadyListener(this);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_cesium;
    }

    @Override
    public void onDetach() {
        mMapViewModel.destroy();
        super.onDetach();
    }

    public void goToLocation(PointGeometry pointGeometry) {
        mGGMapView.flyTo(pointGeometry);
    }

    @Override
    public void addLayer(VectorLayer vectorLayer) {
        mGGMapView.addLayer(vectorLayer);
    }

    @Override
    public void openSendGeoDialog(PointGeometry pointGeometry) {
        SendGeographicMessageDialog.newInstance(pointGeometry, this)
                .show(this.getFragmentManager(), "sendCoordinatesDialog");
    }

    public GGMap getGGMap() {
        return mGGMapView;
    }

    @Override
    public void onGGMapViewReady() {
        if (!mIsRestored) {
            setInitialMapExtent();
        }
        mMapViewModel.mapReady();
    }

    public void clearSentLocationsLayer() {
        mMapViewModel.clearSentLocationsLayer();
    }

    public void clearReceivedLocationsLayer() {
        mMapViewModel.clearReceivedLocationsLayer();
    }

    /**
     * Sets GGMapView extent to configured bounding box values
     */
    private void setInitialMapExtent() {
        float east = Constants.MAP_VIEW_INITIAL_BOUNDING_BOX_EAST;
        float west = Constants.MAP_VIEW_INITIAL_BOUNDING_BOX_WEST;
        float north = Constants.MAP_VIEW_INITIAL_BOUNDING_BOX_NORTH;
        float south = Constants.MAP_VIEW_INITIAL_BOUNDING_BOX_SOUTH;
        mGGMapView.setExtent(west, south, east, north);
    }

    public void addMessageLocationPin(Message message) {
        mMapViewModel.addMessageLocationPin(message);
    }
}
