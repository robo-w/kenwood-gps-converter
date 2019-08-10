package at.or.eru.gsm.converter;

import at.or.eru.gsm.converter.data.UnitPositionData;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Consumer;

public class StreamParser {
    private final Parser parser;
    private final PointSerializer serializer;

    @Inject
    public StreamParser(final Parser parser, final PointSerializer pointSerializer) {
        this.parser = parser;
        this.serializer = pointSerializer;
    }

    void readCoordinateList(final InputStream inputStream, final Consumer<String> resultConsumer) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String readLine = bufferedReader.readLine();
        while (readLine != null) {
            Optional<UnitPositionData> point = parser.getPointForStringLine(readLine);
            point.ifPresent(value -> resultConsumer.accept(serializer.serialize(value)));

            readLine = bufferedReader.readLine();
        }
    }
}
