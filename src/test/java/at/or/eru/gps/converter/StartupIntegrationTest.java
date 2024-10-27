/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2024 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.configuration.StreamingConfiguration;
import at.or.eru.gps.converter.configuration.UnitConfiguration;
import at.or.eru.gps.converter.geobroker.GeobrokerModule;
import at.or.eru.gps.converter.gpx.GpxModule;
import com.google.inject.Guice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

class StartupIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void startup() {
        Guice.createInjector(
                new GeobrokerModule(
                        new GeobrokerConfiguration(
                                "https://no-host.invalid/",
                                List.of(new UnitConfiguration("test", "auth", "id"))),
                        false),
                new GpxModule(new GpxConfiguration(10, 10, tempDir.resolve("gpx-test").toAbsolutePath().toString(), 5)),
                new StreamingModule(new StreamingConfiguration(true)));

        // No startup exception
    }
}
