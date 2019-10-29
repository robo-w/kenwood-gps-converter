package at.or.eru.gps.converter.parser;

import at.or.eru.gps.converter.data.UnitId;
import at.or.eru.gps.converter.data.UnitPositionData;
import at.or.eru.gps.converter.data.UnitStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAnd;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ParserTest {
    private static final double EPSILON = 0.0001;

    private Parser sut;

    @Before
    public void init() {
        sut = new Parser();
    }

    @Test
    public void returnValidString() {
        // time, latitude, longitude, speed, heading, ???, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,N,01409.5561,E,000.0,,020618,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getLatitude(), closeTo(48.1550666666, EPSILON));
        assertThat(data.getLongitude(), closeTo(14.159268333333, EPSILON));
        assertThat(data.getAltitude(), closeTo(0, 0.001));
    }

    @Test
    public void returnValidNegativeString() {
        // time, latitude, longitude, speed, heading, ???, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,020618,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getLatitude(), closeTo(-48.1550666666, EPSILON));
        assertThat(data.getLongitude(), closeTo(-14.159268333333, EPSILON));
        assertThat(data.getAltitude(), closeTo(0, 0.001));
    }

    @Test
    public void returnValidStringWithUnitId() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,020618,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getUnitId(), isPresentAnd(equalTo(UnitId.fromString("4332199"))));
    }

    @Test
    public void returnValidStringWithUnitStatus() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,020618,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getUnitStatus(), isPresentAnd(equalTo(UnitStatus.fromString("28"))));
    }

    @Test
    public void returnValidStringWithTimestamp() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,020618,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getTimestamp(), equalTo(LocalDateTime.of(2018, 6, 2, 7, 45, 5)));
    }

    @Test
    public void returnValidStringWithGeneratedTimestamp() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getTimestamp(), equalTo(LocalTime.of(7, 45, 5).atDate(LocalDate.now())));
    }

    @Test
    public void returnValidStringWithTimestampPm() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,234520,A,4809.3040,S,01409.5561,W,000.0,,021218,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getTimestamp(), equalTo(LocalDateTime.of(2018, 12, 2, 23, 45, 20)));
    }

    @Test
    public void returnValidStringWithTimestampMidnight() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "00001$PKNDS,004520,A,4809.3040,S,01409.5561,W,000.0,,021218,,00,004332199,0028,00,*00";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getTimestamp(), equalTo(LocalDateTime.of(2018, 12, 2, 0, 45, 20)));
    }

    @Test
    public void returnValidNxdnUnit() {
        // time, latitude, longitude, speed, heading, date, ???, ???, unit id, status
        String input = "$PKNDS,105242,A,4809.3040,N,01409.5561,E,000.0,000.0,081019,1.40,W00,U33998,207,00,*06";

        UnitPositionData data = sut.getPointForStringLine(input).get();

        assertThat(data.getUnitId(), isPresentAnd(equalTo(UnitId.fromString("U33998"))));
        assertThat(data.getUnitStatus(), isPresentAnd(equalTo(UnitStatus.fromInt(207))));
    }
}