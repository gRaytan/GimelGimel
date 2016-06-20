package com.teamagam.gimelgimel.app.network.services;

import com.teamagam.gimelgimel.app.common.logging.Logger;
import com.teamagam.gimelgimel.app.common.logging.LoggerFactory;
import com.teamagam.gimelgimel.app.model.ViewsModels.Message;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageImage;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageLatLong;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageText;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageUserLocation;
import com.teamagam.gimelgimel.app.model.entities.ImageMetadata;
import com.teamagam.gimelgimel.app.model.entities.LocationSample;
import com.teamagam.gimelgimel.app.network.rest.RestAPI;
import com.teamagam.gimelgimel.app.utils.NetworkUtil;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class handling different requests from GGMessaging server
 */
public class GGMessagingUtils {

    private static final Logger sLogger = LoggerFactory.create(GGMessagingUtils.class);

    /**
     * Creates text {@link Message} with {@link MessageText} containing given text
     * and asynchronously sends it
     *
     * @param message the message content text
     */
    public static void sendTextMessageAsync(String message) {
        String senderId = NetworkUtil.getMac();
        MessageText messageToSend = new MessageText(senderId, message);
        GGMessagingUtils.sendMessageAsync(messageToSend);
    }

    /**
     * Creates location {@link Message} with {@link MessageLatLong} containing given
     * {@link PointGeometry} and asynchronously sends it
     *
     * @param pointGeometry the message's content location
     */
    public static void sendLatLongMessageAsync(PointGeometry pointGeometry) {
        String senderId = NetworkUtil.getMac();
        Message messageToSend = new MessageLatLong(senderId, pointGeometry);
        GGMessagingUtils.sendMessageAsync(messageToSend);
    }


    /**
     * Creates location {@link Message} with {@link MessageUserLocation} containing given
     * {@link LocationSample} and asynchronously sends it
     *
     * @param sample
     */
    public static void sendUserLocationMessageAsync(LocationSample sample) {
        String senderId = NetworkUtil.getMac();
        Message messageToSend = new MessageUserLocation(senderId, sample);
        GGMessagingUtils.sendMessageAsync(messageToSend);
    }


    public static void sendImageMessageAsync(ImageMetadata meta) {
        String senderId = NetworkUtil.getMac();
        Message messageToSend = new MessageImage(senderId, meta);
        GGMessagingUtils.sendMessageAsync(messageToSend);
    }

    /**
     * Asynchronously sends message to service
     *
     * @param message - the message to send
     */
    public static void sendMessageAsync(Message message) {
        Call<Message> call = RestAPI.getInstance().getMessagingAPI().postMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (!response.isSuccessful()) {
                    sLogger.d("Unsuccessful message post: " + response.errorBody());
                    return;
                }

                sLogger.d("message ID from DB: " + response.body().getMessageId());
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                sLogger.d("FAIL in sending message!!!");
            }
        });
    }
}
