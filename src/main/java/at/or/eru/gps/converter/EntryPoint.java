/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.IoProvider;
import at.or.eru.gps.converter.parser.StreamParser;

import jakarta.inject.Inject;

class EntryPoint {
    private final StreamParser streamParser;
    private final DispatchingConsumer consumer;
    private final IoProvider ioProvider;

    @Inject
    EntryPoint(final StreamParser streamParser, final DispatchingConsumer consumer, final IoProvider ioProvider) {
        this.streamParser = streamParser;
        this.consumer = consumer;
        this.ioProvider = ioProvider;
    }

    void startApplication() {
        this.streamParser.readCoordinateList(ioProvider.getStreamSource(), consumer::handle);
    }
}
