package at.or.eru.gps.converter.gpx;

import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

class UnitTrack {
    private static final Logger LOG = LoggerFactory.getLogger(UnitTrack.class);

    private final UnitId unitId;
    private final TrackFinishedCallback callback;
    private final boolean ignoreTimestamp;
    private final List<UnitPositionData> positionList;
    private final Instant createdAt;
    private final int positionLimit;
    private final Duration maximumTrackDuration;

    UnitTrack(final UnitId unitId, final TrackFinishedCallback callback, final GpxConfiguration configuration, final boolean ignoreTimestamp) {
        this.unitId = unitId;
        this.callback = callback;
        this.ignoreTimestamp = ignoreTimestamp;
        this.positionList = new LinkedList<>();
        this.createdAt = Instant.now();
        this.positionLimit = configuration.getPositionLimit();
        this.maximumTrackDuration = Duration.ofMinutes(configuration.getMaximumTrackTimeInMinutes());
    }

    UnitId getUnitId() {
        return unitId;
    }

    List<UnitPositionData> getPositionList() {
        synchronized (positionList) {
            return ImmutableList.copyOf(positionList);
        }
    }

    void addPosition(final UnitPositionData position) {
        UnitPositionData updatedPosition;
        if (ignoreTimestamp) {
            updatedPosition = new UnitPositionData(
                    LocalDateTime.now(ZoneOffset.UTC),
                    position.getLongitude(),
                    position.getLatitude(),
                    position.getAltitude(),
                    position.getUnitId().orElse(null),
                    position.getUnitStatus().orElse(null)
            );
        } else {
            updatedPosition = position;
        }

        synchronized (positionList) {
            positionList.add(updatedPosition);
        }

        if (positionList.size() == this.positionLimit) {
            LOG.info("Track of unit {} reached size of {}. Track is dumped to file.", unitId, positionLimit);
            callback.trackFinished(unitId);
        }
    }

    void checkMaximumAge() {
        Duration trackLifetime = Duration.between(createdAt, Instant.now());
        if (trackLifetime.compareTo(maximumTrackDuration) > 0) {
            LOG.info("Track of unit {} reached a lifetime of {}. Track is dumped to file.", unitId, trackLifetime);
            callback.trackFinished(unitId);
        }
    }
}
