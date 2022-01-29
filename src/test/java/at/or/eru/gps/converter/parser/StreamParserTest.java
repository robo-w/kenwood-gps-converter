/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.parser;

import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

class StreamParserTest {
    private StreamParser sut;

    @BeforeEach
    void init() {
        sut = new StreamParser(new Parser());
    }

    @Test
    void multilineInput_returnParsedPoint() {
        String input = "\02$PKNDS,074505,A,4809.3040,N,01409.5561,E,000.0,,020618,,00,004332199,0028,00,*00\n42\03";
        Set<UnitPositionData> data = new HashSet<>();

        sut.readCoordinateList(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), data::add);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2018, 6, 2, 7, 45, 5);
        assertThat(data, contains(allOf(
                hasProperty("timestamp", equalTo(expectedTimestamp)),
                hasProperty("unitId", isPresentAnd(equalTo(UnitId.fromInt(4332199))))
        )));
    }

    @Test
    void multilineInputWithoutEtx_returnParsedPoint() {
        String input = "\u0002$PKNDS,154614,A,4809.3040,N,01409.5561,E,000.0,,070919,,00,004332199,0028,00,*05\n" +
                "\n" +
                "\u0002$PKNDS,154617,A,4809.3040,N,01409.5561,E,000.1,,070919,,00,004332199,0028,00,*07\n" +
                "\n" +
                "\u0002$PKNDS,154619,A,4809.3040,N,01409.5561,E,000.0,,070919,,00,004332199,0028,00,*08\n" +
                "\n";
        Set<UnitPositionData> data = new HashSet<>();

        sut.readCoordinateList(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), data::add);

        assertThat(data, hasSize(3));
    }
}
