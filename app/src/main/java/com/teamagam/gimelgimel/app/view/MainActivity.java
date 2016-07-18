package com.teamagam.gimelgimel.app.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.GGApplication;
import com.teamagam.gimelgimel.app.common.logging.Logger;
import com.teamagam.gimelgimel.app.common.logging.LoggerFactory;
import com.teamagam.gimelgimel.app.control.receivers.GpsStatusBroadcastReceiver;
import com.teamagam.gimelgimel.app.control.sensors.LocationFetcher;
import com.teamagam.gimelgimel.app.network.receivers.ConnectivityStatusReceiver;
import com.teamagam.gimelgimel.app.network.services.GGMessageLongPollingService;
import com.teamagam.gimelgimel.app.view.fragments.ViewerFragment;
import com.teamagam.gimelgimel.app.view.fragments.dialogs.GoToDialogFragment;
import com.teamagam.gimelgimel.app.view.fragments.dialogs.TurnOnGpsDialogFragment;
import com.teamagam.gimelgimel.app.view.fragments.messags_panel_fragments.MessagesContainerFragment;
import com.teamagam.gimelgimel.app.view.fragments.messags_panel_fragments.MessagesDetailBaseGeoFragment;
import com.teamagam.gimelgimel.app.view.fragments.viewer_footer_fragments.BaseViewerFooterFragment;
import com.teamagam.gimelgimel.app.view.fragments.viewer_footer_fragments.MapManipulationFooterFragment;
import com.teamagam.gimelgimel.app.view.fragments.viewer_footer_fragments.VectorManipulationFooterFragment;
import com.teamagam.gimelgimel.app.view.settings.SettingsActivity;
import com.teamagam.gimelgimel.app.view.viewer.GGMap;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<GGApplication>
        implements
        GoToDialogFragment.GoToDialogFragmentInterface,
        BaseViewerFooterFragment.MapManipulationInterface,
        ConnectivityStatusReceiver.NetworkAvailableListener,
        MessagesDetailBaseGeoFragment.GeoMessageInterface {

    private static final Logger sLogger = LoggerFactory.create(MainActivity.class);


    @BindView(R.id.no_gps_signal_text_view)
    TextView mNoGpsTextView;

    @BindView(R.id.no_network_text_view)
    TextView mNoNetworkTextView;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.activity_main_layout)
    SlidingUpPanelLayout mSlidingLayout;

    // Represents the tag of the added fragments
    private final String TAG_FRAGMENT_TURN_ON_GPS_DIALOG = TAG + "TURN_ON_GPS";
    private final String TAG_FRAGMENT_MAP_CESIUM = TAG + "TAG_FRAGMENT_GG_CESIUM";

    //app fragments
    private ViewerFragment mViewerFragment;
    private MessagesContainerFragment mMessagesContainerFragment;

    //adapters
    private LocationFetcher mLocationFetcher;
    private ConnectivityStatusReceiver mConnectivityStatusReceiver;
    private GpsStatusAlertBroadcastReceiver mGpsStatusAlertBroadcastReceiver;

    // Listeners
    private SlidingPanelListener mPanelListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToolbar.inflateMenu(R.menu.main);

        initialize(savedInstanceState);

        // creating the menu of the left side
        createLeftDrawer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GGMessageLongPollingService.startMessageLongPollingAsync(this);
        // Register to receive messages.
        // We are registering an observer

        IntentFilter intentFilter = new IntentFilter(ConnectivityStatusReceiver.INTENT_NAME);

        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectivityStatusReceiver,
                intentFilter);

        intentFilter = new IntentFilter(GpsStatusBroadcastReceiver.BROADCAST_NEW_GPS_STATUS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mGpsStatusAlertBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        GGMessageLongPollingService.stopMessagePollingAsync(this);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectivityStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGpsStatusAlertBroadcastReceiver);

        mSlidingLayout.removePanelSlideListener(mPanelListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                sLogger.userInteraction("Opened drawer");
                return true;
            case R.id.action_settings:
                sLogger.userInteraction("Settings menu option item clicked");
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        // Stub for future use
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void goToLocation(PointGeometry pointGeometry) {
        mViewerFragment.goToLocation(pointGeometry);
    }

    @Override
    public void drawPin(PointGeometry pointGeometry) {
        mViewerFragment.drawPin(pointGeometry);
    }

    @Override
    public GGMap getGGMap() {
        return mViewerFragment.getGGMap();
    }

    @Override
    public void onNetworkAvailableChange(boolean isNetworkAvailable) {
        sLogger.v("Network status: " + isNetworkAvailable);

        int visibility = isNetworkAvailable ? View.GONE : View.VISIBLE;
        mNoNetworkTextView.setVisibility(visibility);
        mNoNetworkTextView.bringToFront();
    }

    public void onGpsStopped() {
        sLogger.v("Gps status: stopped");

        setDisplayNoGpsView(true);
    }

    public void onGpsStarted() {
        sLogger.v("Gps status: started");

        setDisplayNoGpsView(false);
    }

    private void initialize(Bundle savedInstanceState) {
        initFragments(savedInstanceState);
        initBroadcastReceivers();
        initGpsStatus();
        initSlidingUpPanel();
    }

    private void initGpsStatus() {
        mLocationFetcher = LocationFetcher.getInstance(this);

        if (!mLocationFetcher.isGpsProviderEnabled()) {
            setDisplayNoGpsView(true);

            TurnOnGpsDialogFragment dialogFragment = new TurnOnGpsDialogFragment();
            dialogFragment.show(getFragmentManager(), TAG_FRAGMENT_TURN_ON_GPS_DIALOG);
        }
    }

    private void initFragments(Bundle savedInstanceState) {

        FragmentManager fragmentManager = getFragmentManager();
        //fragments inflated by xml
        mViewerFragment = (ViewerFragment) fragmentManager.findFragmentById(R.id.fragment_cesium_view);
        mMessagesContainerFragment =
                (MessagesContainerFragment) fragmentManager.findFragmentById(R.id.fragment_messages_container);
    }

    private void initBroadcastReceivers() {
        //create broadcast receiver
        mConnectivityStatusReceiver = new ConnectivityStatusReceiver(this);
        mGpsStatusAlertBroadcastReceiver = new GpsStatusAlertBroadcastReceiver();
    }

    private void initSlidingUpPanel() {
        mPanelListener = new SlidingPanelListener();

        mSlidingLayout.addPanelSlideListener(mPanelListener);
    }

    private void createLeftDrawer() {
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupDrawerContent();
    }

    private void setupDrawerContent() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationItemSelectedListener());
    }

    /**
     * Sets the visibility of the "no gps" alert textview
     *
     * @param displayState - true will make the view visible, false will be it gone
     */
    private void setDisplayNoGpsView(boolean displayState) {
        int visibility = displayState ? View.VISIBLE : View.GONE;
        mNoGpsTextView.setVisibility(visibility);
        mNoGpsTextView.bringToFront();
    }

    /**
     * Listens to an item click from the {@link NavigationView}.
     * Changes the footer container based on the clicked item.
     */
    private class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            createFragmentByMenuItem(item);
            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        }

        private void createFragmentByMenuItem(MenuItem item) {
            // Currently we use the footer the show views from the Drawer
            // We should change this to more flexible code to support other views
            Fragment fragmentToDisplay = getFragmentManager().findFragmentById(R.id.activity_main_container_footer);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.nav_home:
                    removeFragment(fragmentTransaction, fragmentToDisplay);
                    fragmentToDisplay = null;
                    break;
                case R.id.nav_vector:
                    fragmentToDisplay = new VectorManipulationFooterFragment();
                    break;
                case R.id.nav_map:
                    fragmentToDisplay = new MapManipulationFooterFragment();
                    break;
            }

            if (fragmentToDisplay != null) {
                displayFragment(fragmentTransaction, fragmentToDisplay);
            }
        }

        private void removeFragment(FragmentTransaction fragmentTransaction, Fragment fragmentToRemove) {
            if (fragmentToRemove != null) {
                fragmentTransaction.remove(fragmentToRemove);
                fragmentTransaction.commit();
            }
        }

        private void displayFragment(FragmentTransaction fragmentTransaction,
                                     @Nullable Fragment fragmentToDisplay) {
            fragmentTransaction.replace(R.id.activity_main_container_footer, fragmentToDisplay);
            fragmentTransaction.commit();
        }
    }

    private class GpsStatusAlertBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isGpsOn = intent.getBooleanExtra(GpsStatusBroadcastReceiver.GPS_STATUS_EXTRA, true);

            if (isGpsOn) {
                MainActivity.this.onGpsStarted();
            } else {
                MainActivity.this.onGpsStopped();
            }
        }
    }

    private class SlidingPanelListener implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            int height = calculateHeight(slideOffset);
            mMessagesContainerFragment.onHeightChanged(height);
        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        }

        private int calculateHeight(final float slideOffset) {
            int layoutHeight = mSlidingLayout.getHeight();
            int panelHeight = mSlidingLayout.getPanelHeight();

            return (int) ((layoutHeight - panelHeight) * slideOffset);
        }
    }
}