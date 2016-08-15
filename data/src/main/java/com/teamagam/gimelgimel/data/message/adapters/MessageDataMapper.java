package com.teamagam.gimelgimel.data.message.adapters;


import com.teamagam.gimelgimel.data.geometry.entity.PointGeometryData;
import com.teamagam.gimelgimel.data.message.entity.MessageData;
import com.teamagam.gimelgimel.data.message.entity.contents.GeoContentData;
import com.teamagam.gimelgimel.data.message.entity.contents.LocationSampleData;
import com.teamagam.gimelgimel.domain.messages.entities.Message;
import com.teamagam.gimelgimel.domain.messages.model.MessageGeo;
import com.teamagam.gimelgimel.domain.messages.model.MessageImage;
import com.teamagam.gimelgimel.domain.messages.model.MessageModel;
import com.teamagam.gimelgimel.domain.messages.model.MessageText;
import com.teamagam.gimelgimel.domain.messages.model.MessageUserLocation;
import com.teamagam.gimelgimel.domain.messages.model.PointGeometry;
import com.teamagam.gimelgimel.domain.messages.model.contents.ImageMetadata;
import com.teamagam.gimelgimel.domain.messages.model.contents.LocationSample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class used to transform {@link MessageData} (in the data layer) to {@link Message} in the
 * domain layer.
 */
@Singleton
public class MessageDataMapper {

    @Inject
    public MessageDataMapper() {
    }

    /**
     * Transform a {@link MessageData} into an {@link Message}.
     *
     * @param message Object to be transformed.
     * @return {@link Message} if valid {@link MessageData} otherwise null.
     */
    public MessageModel transform(MessageData message) {
        switch (message.getType()) {
            case MessageData.GEO: {
                return createMessageGeo(message);
            }
            case MessageData.IMAGE: {
                return createMessageImage(message);
            }
            case MessageData.TEXT: {
                return createMessageText(message);
            }
            case MessageData.USER_LOCATION: {
                return createMessageUserLocation(message);
            }
            default:
                return null;
        }
    }

    /**
     * Transform a List of {@link MessageData} into a Collection of {@link MessageModel}.
     *
     * @param messageCollection Object Collection to be transformed.
     * @return {@link MessageModel} if valid {@link MessageData} otherwise null.
     */
    public List<MessageModel> transform(Collection<MessageData> messageCollection) {
        List<MessageModel> messageList = new ArrayList<>(20);
        MessageModel messageModel;
        for (MessageData message : messageCollection) {
            messageModel = transform(message);
            if (messageModel != null) {
                messageList.add(messageModel);
            }
        }

        return messageList;
    }


    private MessageUserLocation createMessageUserLocation(MessageData message) {
        LocationSampleData content =
                (LocationSampleData) message.getContent();
        LocationSample convertedLocationSample = convertLocationSample(content);

        MessageUserLocation userLocation =
                new MessageUserLocation(message.getSenderId(), convertedLocationSample);
        userLocation.setCreatedAt(message.getCreatedAt());

        return userLocation;
    }

    private MessageText createMessageText(MessageData message) {
        String content = (String) message.getContent();

        MessageText text = new MessageText(message.getSenderId(), content);
        text.setCreatedAt(message.getCreatedAt());

        return text;
    }

    private MessageImage createMessageImage(MessageData message) {
        com.teamagam.gimelgimel.data.message.entity.contents.ImageMetadata content =
                (com.teamagam.gimelgimel.data.message.entity.contents.ImageMetadata) message.getContent();
        ImageMetadata convertedImageMetadata =
                convertImageMetadata(content);

        MessageImage image = new MessageImage(message.getSenderId(), convertedImageMetadata);
        image.setCreatedAt(message.getCreatedAt());

        return image;
    }

    private MessageGeo createMessageGeo(MessageData message) {
        GeoContentData content = (GeoContentData) message.getContent();
        PointGeometry convertedPoint = convertPointGeometry(content.getPointGeometry());

        MessageGeo geo = new MessageGeo(message.getSenderId(),
                convertedPoint, content.getText(), message.getType());
        geo.setCreatedAt(message.getCreatedAt());

        return geo;
    }

    private LocationSample convertLocationSample(LocationSampleData content) {
        LocationSample convertedLocationSample =
                new LocationSample(convertPointGeometry(content.getLocation()), content.getTime());

        if (content.hasAccuracy()) {
            convertedLocationSample.setAccuracy(content.getAccuracy());
        }
        if (content.hasBearing()) {
            convertedLocationSample.setBearing(content.getBearing());
        }
        if (content.hasSpeed()) {
            convertedLocationSample.setSpeed(content.getSpeed());
        }

        return convertedLocationSample;
    }

    private ImageMetadata convertImageMetadata(com.teamagam.gimelgimel.data.message.entity.contents.ImageMetadata content) {
        ImageMetadata convertedImageMetadata =
                new ImageMetadata(
                        content.getTime(), content.getURL(), content.getSource());

        if (content.hasLocation()) {
            convertedImageMetadata.setLocation(convertPointGeometry(content.getLocation()));
        }

        return convertedImageMetadata;
    }

    private PointGeometry convertPointGeometry(PointGeometryData pointGeometry) {
        PointGeometry convertedPoint = new PointGeometry(pointGeometry.latitude, pointGeometry.longitude);

        if (pointGeometry.hasAltitude) {
            convertedPoint.setAltitude(pointGeometry.altitude);
        }

        return convertedPoint;
    }

}
