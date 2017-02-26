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

    private PanelViewModel mViewModel;
    private SlidingPanelListener mPanelListener;
    private PageChangeListener mPageListener;

    MainActivityPanel(FragmentManager fm, Activity activity) {
        ButterKnife.bind(this, activity);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mSlidingLayout.removePanelSlideListener(mPanelListener);
        mBottomViewPager.removeOnPageChangeListener(mPageListener);
    }

    public void setAdapter(BottomPanelPagerAdapter pageAdapter) {
        mBottomViewPager.setAdapter(pageAdapter);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mBottomViewPager.setCurrentItem(item, smoothScroll);
    }

    public int getCurrentItem() {
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

    public boolean isMessagesContainerSelected() {
        return mViewModel.isMessagesPage(mBottomViewPager.getCurrentItem());
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
            mViewModel.onChangePanelState(newState);

            if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                onPanelAnchored();
            } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                onPanelCollapsed();
            }
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

        private void onPanelAnchored() {
            adjustMainContentHeight(getScreenWithoutCollapsedPanelHeight(),
                    mSlidingLayout.getAnchorPoint());
        }

        private void onPanelCollapsed() {
            adjustMainContentHeight(getScreenWithoutCollapsedPanelHeight(), 0);
        }

        private void adjustMainContentHeight(int screenWithoutCollapsedPanelHeight,
                                             float bottomPanelSlideOffset) {
            int height = (int) (screenWithoutCollapsedPanelHeight *
                    (1 - bottomPanelSlideOffset)) - mToolbar.getHeight();
            adjustViewHeight(mMainActivityContentLayout, height);
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewModel.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
