package com.teamagam.gimelgimel.app.mainActivity.viewmodel;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.teamagam.gimelgimel.app.common.base.ViewModels.BaseViewModel;
import com.teamagam.gimelgimel.app.common.base.adapters.BottomPanelPagerAdapter;
import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.mainActivity.view.MainActivityPanel;
import com.teamagam.gimelgimel.domain.messages.DisplayUnreadMessagesCountInteractor;
import com.teamagam.gimelgimel.domain.messages.DisplayUnreadMessagesCountInteractorFactory;
import com.teamagam.gimelgimel.domain.messages.UpdateMessagesContainerStateInteractorFactory;
import com.teamagam.gimelgimel.domain.messages.UpdateMessagesReadInteractor;
import com.teamagam.gimelgimel.domain.messages.UpdateMessagesReadInteractorFactory;
import com.teamagam.gimelgimel.domain.messages.repository.MessagesContainerStateRepository;

import javax.inject.Inject;

@AutoFactory
public class PanelViewModel extends BaseViewModel<MainActivityPanel> {

    private final FragmentManager mFragmentManager;
    private final Activity mActivity;
    protected AppLogger sLogger = AppLoggerFactory.create();
    UpdateMessagesReadInteractorFactory mUpdateMessagesReadInteractorFactory;
    UpdateMessagesContainerStateInteractorFactory mUpdateMessagesContainerStateInteractorFactory;
    DisplayUnreadMessagesCountInteractorFactory mDisplayUnreadCountInteractorFactory;
    private UpdateMessagesReadInteractor mMessagesReadInteractor;
    private DisplayUnreadMessagesCountInteractor mDisplayUnreadMessagesCountInteractor;

    private BottomPanelPagerAdapter mPageAdapter;

    @Inject
    PanelViewModel(@Provided UpdateMessagesReadInteractorFactory
                           updateMessagesReadInteractorFactory,
                   @Provided UpdateMessagesContainerStateInteractorFactory
                           updateMessagesContainerStateInteractorFactory,
                   @Provided DisplayUnreadMessagesCountInteractorFactory
                           displayUnreadMessagesCountInteractorFactory,
                   FragmentManager fragmentManager,
                   Activity activity) {
        mUpdateMessagesReadInteractorFactory = updateMessagesReadInteractorFactory;
        mUpdateMessagesContainerStateInteractorFactory =
                updateMessagesContainerStateInteractorFactory;
        mDisplayUnreadCountInteractorFactory = displayUnreadMessagesCountInteractorFactory;
        mFragmentManager = fragmentManager;
        mActivity = activity;
    }

    public static boolean isClosedState(SlidingUpPanelLayout.PanelState state) {
        return state == SlidingUpPanelLayout.PanelState.COLLAPSED
                || state == SlidingUpPanelLayout.PanelState.HIDDEN;
    }

    public static boolean isOpenState(SlidingUpPanelLayout.PanelState state) {
        return state == SlidingUpPanelLayout.PanelState.ANCHORED
                || state == SlidingUpPanelLayout.PanelState.EXPANDED;
    }

    @Override
    public void start() {
        super.start();
        mPageAdapter = new BottomPanelPagerAdapter(mFragmentManager, mActivity);
        mView.setAdapter(mPageAdapter);
        mMessagesReadInteractor = mUpdateMessagesReadInteractorFactory.create();
        mDisplayUnreadMessagesCountInteractor = mDisplayUnreadCountInteractorFactory.create(
                new DisplayUnreadMessagesCountInteractor.Renderer() {
                    @Override
                    public void renderUnreadMessagesCount(int unreadMessagesCount) {
                        mPageAdapter.updateUnreadCount(unreadMessagesCount);
                    }
                }
        );
        mDisplayUnreadMessagesCountInteractor.execute();
    }

    @Override
    public void stop() {
        super.stop();
        mMessagesReadInteractor.unsubscribe();
        mDisplayUnreadMessagesCountInteractor.unsubscribe();
    }

    public void onPageSelected(int position) {
        if (mView.isSlidingPanelOpen()) {
            onPageSelectedWithOpenPanel(position);
        }
    }

    public void onChangePanelState(SlidingUpPanelLayout.PanelState newState) {
        if (mView.isMessagesContainerSelected()) {
            onChangePanelStateWithMessagesSelected(newState);
        }
    }

    private void onPageSelectedWithOpenPanel(int position) {
        if (BottomPanelPagerAdapter.isSensorsPage(position)) {
            onMessagesContainerConcealed();
        } else if (BottomPanelPagerAdapter.isMessagesPage(position)) {
            onMessagesContainerRevealed();
        }
    }

    private void onChangePanelStateWithMessagesSelected(SlidingUpPanelLayout.PanelState newState) {
        if (isClosedState(newState)) {
            onMessagesContainerConcealed();
        } else if (isOpenState(newState)) {
            onMessagesContainerRevealed();
        }
    }

    private void onMessagesContainerRevealed() {
        mMessagesReadInteractor.execute();
        mUpdateMessagesContainerStateInteractorFactory.create(MessagesContainerStateRepository.ContainerState.VISIBLE).execute();
    }

    private void onMessagesContainerConcealed() {
        mMessagesReadInteractor.execute();
        mUpdateMessagesContainerStateInteractorFactory.create(MessagesContainerStateRepository.ContainerState.INVISIBLE).execute();
    }
}