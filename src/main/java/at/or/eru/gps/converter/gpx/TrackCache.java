package at.or.eru.gps.converter.gpx;

import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Singleton
public class TrackCache implements TrackFinishedCallback {
    private static final Logger LOG = LoggerFactory.getLogger(TrackCache.class);

    private final Map<UnitId, UnitTrack> activeTracks;
    private final GpxConfiguration configuration;
    private final GpxWriter gpxWriter;

    @Inject
    public TrackCache(final GpxConfiguration configuration, final GpxWriter gpxWriter) {
        this.configuration = configuration;
        this.gpxWriter = gpxWriter;
        this.activeTracks = new ConcurrentHashMap<>();
        startTrackWatchDog();
    }

    public void handle(final UnitPositionData positionData) {
        positionData.getUnitId().ifPresentOrElse(
                unitId -> activeTracks.compute(unitId, (id, track) -> createOrUpdateUnitTrack(id, track, positionData)),
                () -> LOG.debug("Position data does not contain a unitId. Track cannot be created."));
    }

    @Override
    public void trackFinished(final UnitId unitId) {
        UnitTrack finishedTrack = activeTracks.remove(unitId);
        if (finishedTrack != null) {
            gpxWriter.writeTrack(unitId, finishedTrack.getPositionList());
        } else {
            LOG.warn("Got <null> as result from internal Map for unitId {}!", unitId);
        }
    }

    private void finishAllTracks() {
        Set<UnitId> unitIds = ImmutableSet.copyOf(activeTracks.keySet());
        unitIds.forEach(this::trackFinished);
    }

    private UnitTrack createOrUpdateUnitTrack(
            final UnitId id,
            final UnitTrack existingTrack,
            final UnitPositionData positionData) {
        UnitTrack updatedTrack = Objects.requireNonNullElseGet(existingTrack, () -> new UnitTrack(id, this, configuration));
        updatedTrack.addPosition(positionData);
        return updatedTrack;
    }

    private void startTrackWatchDog() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("gpx-track-watchdog")
                .setPriority(2)
                .build();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);
        final long delay = configuration.getTrackTimeExceededCheckIntervalInMinutes();
        scheduler.scheduleAtFixedRate(this::checkAllTracks, delay, delay, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new ShutdownAction());
    }

    private void checkAllTracks() {
        Set<UnitTrack> tracks = ImmutableSet.copyOf(activeTracks.values());
        LOG.info("Checking all active tracks if maximum age exceeded. Currently {} tracks are cached.", tracks.size());
        tracks.forEach(UnitTrack::checkMaximumAge);
    }

    private class ShutdownAction extends Thread {
        ShutdownAction() {
            super("GPX-write-on-shutdown");
        }

        @Override
        public void run() {
            System.err.println("Try to flush all existing tracks to disk on shutdown...");
            finishAllTracks();
            gpxWriter.close();
        }
    }
}
