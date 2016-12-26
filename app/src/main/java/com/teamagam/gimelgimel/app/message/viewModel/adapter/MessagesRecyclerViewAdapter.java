package com.teamagam.gimelgimel.app.message.viewModel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseDisplayedMessagesRandomAccessor;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseRecyclerArrayAdapter;
import com.teamagam.gimelgimel.app.common.base.adapters.BaseRecyclerViewHolder;
import com.teamagam.gimelgimel.app.common.logging.AppLogger;
import com.teamagam.gimelgimel.app.common.logging.AppLoggerFactory;
import com.teamagam.gimelgimel.app.message.model.MessageApp;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;


/**
 * {@link RecyclerView.Adapter} that can display a {@link MessageApp} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class MessagesRecyclerViewAdapter extends
        BaseRecyclerArrayAdapter<MessagesRecyclerViewAdapter.MessageViewHolder, MessageApp> {

    private static AppLogger sLogger = AppLoggerFactory.create();

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_GEO = 1;
    private static final int TYPE_IMAGE = 2;
    private static Map<String, Integer> sTypeMessageMap;

    static {
        sTypeMessageMap = new TreeMap<>();
        sTypeMessageMap.put(MessageApp.TEXT, TYPE_TEXT);
        sTypeMessageMap.put(MessageApp.GEO, TYPE_GEO);
        sTypeMessageMap.put(MessageApp.IMAGE, TYPE_IMAGE);
    }

    private static int getMessageType(MessageApp msg) {
        return sTypeMessageMap.get(msg.getType());
    }

    private final BaseDisplayedMessagesRandomAccessor<MessageApp> mDisplayedAccessor;
    private MessageApp mCurrentlySelected;

    public MessagesRecyclerViewAdapter(
            BaseDisplayedMessagesRandomAccessor<MessageApp> accessor,
            OnItemClickListener<MessageApp> listener) {
        super(accessor, listener);
        mDisplayedAccessor = accessor;
    }

    @Override
    public int getItemViewType(int position) {
        return getMessageType(mDisplayedAccessor.get(position));
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
    protected MessageViewHolder createNewViewHolder(View view) {
        sLogger.d("createNewViewHolder");
        return new MessageViewHolder(view);
    }

    @Override
    protected int getSingleItemLayoutRes() {
        return R.layout.recycler_messages_list_item;
    }

    @Override
    protected void bindItemToView(final MessageViewHolder holder,
                                  final MessageApp message) {
        sLogger.d("onBindItemView");
        drawMessageIcon(holder, message);
        drawMessageDate(holder, message);
//        drawMessageBackground(holder, message);

        holder.senderTV.setText(message.getSenderId());
    }

    private void drawMessageIcon(MessageViewHolder holder, MessageApp displayMessage) {
        int draw;

        if (displayMessage.isSelected()) {
            draw = R.drawable.ic_done;
            holder.typeIV.setColorFilter(R.color.black);
        } else {
            draw = getTypedMessageDrawable(displayMessage);
            holder.typeIV.setColorFilter(R.color.white);
        }
        holder.typeIV.setImageDrawable(holder.itemView.getContext().getDrawable(draw));
    }

    private int getTypedMessageDrawable(MessageApp displayMessage) {
        int draw;
        switch (getMessageType(displayMessage)) {
            case TYPE_TEXT:
                draw = R.drawable.ic_message;
                break;
            case TYPE_IMAGE:
                draw = R.drawable.ic_camera;
                break;
            case TYPE_GEO:
                draw = R.drawable.ic_map_marker;
                break;
            default:
                draw = R.drawable.ic_notifications_black_24dp;
        }
        return draw;
    }

    private void drawMessageDate(MessageViewHolder holder, MessageApp displayMessage) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                holder.mAppContext.getString(R.string.message_list_item_time));
        holder.timeTV.setText(sdf.format(displayMessage.getCreatedAt()));
    }

    private void drawMessageBackground(MessageViewHolder holder, MessageApp message) {
        int backgroundColorId;

        backgroundColorId = getBackgroundColorId(message);

        holder.setBackgroundColor(backgroundColorId);
    }

    private int getBackgroundColorId(MessageApp message) {
        if (message.isSelected()) {
            return R.color.selected_list_item;
        }
        if (message.isRead()) {
            return R.color.default_list_item;
        } else {
            return R.color.unread_message_list_item;
        }
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

        @BindView(R.id.message_row_type_imageview)
        ImageView typeIV;

        @BindView(R.id.message_row_date_textview)
        TextView timeTV;

        @BindView(R.id.message_row_sender_textview)
        TextView senderTV;

        MessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
