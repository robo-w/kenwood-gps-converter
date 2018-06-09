package at.or.eru.gsm.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Point {
    private final double longitude;
    private final double latitude;
    private final double altitude;

    public Point(final double longitude, final double latitude, final double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("longitude", longitude)
                .append("latitude", latitude)
                .append("altitude", altitude)
                .toString();
    }
}
