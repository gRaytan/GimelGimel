package com.teamagam.gimelgimel.data.alerts.entity;

import com.google.gson.annotations.SerializedName;
import com.teamagam.geogson.core.model.Point;
import com.teamagam.gimelgimel.data.map.entity.PointGeometryData;

public class AlertData {

    @SerializedName("source")
    public String source;
    @SerializedName("text")
    public String text;
    @SerializedName("severity")
    public int severity;
    @SerializedName("location")
    public Point location;
    @SerializedName("time")
    public long time;

    public AlertData(String source, String text, int severity, long time) {
        this.source = source;
        this.text = text;
        this.severity = severity;
        this.time = time;
    }
}
