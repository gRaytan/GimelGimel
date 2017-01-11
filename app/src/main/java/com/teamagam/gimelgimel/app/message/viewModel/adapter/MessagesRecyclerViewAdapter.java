package com.teamagam.gimelgimel.app.message.viewModel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseDisplayedMessagesRandomAccessor;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseRecyclerArrayAdapter;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseRecyclerViewHolder;
import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.message.model.MessageApp;
import com.teamagam.gimelgimel.domain.map.GoToLocationMapInteractorFactory;
import com.teamagam.gimelgimel.domain.map.ToggleMessageOnMapInteractorFactory;

import butterknife.BindView;


/**
 * {@link RecyclerView.Adapter} that can display a {@link MessageApp} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class MessagesRecyclerViewAdapter extends
        BaseRecyclerArrayAdapter<MessagesRecyclerViewAdapter.MessageViewHolder, MessageApp> {

    private static AppLogger sLogger = AppLoggerFactory.create();

    private static final int VIEW_TYPE_SELF = 0;
    private static final int VIEW_TYPE_OTHER = 1;


    private final BaseDisplayedMessagesRandomAccessor<MessageApp> mDisplayedAccessor;
    private final GoToLocationMapInteractorFactory mGoToLocationMapInteractorFactory;
    private final ToggleMessageOnMapInteractorFactory mDrawMessageOnMapInteractorFactory;
    private MessageApp mCurrentlySelected;

    public MessagesRecyclerViewAdapter(
            BaseDisplayedMessagesRandomAccessor<MessageApp> accessor,
            OnItemClickListener<MessageApp> listener,
            GoToLocationMapInteractorFactory goToLocationMapInteractorFactory,
            ToggleMessageOnMapInteractorFactory drawMessageOnMapInteractorFactory) {
        super(accessor, listener);
        mDisplayedAccessor = accessor;
        mGoToLocationMapInteractorFactory = goToLocationMapInteractorFactory;
        mDrawMessageOnMapInteractorFactory = drawMessageOnMapInteractorFactory;
    }

    @Override
    public int getItemViewType(int position) {
        MessageApp messageApp = mDisplayedAccessor.get(position);
        if (messageApp.isFromSelf()) {
            return VIEW_TYPE_SELF;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    public synchronized void read(String messageId) {
        int idx = mDisplayedAccessor.getPosition(messageId);
        MessageApp messageApp = mDisplayedAccessor.get(idx);
        messageApp.setRead(true);
        notifyItemChanged(idx);
    }

    public synchronized void select(String messageId) {
        unselectCurrent();
        selectNew(messageId);
        notifyDataSetChanged();
    }

    @Override
    protected MessageViewHolder createNewViewHolder(View view, int viewType) {
        sLogger.d("createNewViewHolder");
        return new MessageViewHolder(view);
    }

    @Override
    protected int getListItemLayout(int viewType) {
        if (viewType == VIEW_TYPE_OTHER) {
            return R.layout.recycler_message_list_item_other;
        }
        return R.layout.recycler_message_list_item_self;
    }

    @Override
    protected void bindItemToView(final MessageViewHolder holder,
                                  final MessageApp message) {
        sLogger.d("onBindItemView");
        MessageViewHolderBindVisitor bindVisitor = new MessageViewHolderBindVisitor(
                holder, mGoToLocationMapInteractorFactory, mDrawMessageOnMapInteractorFactory);
        message.accept(bindVisitor);
    }

    private void selectNew(String messageId) {
        int idx = mDisplayedAccessor.getPosition(messageId);
        MessageApp messageApp = mDisplayedAccessor.get(idx);
        messageApp.setSelected(true);
        mCurrentlySelected = messageApp;
    }

    private void unselectCurrent() {
        if (mCurrentlySelected != null) {
            mCurrentlySelected.setSelected(false);
        }
    }

    /**
     * used to configure how the views should behave.
     */
    static class MessageViewHolder extends BaseRecyclerViewHolder<MessageApp> {

        @BindView(R.id.message_type_imageview)
        ImageView imageView;

        @BindView(R.id.message_date_textview)
        TextView timeTV;

        @BindView(R.id.message_sender_textview)
        TextView senderTV;

        @BindView(R.id.message_text_content)
        TextView contentTV;

        @BindView(R.id.message_goto_button)
        Button gotoButton;

        @BindView(R.id.message_display_toggle)
        ToggleButton displayToggleButton;

        @BindView(R.id.message_geo_panel)
        LinearLayout messageGeoPanel;

        @BindView(R.id.message_geo_panel_separator)
        View messageGeoPanelSeparator;

        MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
