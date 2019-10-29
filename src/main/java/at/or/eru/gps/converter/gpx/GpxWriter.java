package at.or.eru.gps.converter.gpx;

import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.configuration.IgnoreTimestamp;
import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.TrackSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Singleton
class GpxWriter implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(GpxWriter.class);
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setThreadFactory(Executors.defaultThreadFactory())
            .setNameFormat("gpx-writer")
            .setPriority(4)
            .build();
    private static final DateTimeFormatter FILENAME_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT);

    private final GpxConfiguration configuration;
    private final ExecutorService executor;

    @Inject
    GpxWriter(final GpxConfiguration configuration) {
        this.configuration = configuration;
        this.executor = Executors.newSingleThreadExecutor(THREAD_FACTORY);
        checkIfDirectoryIsWritable();
    }

    void writeTrack(final UnitId unitId, final List<UnitPositionData> track) {
        executor.execute(new WriteTask(unitId, track));
    }

    @Override
    public void close() {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Shutdown was interrupted. Reason: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private class WriteTask implements Runnable {

        private final UnitId unitId;

        private final List<UnitPositionData> track;

        WriteTask(final UnitId unitId, final List<UnitPositionData> track) {
            this.unitId = Objects.requireNonNull(unitId, "Unit ID must not be null.");
            this.track = ImmutableList.copyOf(track);
        }

        @Override
        public void run() {
            Instant createdAt = Instant.now();
            GPX gpxTrack = GPX.builder("Java Kenwood GPS Converter by robo-w")
                    .metadata(m -> m.name("Track-" + unitId).desc("Track of Unit " + unitId).time(createdAt))
                    .addTrack(trackBuilder -> trackBuilder.addSegment(
                            segment -> track.forEach(position -> addPositionToTrack(segment, position))))
                    .build();
            String fileName = createGpxFilename(createdAt);
            Path gpxPath = Paths.get(configuration.getGpxOutputDirectory(), fileName);
            try {
                GPX.write(gpxTrack, gpxPath);
            } catch (IOException e) {
                LOG.warn("Failed to write GPX file.", e);
            }
        }

        private void addPositionToTrack(final TrackSegment.Builder builder, final UnitPositionData position) {
            builder.addPoint(
                    p -> p.lat(position.getLatitude())
                            .lon(position.getLongitude())
                            .ele(position.getAltitude())
                            .time(position.getTimestamp().toInstant(ZoneOffset.UTC)));
        }

        private String createGpxFilename(final Instant createdAt) {
            ZonedDateTime localDateTime = createdAt.atZone(ZoneId.systemDefault());
            return FILENAME_DATE_FORMAT.format(localDateTime) + "-unit-" + unitId + ".gpx";
        }
    }

    private void checkIfDirectoryIsWritable() {
        if (configuration.isTrackWritingEnabled()) {
            String directory = configuration.getGpxOutputDirectory();
            LOG.info("Writing GPX track is ENABLED. Directory '{}' is checked if write access is granted.", directory);
            long pid = ProcessHandle.current().pid();

            String checkFileName = "check-access-pid-" + pid;
            try {
                Files.writeString(Paths.get(directory, checkFileName), "Checking access to directory.", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                LOG.error("Check if GPX output directory is writable failed. Reason: {}", e.getMessage());
                throw new UncheckedIOException("GPX Output directory is not writable.", e);
            }

        } else {
            LOG.debug("Writing GPX track is DISABLED.");
        }
    }
}
