package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.UnitPositionData;

import java.util.Locale;

public class PointSerializer {
    String serialize(final UnitPositionData unitPositionData) {
        return String.format(Locale.ROOT, "%f,%f,%f", unitPositionData.getLongitude(), unitPositionData.getLatitude(), unitPositionData.getAltitude());
    }
}
