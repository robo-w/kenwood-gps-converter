package at.or.eru.gps.converter;

import at.or.eru.gps.converter.data.UnitPositionData;
import com.google.common.base.Ascii;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamParser {
    private static final Logger LOG = LoggerFactory.getLogger(StreamParser.class);

    private static final String ETX = ((char) Ascii.ETX) + "";
    private static final String STX = ((char) Ascii.STX) + "";

    private final Parser parser;

    @Inject
    public StreamParser(final Parser parser) {
        this.parser = parser;
    }

    void readCoordinateList(final InputStream inputStream, final Consumer<UnitPositionData> resultConsumer) {
        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.useDelimiter(ETX);

            LOG.debug("Reading from stdin delimited by Ascii ETX...");
            while (scanner.hasNext()) {
                String readInput = scanner.next();

                String trimmedInput = StringUtils.trimToNull(trimControlCharacters(readInput));
                if (trimmedInput != null) {
                    String[] lines = trimmedInput.split("\\r?\\n");
                    Stream.of(lines)
                            .flatMap(line -> parser.getPointForStringLine(line).stream())
                            .forEach(resultConsumer);
                } else {
                    LOG.trace("Ignoring empty input line.");
                }
            }
        } finally {
            LOG.debug("End of input stream reached. No more content to parse.");
        }
    }

    private static String trimControlCharacters(final String readInput) {
        String truncated = readInput;

        if (truncated.startsWith(STX)) {
            truncated = truncated.substring(1);
        }

        if (truncated.endsWith(ETX)) {
            truncated = truncated.substring(0, truncated.length() - 1);
        }

        return truncated;
    }
}
