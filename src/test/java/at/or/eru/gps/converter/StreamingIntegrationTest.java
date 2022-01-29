/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2022 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.configuration.IoProvider;
import at.or.eru.gps.converter.configuration.StreamingConfiguration;
import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import at.or.eru.gps.converter.data.UnitStatus;
import com.google.inject.Guice;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

class StreamingIntegrationTest {
    private static final String TEST_DATA = "$PKNDS,105242,A,4203.3040,N,01109.5561,E,000.0,000.0,081019,1.40,W00,U55123,207,00,*06";

    private InputStream simulatedInputStream;
    private ByteArrayOutputStream simulatedOutputStream;

    @BeforeEach
    void init() {
        simulatedInputStream = new ByteArrayInputStream(TEST_DATA.getBytes(StandardCharsets.UTF_8));
        simulatedOutputStream = new ByteArrayOutputStream();
        var injector = Guice.createInjector(
                new StreamingModule(new StreamingConfiguration(true)),
                binder -> {
                    binder.bind(IoProvider.class).toInstance(new TestIoProvider());
                    binder.bind(GeobrokerConfiguration.class).toInstance(GeobrokerConfiguration.NO_OP);
                    binder.bind(GpxConfiguration.class).toInstance(GpxConfiguration.NO_OP);
                });
        var entryPoint = injector.getInstance(EntryPoint.class);
        entryPoint.startApplication();
    }

    @Test
    void sendTestData_parsedDataReturned() {
        Awaitility.await()
                .until(() -> simulatedOutputStream.size() > 0);

        var parsedOutput = simulatedOutputStream.toString(StandardCharsets.UTF_8);

        var expectedOutput = new UnitPositionData(LocalDateTime.of(2019, 10, 8, 10, 52, 42),
                11.159268333333333,
                42.05506666666667,
                0.0,
                UnitId.fromString("U55123"),
                UnitStatus.fromInt(207));

        assertThat(parsedOutput, startsWith(expectedOutput.toString()));
    }

    private class TestIoProvider implements IoProvider {
        private final PrintStream printStream = new PrintStream(simulatedOutputStream);

        @Override
        public InputStream getStreamSource() {
            return simulatedInputStream;
        }

        @Override
        public PrintStream getStreamTarget() {
            return printStream;
        }
    }
}
