package at.or.eru.gsm.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
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
            String longitudeRaw = split[5];

            double latitude = getDegrees(latitudeRaw, 2);
            double longitude = getDegrees(longitudeRaw, 3);

            point = new Point(longitude, latitude, 0);

        } else {
            LOG.debug("Unsupported input line: {}", line);
        }


        return point;
    }

    private double getDegrees(final String longitudeRaw, final int realPartLength) {
        double degreeRealPart = Double.parseDouble(longitudeRaw.substring(0, realPartLength));
        double minutes = Double.parseDouble(longitudeRaw.substring(realPartLength));

        return degreeRealPart + (minutes / 60);
    }
}
