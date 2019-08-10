package at.or.eru.gsm.converter.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.longitude, longitude) == 0 &&
                Double.compare(point.latitude, latitude) == 0 &&
                Double.compare(point.altitude, altitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, altitude);
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
