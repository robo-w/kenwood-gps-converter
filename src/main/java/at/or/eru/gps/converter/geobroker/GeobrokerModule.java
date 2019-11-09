/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.geobroker;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.IgnoreTimestamp;
import com.google.inject.AbstractModule;
import wien.dragon.geobroker.lib.GeobrokerLibModule;

import java.util.Objects;

public class GeobrokerModule extends AbstractModule {
    private final GeobrokerConfiguration configuration;
    private final boolean ignoreTimestamps;

    public GeobrokerModule(final GeobrokerConfiguration configuration, final boolean ignoreTimestamps) {
        this.configuration = Objects.requireNonNull(configuration, "Configuration must not be null.");
        this.ignoreTimestamps = ignoreTimestamps;
    }

    @Override
    protected void configure() {
        bind(GeobrokerConfiguration.class).toInstance(configuration);
        bind(Boolean.class).annotatedWith(IgnoreTimestamp.class).toInstance(ignoreTimestamps);
        if (configuration.isValid()) {
            install(new GeobrokerLibModule(configuration.getBaseUrl()));
        }
    }
}
