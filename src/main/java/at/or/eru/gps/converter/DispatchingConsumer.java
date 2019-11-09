/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.configuration.StreamingConfiguration;
import at.or.eru.gps.converter.data.UnitPositionData;
import at.or.eru.gps.converter.geobroker.GeobrokerUnitDataHandler;
import at.or.eru.gps.converter.gpx.TrackCache;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DispatchingConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(DispatchingConsumer.class);

    private final StreamingConfiguration streamingConfiguration;
    private final TrackCache trackCache;
    private final GeobrokerUnitDataHandler geobrokerUnitDataHandler;

    @Inject
    public DispatchingConsumer(
            final GpxConfiguration gpxConfiguration,
            final GeobrokerConfiguration geobrokerConfiguration,
            final StreamingConfiguration streamingConfiguration,
            final Injector injector) {
        this.streamingConfiguration = streamingConfiguration;
        if (gpxConfiguration.isTrackWritingEnabled()) {
            LOG.info("GPX Track Caching is ENABLED.");
            this.trackCache = injector.getInstance(TrackCache.class);
        } else {
            this.trackCache = null;
        }

        if (geobrokerConfiguration.isValid()) {
            LOG.info("Geobroker unit updates are ENABLED.");
            this.geobrokerUnitDataHandler = injector.getInstance(GeobrokerUnitDataHandler.class);
        } else {
            this.geobrokerUnitDataHandler = null;
        }
    }

    void handle(final UnitPositionData positionData) {
        if (trackCache != null) {
            trackCache.handle(positionData);
        }

        if (geobrokerUnitDataHandler != null) {
            geobrokerUnitDataHandler.handle(positionData);
        }

        if (streamingConfiguration.isStreamingModeEnabled()) {
            System.out.println(positionData);
        }
    }
}
