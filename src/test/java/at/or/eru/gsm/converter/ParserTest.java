package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.Point;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class ParserTest {
    private Parser sut;

    @Before
    public void init() {
        sut = new Parser();
    }

    @Test
    public void returnValidString() {
        // time, latitude, longitude, speed, heading, ???, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,N,01409.5561,E,000.0,,020618,,00,004332199,0028,00,*00";

        Point pointForStringLine = sut.getPointForStringLine(input);

        assertThat(pointForStringLine.getLatitude(), closeTo(48.1550666666, 0.001));
        assertThat(pointForStringLine.getLongitude(), closeTo(14.159268333333, 0.001));
        assertThat(pointForStringLine.getAltitude(), closeTo(0, 0.001));
    }

    @Test
    public void returnValidNegativeString() {
        // time, latitude, longitude, speed, heading, ???, ???, ???, unit id, status
        String input = "00001$PKNDS,074505,A,4809.3040,S,01409.5561,W,000.0,,020618,,00,004332199,0028,00,*00";

        Point pointForStringLine = sut.getPointForStringLine(input);

        assertThat(pointForStringLine.getLatitude(), closeTo(-48.1550666666, 0.001));
        assertThat(pointForStringLine.getLongitude(), closeTo(-14.159268333333, 0.001));
        assertThat(pointForStringLine.getAltitude(), closeTo(0, 0.001));
    }
}