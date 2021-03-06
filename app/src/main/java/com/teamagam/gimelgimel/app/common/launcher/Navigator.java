package com.teamagam.gimelgimel.app.common.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.teamagam.gimelgimel.app.location.GoToLocationDialogFragment;
import com.teamagam.gimelgimel.app.location.TurnOnGpsDialogFragment;
import com.teamagam.gimelgimel.app.mainActivity.view.MainActivityConnectivityAlerts;
import com.teamagam.gimelgimel.app.map.model.geometries.PointGeometryApp;
import com.teamagam.gimelgimel.app.map.view.DrawActionActivity;
import com.teamagam.gimelgimel.app.message.view.ImageFullscreenActivity;
import com.teamagam.gimelgimel.app.message.view.SendGeographicMessageDialog;

import javax.inject.Inject;

/**
 * Navigator for creating and navigating between views.
 */

public class Navigator {

    private static final String TAG_FRAGMENT_TURN_ON_GPS_DIALOG =
            MainActivityConnectivityAlerts.class.getSimpleName() + "TURN_ON_GPS";


    private final Activity mActivity;

    @Inject
    public Navigator(Activity activity) {
        mActivity = activity;
    }

    /**
     * Opens the full image view (activity) {@link ImageFullscreenActivity}.
     * <p>
     * the context is always the application's context.
     *
     * @param imageUri
     */
    public void navigateToFullScreenImage(Uri imageUri) {
        Intent intentToLaunch = ImageFullscreenActivity.getCallingIntent(mActivity);
        intentToLaunch.setData(imageUri);
        intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intentToLaunch);
    }


    public void navigateToSendGeoMessage(PointGeometryApp pointGeometry) {
        SendGeographicMessageDialog.newInstance(pointGeometry)
                .show(mActivity.getFragmentManager(), "sendCoordinatesDialog");
    }

    public void navigateToTurnOnGPSDialog() {
        TurnOnGpsDialogFragment dialogFragment = new TurnOnGpsDialogFragment();
        dialogFragment.show(mActivity.getFragmentManager(), TAG_FRAGMENT_TURN_ON_GPS_DIALOG);
    }

    public void openSendQuadrilateralAction() {
        DrawActionActivity.startSendQuadAction(mActivity);
    }

    public void openMeasureDistanceAction() {
        DrawActionActivity.startMeasureAction(mActivity);
    }

    public static void openGoToDialog(Activity activity) {
        GoToLocationDialogFragment.newInstance().show(activity.getFragmentManager(), "gotodialogtag");
    }
}
