package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.Point;

import java.util.Locale;

public class PointSerializer {
    String serialize(final Point point) {
        return String.format(Locale.ROOT, "%f,%f,%f", point.getLongitude(), point.getLatitude(), point.getAltitude());
    }
}
