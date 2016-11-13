package com.teamagam.gimelgimel.app.message.view;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.message.model.MessageImageApp;
import com.teamagam.gimelgimel.app.message.viewModel.ImageMessageDetailViewModel;
import com.teamagam.gimelgimel.app.message.viewModel.ImageMessageDetailViewModelFactory;
import com.teamagam.gimelgimel.app.view.MainActivity;
import com.teamagam.gimelgimel.app.view.drawable.CircleProgressBarDrawable;
import com.teamagam.gimelgimel.databinding.FragmentMessageImageBinding;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * A subclass {@link MessagesDetailBaseGeoFragment} for showing Image Messages.
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesDetailImageFragment extends MessagesDetailFragment<ImageMessageDetailViewModel> {

    private static final String MESSAGE_KEY = MessagesDetailImageFragment.class.getSimpleName() + "message_key";

    @BindView(R.id.image_view)
    SimpleDraweeView mDraweeView;

    @Inject
    ImageMessageDetailViewModelFactory mViewModelFactory;

    private ImageMessageDetailViewModel mViewModel;


    public static MessagesDetailImageFragment create(MessageImageApp imageMessage) {
        MessagesDetailImageFragment fragment = new MessagesDetailImageFragment();

        Bundle args = new Bundle();
        args.putParcelable(MESSAGE_KEY, imageMessage);

        fragment.setArguments(args);

        return fragment;
    }

    public MessagesDetailImageFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).getMainActivityComponent().inject(this);

        MessageImageApp message = getArguments().getParcelable(MESSAGE_KEY);

        mViewModel = mViewModelFactory.create(message);
//        mViewModel = new ImageMessageDetailViewModel(message);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_message_image;
    }

    @Override
    protected void createSpecificViews(View rootView) {
        mDraweeView.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable());
    }

    @Override
    protected ImageMessageDetailViewModel getSpecificViewModel() {
        return mViewModel;
    }

    @Override
    protected ViewDataBinding bindViewModel(View rootView) {
        FragmentMessageImageBinding bind = FragmentMessageImageBinding.bind(rootView);
        bind.setViewModel(mViewModel);
        mViewModel.setView(this);
        return bind;
    }
}

