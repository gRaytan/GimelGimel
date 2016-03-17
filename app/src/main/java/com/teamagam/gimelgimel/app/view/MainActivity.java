package com.teamagam.gimelgimel.app.view;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.GGApplication;
import com.teamagam.gimelgimel.app.control.sensors.GGLocation;
import com.teamagam.gimelgimel.app.view.fragments.ViewerFragment;
import com.teamagam.gimelgimel.app.view.fragments.FriendsFragment;
import com.teamagam.gimelgimel.app.view.settings.SettingsActivity;

//todo: clean
//import com.commonsware.cwac.wakeful.WakefulIntentService;
//import com.teamagam.gimelgimel.app.control.services.GGService;

public class MainActivity extends BaseActivity<GGApplication>
        implements ViewerFragment.OnFragmentInteractionListener {

    // Represents the tag of the added fragments
    private final String TAG_FRAGMENT_FRIENDS = TAG + "TAG_FRAGMENT_GG_FRIENDS";
    private final String TAG_FRAGMENT_MAP_CESIUM = TAG + "TAG_FRAGMENT_GG_CESIUM";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private FriendsFragment mFriendsFragment;
    private ViewerFragment mViewerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handling dynamic fragments section.
        // If this is the first time the Activity is created (and it's not a restart of it)
        if (savedInstanceState == null) {
            mFriendsFragment = new FriendsFragment();
            mViewerFragment = new ViewerFragment();
        }
        // Else, it's a restart, just fetch the already existing fragments
        else {
            FragmentManager fragmentManager = getFragmentManager();

            mFriendsFragment = (FriendsFragment) fragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_FRIENDS);
            mViewerFragment = (ViewerFragment) fragmentManager.findFragmentByTag(
                    TAG_FRAGMENT_MAP_CESIUM);
        }

        GGLocation gps = new GGLocation(this);
        Location loc = gps.getLocation();
        mTitle = getTitle();

        //todo: start both fragments.
        // Now do the actual swap of views
        if (!mIsTwoPane) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mViewerFragment, TAG_FRAGMENT_MAP_CESIUM)
                    .commit();
        }
        else{

        }

//        R.id.fragment_friends_recycler_view

        //todo: where to start service? login activity?
//        WakefulIntentService.sendWakefulWork(this, GGService.actionGetTipsIntent(this));


    }

    public void restoreActionBar() {
//        mToolbar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_friends:
                intent = new Intent(this, FriendsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //todo: complete interaction with fragment.
    }
}