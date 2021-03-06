/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter;

import at.or.eru.gps.converter.parser.StreamParser;

import javax.inject.Inject;

class EntryPoint {
    private final StreamParser streamParser;
    private final DispatchingConsumer consumer;

    @Inject
    EntryPoint(final StreamParser streamParser, final DispatchingConsumer consumer) {
        this.streamParser = streamParser;
        this.consumer = consumer;
    }

    void startApplication() {
        this.streamParser.readCoordinateList(System.in, consumer::handle);
    }
}
