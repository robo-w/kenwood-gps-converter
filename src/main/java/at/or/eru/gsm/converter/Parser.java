package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private static final Pattern PATTERN = Pattern.compile(".*PKNDS(.*),\\*.*");

    @Nullable
    Point getPointForStringLine(final String line) {
        Point point = null;

        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            String filtered = matcher.group(0);
            String[] split = filtered.split(",");
            String latitudeRaw = split[3];
            String latitudeOrientation = split[4];
            String longitudeRaw = split[5];
            String longitudeOrientation = split[6];

            double latitude = getDegrees(latitudeRaw, 2, getOrientationOfLatitude(latitudeOrientation));
            double longitude = getDegrees(longitudeRaw, 3, getOrientationOfLongitude(longitudeOrientation));

            point = new Point(longitude, latitude, 0);

        } else {
            LOG.debug("Unsupported input line: {}", line);
        }


        return point;
    }

    private static DegreeOrientation getOrientationOfLongitude(final String longitudeOrientation) {
        return Objects.equals("W", longitudeOrientation) ? DegreeOrientation.NEGATIVE : DegreeOrientation.POSITIVE;
    }

    private static DegreeOrientation getOrientationOfLatitude(final String latitudeOrientation) {
        return Objects.equals("S", latitudeOrientation) ? DegreeOrientation.NEGATIVE : DegreeOrientation.POSITIVE;
    }

    private static double getDegrees(final String longitudeRaw, final int realPartLength, final DegreeOrientation orientation) {
        final double degreeRealPart = Double.parseDouble(longitudeRaw.substring(0, realPartLength));
        final double minutes = Double.parseDouble(longitudeRaw.substring(realPartLength));

        final double absoluteDegrees = degreeRealPart + (minutes / 60);
        return orientation == DegreeOrientation.POSITIVE ? absoluteDegrees : -1 * absoluteDegrees;
    }

    private enum DegreeOrientation {
        /**
         * Positive orientation, for example NORTH or EAST.
         */
        POSITIVE,

        /**
         * Negative orientation, for example SOUTH or WEST.
         */
        NEGATIVE,
    }
}
