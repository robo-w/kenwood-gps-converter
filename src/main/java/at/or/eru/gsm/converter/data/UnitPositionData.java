package at.or.eru.gsm.converter.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public class UnitPositionData {
    private final LocalDateTime timestamp;
    private final double longitude;
    private final double latitude;
    private final double altitude;
    @Nullable
    private final UnitId unitId;
    @Nullable
    private final UnitStatus unitStatus;

    public UnitPositionData(
            final LocalDateTime timestamp,
            final double longitude,
            final double latitude,
            final double altitude,
            @Nullable final UnitId unitId,
            @Nullable final UnitStatus unitStatus) {
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.unitId = unitId;
        this.unitStatus = unitStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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

    public Optional<UnitId> getUnitId() {
        return Optional.ofNullable(unitId);
    }

    public Optional<UnitStatus> getUnitStatus() {
        return Optional.ofNullable(unitStatus);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitPositionData that = (UnitPositionData) o;
        return Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.altitude, altitude) == 0 &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(unitId, that.unitId) &&
                Objects.equals(unitStatus, that.unitStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, longitude, latitude, altitude, unitId, unitStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("timestamp", timestamp)
                .append("longitude", longitude)
                .append("latitude", latitude)
                .append("altitude", altitude)
                .append("unitId", unitId)
                .append("unitStatus", unitStatus)
                .toString();
    }
}
