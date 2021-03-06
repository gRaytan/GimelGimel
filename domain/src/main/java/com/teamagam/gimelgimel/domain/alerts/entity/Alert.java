package com.teamagam.gimelgimel.domain.alerts.entity;

public class Alert {

    private static final String TYPE_BUBBLE = "bubble";

    private final String mAlertId;
    private final String mSource;
    private final String mText;
    private final int mSeverity;
    private final long mTime;

    public Alert(String alertId, int severity, String text, String source, long time) {
        mAlertId = alertId;
        mSeverity = severity;
        mText = text;
        mSource = source;
        mTime = time;
    }

    public String getId() {
        return mAlertId;
    }

    public String getSource() {
        return mSource;
    }

    public String getText() {
        return mText;
    }

    public int getSeverity() {
        return mSeverity;
    }

    public long getTime() {
        return mTime;
    }

    public boolean isChatAlert() {
        return TYPE_BUBBLE.equalsIgnoreCase(mSource);
    }
}
