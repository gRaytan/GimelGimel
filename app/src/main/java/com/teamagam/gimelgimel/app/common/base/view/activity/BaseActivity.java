package com.teamagam.gimelgimel.app.common.base.view.activity;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.teamagam.gimelgimel.app.GGApplication;
import com.teamagam.gimelgimel.app.common.base.view.ActivitySubcomponent;
import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.injectors.components.ApplicationComponent;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;

public abstract class BaseActivity<T extends Application> extends AppCompatActivity {

    protected final AppLogger sLogger = AppLoggerFactory.create(((Object) this).getClass());

    protected T mApp;

    protected boolean mIsResumed = false;

    private Collection<ActivitySubcomponent> mSubcomponents;

    public BaseActivity() {
        mSubcomponents = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        sLogger.userInteraction("Back key pressed");
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sLogger.onCreate();
        super.onCreate(savedInstanceState);

        // Just inflate the activity layout
        int layoutResId = getActivityLayout();
        if (layoutResId > 0) {
            setContentView(layoutResId);
        }

        // Set application object reference
        mApp = (T) getApplication();

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onCreate();
        }

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        sLogger.onStart();
        super.onStart();

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onStart();
        }
    }

    @Override
    protected void onResume() {
        sLogger.onResume();
        super.onResume();

        mIsResumed = true;

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onResume();
        }
    }

    @Override
    protected void onPause() {
        sLogger.onPause();
        super.onPause();

        mIsResumed = false;

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onPause();
        }
    }

    @Override
    protected void onStop() {
        sLogger.onStop();
        super.onStop();

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        sLogger.onDestroy();
        super.onDestroy();

        for (ActivitySubcomponent as : mSubcomponents) {
            as.onDestroy();
        }
    }

    protected abstract int getActivityLayout();

    protected ApplicationComponent getApplicationComponent() {
        return ((GGApplication) getApplication()).getApplicationComponent();
    }

    protected void attachSubcomponent(ActivitySubcomponent activitySubcomponent) {
        mSubcomponents.add(activitySubcomponent);
    }
}