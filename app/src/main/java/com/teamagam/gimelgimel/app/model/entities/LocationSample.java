package com.teamagam.gimelgimel.app.model.entities;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;

import java.util.Date;

/**
 * Created on 4/18/2016.
 * <p/>
 * An immutable data class representing a geographic location sample.
 * <p/>
 * <p>A location can consist of a {@link PointGeometry} , timestamp,
 * and other information such as bearing, altitude and velocity.
 * <p/>
 * <p>All locations generated by the {@link LocationManager} are
 * guaranteed to have a valid latitude, longitude, and timestamp
 * (UTC time), all other
 * parameters are optional.
 */
public class LocationSample implements Parcelable {

    @SerializedName("location")
    private PointGeometry mPoint;

    @SerializedName("timeStamp")
    private long mTime;

    @SerializedName("provider")
    private String mProvider;

    @SerializedName("hasSpeed")
    private boolean mHasSpeed = false;

    @SerializedName("speed")
    private float mSpeed = 0.0f;

    @SerializedName("hasBearing")
    private boolean mHasBearing = false;

    @SerializedName("bearing")
    private float mBearing = 0.0f;

    @SerializedName("hasAccuracy")
    private boolean mHasAccuracy = false;

    @SerializedName("accuracy")
    private float mAccuracy = 0.0f;

    /**
     * Construct a new Location Sample that has only time and location.
     */
    public LocationSample(PointGeometry point, long time) {
        mPoint = point;
        mTime = time;
    }

    /**
     * Construct a new Location Sample that is copied from an existing Location.
     */
    public LocationSample(Location l) {
        mProvider = l.getProvider();
        mTime = l.getTime();
        mPoint = new PointGeometry(l.getLatitude(), l.getLongitude());
        if (l.hasAltitude()) {
            mPoint.altitude = l.getAltitude();
        }
        mHasSpeed = l.hasSpeed();
        mSpeed = l.getSpeed();
        mHasBearing = l.hasBearing();
        mBearing = l.getBearing();
        mHasAccuracy = l.hasAccuracy();
        mAccuracy = l.getAccuracy();
    }

    /**
     * Returns the name of the provider that generated this fix.
     *
     * @return the provider, or null if it has not been set
     */
    public String getProvider() {
        return mProvider;
    }

    /**
     * Return the UTC time of this fix, in milliseconds since January 1, 1970.
     * <p/>
     * <p>Note that the UTC time on a device is not monotonic: it
     * can jump forwards or backwards unpredictably. So always use
     * {@link Location getElapsedRealtimeNanos()} when calculating time deltas.
     * <p/>
     * <p>On the other hand, {@link #getTime} is useful for presenting
     * a human readable time to the user, or for carefully comparing
     * location fixes across reboot or across devices.
     * <p/>
     * <p>All locations generated by the {@link LocationManager}
     * are guaranteed to have a valid UTC time, however remember that
     * the system time may have changed since the location was generated.
     *
     * @return time of fix, in milliseconds since January 1, 1970.
     */
    public long getTime() {
        return mTime;
    }

    /**
     * Get the location..
     * <p/>
     * <p>All locations generated by the {@link LocationManager}
     * will have a valid location.
     */
    public PointGeometry getLocation() {
        return new PointGeometry(mPoint);
    }

    /**
     * True if this location has a speed.
     */
    public boolean hasSpeed() {
        return mHasSpeed;
    }

    /**
     * Get the speed if it is available, in meters/second over ground.
     * <p/>
     * <p>If this location does not have a speed then 0.0 is returned.
     */
    public float getSpeed() {
        return mSpeed;
    }

    /**
     * True if this location has a bearing.
     */
    public boolean hasBearing() {
        return mHasBearing;
    }

    /**
     * Get the bearing, in degrees.
     * <p/>
     * <p>Bearing is the horizontal direction of travel of this device,
     * and is not related to the device orientation. It is guaranteed to
     * be in the range (0.0, 360.0] if the device has a bearing.
     * <p/>
     * <p>If this location does not have a bearing then 0.0 is returned.
     */
    public float getBearing() {
        return mBearing;
    }

    /**
     * True if this location has an accuracy.
     * <p/>
     * <p>All locations generated by the {@link LocationManager} have an
     * accuracy.
     */
    public boolean hasAccuracy() {
        return mHasAccuracy;
    }

    /**
     * Get the estimated accuracy of this location, in meters.
     * <p/>
     * <p>We define accuracy as the radius of 68% confidence. In other
     * words, if you draw a circle centered at this location's
     * latitude and longitude, and with a radius equal to the accuracy,
     * then there is a 68% probability that the true location is inside
     * the circle.
     * <p/>
     * <p>In statistical terms, it is assumed that location errors
     * are random with a normal distribution, so the 68% confidence circle
     * represents one standard deviation. Note that in practice, location
     * errors do not always follow such a simple distribution.
     * <p/>
     * <p>This accuracy estimation is only concerned with horizontal
     * accuracy, and does not indicate the accuracy of bearing,
     * velocity or altitude if those are included in this Location.
     * <p/>
     * <p>If this location does not have an accuracy, then 0.0 is returned.
     * All locations generated by the {@link LocationManager} include
     * an accuracy.
     */
    public float getAccuracy() {
        return mAccuracy;
    }

    public long getAgeMillis() {
        return System.currentTimeMillis() - mTime;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Location[");
        s.append(mProvider);
        s.append(" ");
        s.append(mPoint);
        if (mHasAccuracy) {
            s.append(String.format(" acc=%.0f", mAccuracy));
        } else {
            s.append(" acc=???");
        }
        if (mTime == 0) {
            s.append(" t=?!?");
        } else {
            s.append(" t=");
            s.append(new Date(mTime));
        }

        if (mPoint.hasAltitude) {
            s.append(" alt=").append(mPoint.altitude);
        }
        if (mHasSpeed) {
            s.append(" vel=").append(mSpeed);
        }
        if (mHasBearing) {
            s.append(" bear=").append(mBearing);
        }

        s.append(']');
        return s.toString();
    }

    protected LocationSample(Parcel in) {
        mPoint = in.readParcelable(PointGeometry.class.getClassLoader());
        mTime = in.readLong();
        mProvider = in.readString();
        mHasSpeed = in.readByte() != 0;
        mSpeed = in.readFloat();
        mHasBearing = in.readByte() != 0;
        mBearing = in.readFloat();
        mHasAccuracy = in.readByte() != 0;
        mAccuracy = in.readFloat();
    }

    public static final Creator<LocationSample> CREATOR = new Creator<LocationSample>() {
        @Override
        public LocationSample createFromParcel(Parcel in) {
            return new LocationSample(in);
        }

        @Override
        public LocationSample[] newArray(int size) {
            return new LocationSample[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mPoint, flags);
        dest.writeLong(mTime);
        dest.writeString(mProvider);
        dest.writeByte((byte) (mHasSpeed ? 1 : 0));
        dest.writeFloat(mSpeed);
        dest.writeByte((byte) (mHasBearing ? 1 : 0));
        dest.writeFloat(mBearing);
        dest.writeByte((byte) (mHasAccuracy ? 1 : 0));
        dest.writeFloat(mAccuracy);
    }
}
