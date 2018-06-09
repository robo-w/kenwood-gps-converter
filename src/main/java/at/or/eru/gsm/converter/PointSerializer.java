package at.or.eru.gsm.converter;

import java.util.List;

public class PointSerializer {
    String serialize(final List<Point> pointList) {
        StringBuilder coordinates = new StringBuilder();

        for (Point point : pointList) {
            coordinates.append(point.getLongitude()).append(",").append(point.getLatitude()).append(",").append(point.getAltitude()).append("\n");
        }

        return coordinates.toString();
    }
}
