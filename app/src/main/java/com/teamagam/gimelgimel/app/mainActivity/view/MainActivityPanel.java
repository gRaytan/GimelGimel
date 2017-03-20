package com.teamagam.gimelgimel.app.mainActivity.view;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.common.base.adapters.BottomPanelPagerAdapter;
import com.teamagam.gimelgimel.app.common.base.view.ActivitySubcomponent;
import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.mainActivity.viewmodel.PanelViewModel;
import com.teamagam.gimelgimel.app.mainActivity.viewmodel.PanelViewModelFactory;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityPanel extends ActivitySubcomponent {

    private static final AppLogger sLogger = AppLoggerFactory.create();

    @Inject
    PanelViewModelFactory mPanelViewModelFactory;

    @BindView(R.id.activity_main_layout)
    SlidingUpPanelLayout mSlidingLayout;

    @BindView(R.id.main_activity_main_content)
    View mMainActivityContentLayout;

    @BindView(R.id.bottom_panel_tabs)
    PagerSlidingTabStrip mTabsStrip;

    @BindView(R.id.bottom_swiping_panel)
    ViewPager mBottomViewPager;

    @BindView(R.id.main_toolbar)
    View mToolbar;

    private final Activity mActivity;
    private PanelViewModel mViewModel;
    private SlidingPanelListener mPanelListener;
    private PageChangeListener mPageListener;
    private Unregistrar mKeyboardVisibilityUnregistrar;

    MainActivityPanel(FragmentManager fm, Activity activity) {
        mActivity = activity;
        ButterKnife.bind(this, mActivity);
        ((MainActivity) activity).getMainActivityComponent().inject(this);
        mViewModel = mPanelViewModelFactory.create(fm);
        mViewModel.setView(this);
        mViewModel.start();

        mTabsStrip.setViewPager(mBottomViewPager);
        mPanelListener = new SlidingPanelListener();
        mPageListener = new PageChangeListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSlidingLayout.addPanelSlideListener(mPanelListener);
        mBottomViewPager.addOnPageChangeListener(mPageListener);

        mKeyboardVisibilityUnregistrar = KeyboardVisibilityEvent.registerEventListener(mActivity,
                createKeyboardVisibilityEventListener());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSlidingLayout.removePanelSlideListener(mPanelListener);
        mBottomViewPager.removeOnPageChangeListener(mPageListener);
        mKeyboardVisibilityUnregistrar.unregister();
    }

    public void setAdapter(BottomPanelPagerAdapter pageAdapter) {
        mBottomViewPager.setAdapter(pageAdapter);
    }

    public void setCurrentPage(int position) {
        mBottomViewPager.setCurrentItem(position, true);
    }

    public int getCurrentPagePosition() {
        return mBottomViewPager.getCurrentItem();
    }

    public void collapseSlidingPanel() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void anchorSlidingPanel() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
    }

    public void changePanelPage(int pageIndex) {
        mBottomViewPager.setCurrentItem(pageIndex);
    }

    public boolean isSlidingPanelOpen() {
        return PanelViewModel.isOpenState(mSlidingLayout.getPanelState());
    }

    private KeyboardVisibilityEventListener createKeyboardVisibilityEventListener() {
        return isOpen -> adjustPanelDimensions();
    }

    private void adjustPanelDimensions() {
        mPanelListener.onPanelSlide(null, mSlidingLayout.getCurrentParallaxOffset());
    }

    private class SlidingPanelListener implements SlidingUpPanelLayout.PanelSlideListener {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            adjustBottomPanelContentHeight(slideOffset);
        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                        SlidingUpPanelLayout.PanelState newState) {
            sLogger.userInteraction("MainActivity's bottom panel mode changed from "
                    + previousState + " to " + newState);
        }

        private void adjustBottomPanelContentHeight(float slideOffset) {
            int screenWithoutCollapsedPanelHeight = getScreenWithoutCollapsedPanelHeight();

            int newHeight = (int) (screenWithoutCollapsedPanelHeight * slideOffset);
            int minimumHeight = (int) (screenWithoutCollapsedPanelHeight * mSlidingLayout.getAnchorPoint());

            int finalHeight = Math.max(newHeight, minimumHeight);
            adjustViewHeight(mBottomViewPager, finalHeight);
        }

        private int getScreenWithoutCollapsedPanelHeight() {
            int layoutHeight = mSlidingLayout.getHeight();
            int panelHeight = mSlidingLayout.getPanelHeight();
            return layoutHeight - panelHeight;
        }

        private void adjustViewHeight(View view, int newHeightPxl) {
            ViewGroup.LayoutParams currentLayoutParams = view.getLayoutParams();
            currentLayoutParams.height = newHeightPxl;
            view.setLayoutParams(currentLayoutParams);
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewModel.onPageSelected();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
