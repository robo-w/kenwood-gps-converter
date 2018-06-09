package at.or.eru.gsm.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamParser {
    private final Parser parser = new Parser();
    private final PointSerializer serializer = new PointSerializer();

    String readCoordinateList() throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        List<Point> points = new ArrayList<>();

        String readLine = bufferedReader.readLine();
        while (readLine != null) {
            Point point = parser.getPointForStringLine(readLine);
            if (point != null) {
                points.add(point);
            }

            readLine = bufferedReader.readLine();
        }

        return serializer.serialize(points);
    }
}
