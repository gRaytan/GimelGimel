package com.teamagam.gimelgimel.app.utils;

import com.teamagam.gimelgimel.BuildConfig;
import com.teamagam.gimelgimel.app.model.ViewsModels.Message;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageLatLong;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageText;
import com.teamagam.gimelgimel.app.model.ViewsModels.MessageUserLocation;
import com.teamagam.gimelgimel.app.model.entities.LocationSample;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created on 5/2/2016.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GsonUtilTest {

    @Test
    public void testGsonUtil_toJsonText_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        String text = "text123";
        Message msg = new MessageText(senderId, text);
        String targetJson = "{\"content\":\"text123\",\"senderId\":\"sender1\",\"type\":\"Text\"}";

        //Act
        String msgJson = GsonUtil.toJson(msg);

        //Assert
        assertEquals(msgJson, targetJson);
    }

    @Test
    public void testGsonUtil_toJsonLatLong_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        Message msg = new MessageLatLong(senderId, new PointGeometry(23, 32));

        String targetJson = "{\"content\":{\"latitude\":23.0,\"longitude\":32.0,\"altitude\":0.0,\"hasAltitude\":false},\"senderId\":\"sender1\",\"type\":\"LatLong\"}";

        //Act
        String msgJson = GsonUtil.toJson(msg);

        //Assert
        assertEquals(msgJson, targetJson);
    }

    @Test
    public void testGsonUtil_toJsonUserLocation_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        Message msg = new MessageUserLocation(senderId, new LocationSample(new PointGeometry(23, 32), 1462289579954L));
        String targetJson = "{\"content\":{\"location\":{\"latitude\":23.0,\"longitude\":32.0,\"altitude\":0.0,\"hasAltitude\":false},\"timeStamp\":1462289579954,\"hasSpeed\":false,\"speed\":0.0,\"hasBearing\":false,\"bearing\":0.0,\"hasAccuracy\":false,\"accuracy\":0.0},\"senderId\":\"sender1\",\"type\":\"UserLocation\"}";

        //Act
        String msgJson = GsonUtil.toJson(msg);

        //Assert
        assertEquals(msgJson, targetJson);
    }


    @Test
    public void testGsonUtil_fromJsonText_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        String text = "text123";
        Message msg = new MessageText(senderId, text);

        //Act
        String msgJson = GsonUtil.toJson(msg);
        Message msgObj = GsonUtil.fromJson(msgJson, Message.class);

        //Assert
        assertEquals(msgObj.getClass(), msg.getClass());
    }

    @Test
    public void testGsonUtil_fromJsonLatLong_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        Message msg = new MessageLatLong(senderId, new PointGeometry(23, 32));

        //Act
        String msgJson = GsonUtil.toJson(msg);
        Message msgObj = GsonUtil.fromJson(msgJson, Message.class);

        //Assert
        assertEquals(msgObj.getClass(), msg.getClass());
    }

    @Test
    public void testGsonUtil_fromJsonUserLocation_shouldBeEqual() throws Exception {
        //Arrange
        String senderId = "sender1";
        Message msg = new MessageUserLocation(senderId, new LocationSample(new PointGeometry(23, 32), 1462289579954L));

        //Act
        String msgJson = GsonUtil.toJson(msg);
        Message msgObj = GsonUtil.fromJson(msgJson, Message.class);

        //Assert
        assertEquals(msgObj.getClass(), msg.getClass());
    }

}
