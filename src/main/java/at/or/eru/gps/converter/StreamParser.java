package at.or.eru.gps.converter;

import at.or.eru.gps.converter.data.UnitPositionData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Consumer;

public class StreamParser {
    private static final Logger LOG = LoggerFactory.getLogger(StreamParser.class);

    private final Parser parser;

    @Inject
    public StreamParser(final Parser parser) {
        this.parser = parser;
    }

    void readCoordinateList(final InputStream inputStream, final Consumer<UnitPositionData> resultConsumer) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        LOG.debug("Reading from stdin...");
        String readLine = bufferedReader.readLine();
        while (readLine != null) {
            String trimmedLine = StringUtils.trimToNull(readLine);
            if (trimmedLine != null) {
                Optional<UnitPositionData> point = parser.getPointForStringLine(readLine);
                point.ifPresent(resultConsumer);
            } else {
                LOG.trace("Ignoring empty input line.");
            }

            readLine = bufferedReader.readLine();
        }
    }
}
