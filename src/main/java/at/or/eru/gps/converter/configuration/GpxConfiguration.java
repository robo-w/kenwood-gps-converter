/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2022 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

public class GpxConfiguration {
    public static final GpxConfiguration NO_OP = new GpxConfiguration(0, 0, null, 0);

    private final int positionLimit;
    private final long maximumTrackTimeInMinutes;
    private final String gpxOutputDirectory;
    private final long trackTimeExceededCheckIntervalInMinutes;

    public GpxConfiguration(
            final int positionLimit,
            final long maximumTrackTimeInMinutes,
            final String gpxOutputDirectory,
            final long trackTimeExceededCheckIntervalInMinutes) {
        this.positionLimit = positionLimit;
        this.maximumTrackTimeInMinutes = maximumTrackTimeInMinutes;
        this.gpxOutputDirectory = gpxOutputDirectory == null ? null : gpxOutputDirectory.trim();
        this.trackTimeExceededCheckIntervalInMinutes = trackTimeExceededCheckIntervalInMinutes;
    }

    public int getPositionLimit() {
        return positionLimit;
    }

    public long getMaximumTrackTimeInMinutes() {
        return maximumTrackTimeInMinutes;
    }

    public String getGpxOutputDirectory() {
        return gpxOutputDirectory;
    }

    public long getTrackTimeExceededCheckIntervalInMinutes() {
        return trackTimeExceededCheckIntervalInMinutes;
    }

    public boolean isTrackWritingEnabled() {
        return gpxOutputDirectory != null;
    }
}
