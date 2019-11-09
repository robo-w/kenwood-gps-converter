/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

public class StreamingConfiguration {
    private final boolean streamingModeEnabled;

    public StreamingConfiguration(final boolean streamingModeEnabled) {
        this.streamingModeEnabled = streamingModeEnabled;
    }

    public boolean isStreamingModeEnabled() {
        return streamingModeEnabled;
    }
}
