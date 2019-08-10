package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.UnitId;
import at.or.eru.gsm.converter.data.UnitPositionData;
import at.or.eru.gsm.converter.data.UnitStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private static final Pattern PATTERN = Pattern.compile(".*PKNDS(.*),\\*.*");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("ddMMyy hhmmss a", Locale.ROOT);;

    Optional<UnitPositionData> getPointForStringLine(final String line) {
        Optional<UnitPositionData> point = Optional.empty();

        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            String filtered = matcher.group(0);
            String[] split = filtered.split(",");

            String latitudeRaw = split[3];
            String latitudeOrientation = split[4];
            double latitude = getDegrees(latitudeRaw, 2, getOrientationOfLatitude(latitudeOrientation));

            String longitudeRaw = split[5];
            String longitudeOrientation = split[6];
            double longitude = getDegrees(longitudeRaw, 3, getOrientationOfLongitude(longitudeOrientation));

            UnitId unitId = getUnitId(split);
            UnitStatus unitStatus = getUnitStatus(split);
            LocalDateTime dateTime = getTimestamp(split);

            point = Optional.of(new UnitPositionData(dateTime, longitude, latitude, 0, unitId, unitStatus));

        } else {
            LOG.debug("Unsupported input line: {}", line);
        }


        return point;
    }

    private LocalDateTime getTimestamp(final String[] split) {
        String timeRaw = split[1];
        String amPmRaw = split[2];
        String dateRaw = split[9];

        LocalDateTime dateTime = null;
        try {
            dateTime = LocalDateTime.parse(String.format("%s %s %sM", dateRaw, timeRaw, amPmRaw), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            LOG.debug("Failed to parse LocalDateTime: {}", e.getMessage());
        }

        return dateTime;
    }

    @Nullable
    private UnitId getUnitId(final String[] split) {
        UnitId unitId = null;
        if (split.length > 12) {
            String unitIdRaw = split[12];
            try {
                unitId = UnitId.fromString(unitIdRaw);
            } catch (Exception e) {
                LOG.debug("Failed to read unit ID from string: {}", e.getMessage());
            }
        }

        return unitId;
    }

    @Nullable
    private UnitStatus getUnitStatus(final String[] split) {
        UnitStatus unitStatus = null;
        if (split.length > 13) {
            String unitStatusRaw = split[13];
            try {
                unitStatus = UnitStatus.fromString(unitStatusRaw);
            } catch (Exception e) {
                LOG.debug("Failed to read unit status from string: {}", e.getMessage());
            }
        }

        return unitStatus;
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
