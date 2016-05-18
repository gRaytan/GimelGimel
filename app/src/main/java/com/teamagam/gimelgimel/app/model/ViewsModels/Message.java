package com.teamagam.gimelgimel.app.model.ViewsModels;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;


/**
 * A class representing a type of ic_message passed to the server
 */
public abstract class Message<T> {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TEXT, LAT_LONG, USER_LOCATION, IMAGE})
    public @interface MessageType {}
    public static final String TEXT = "Text";
    public static final String LAT_LONG = "LatLong";
    public static final String USER_LOCATION = "UserLocation";
    public static final String IMAGE = "Image";
    @SerializedName("content")
    protected T mContent;
    @SerializedName("_id")
    private String mMessageId;
    @SerializedName("senderId")
    private String mSenderId;
    @SerializedName("createdAt")
    private Date mCreatedAt;
    @SerializedName("type")
    private
    @MessageType
    String mType;

    public Message(String senderId, @MessageType String type) {
        this.mSenderId = senderId;
        this.mType = type;
    }

    public T getContent() {
        return mContent;
    }

    public void setContent(T content) {
        mContent = content;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        mMessageId = messageId;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public void setSenderId(String senderId) {
        mSenderId = senderId;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
