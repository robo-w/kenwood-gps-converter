package at.or.eru.gps.converter;

import at.or.eru.gps.converter.data.UnitPositionData;

import java.util.Locale;

public class PointSerializer {
    String serialize(final UnitPositionData unitPositionData) {
        return String.format(Locale.ROOT, "%f,%f,%f", unitPositionData.getLongitude(), unitPositionData.getLatitude(), unitPositionData.getAltitude());
    }
}
