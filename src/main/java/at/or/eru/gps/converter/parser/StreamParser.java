/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.parser;

import at.or.eru.gps.converter.data.UnitPositionData;
import com.google.common.base.Ascii;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StreamParser {
    private static final Logger LOG = LoggerFactory.getLogger(StreamParser.class);

    private static final String ETX = ((char) Ascii.ETX) + "";
    private static final String STX = ((char) Ascii.STX) + "";

    private static final Pattern SCAN_PATTERN = Pattern.compile("[\u0002\u0003\\n\\r]");
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setThreadFactory(Executors.defaultThreadFactory())
            .setNameFormat("position-data-consumer")
            .setPriority(6)
            .build();

    private final Parser parser;
    private final Executor executor;

    @Inject
    public StreamParser(final Parser parser) {
        this(parser, Executors.newSingleThreadExecutor(THREAD_FACTORY));
    }

    public StreamParser(final Parser parser, final Executor executor) {
        this.parser = parser;
        this.executor = executor;
    }

    public void readCoordinateList(final InputStream inputStream, final Consumer<UnitPositionData> resultConsumer) {
        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.useDelimiter(SCAN_PATTERN);

            LOG.debug("Reading from stdin delimited by Ascii ETX...");
            while (scanner.hasNext()) {
                String readInput = scanner.next();

                String trimmedInput = StringUtils.trimToNull(trimControlCharacters(readInput));
                if (trimmedInput != null) {
                    String[] lines = trimmedInput.split("\\r?\\n");
                    Stream.of(lines)
                            .flatMap(line -> parser.getPointForStringLine(line).stream())
                            .forEach(point -> executor.execute(new ResultCallback(resultConsumer, point)));
                } else {
                    LOG.trace("Ignoring empty input line.");
                }
            }
        } finally {
            LOG.debug("End of input stream reached. No more content to parse.");
        }
    }

    private static class ResultCallback implements Runnable {
        private static final Logger LOG = LoggerFactory.getLogger(ResultCallback.class);

        private final Consumer<UnitPositionData> resultConsumer;
        private final UnitPositionData point;

        ResultCallback(final Consumer<UnitPositionData> resultConsumer, final UnitPositionData point) {
            this.resultConsumer = resultConsumer;
            this.point = point;
        }

        @Override
        public void run() {
            try {
                resultConsumer.accept(point);
            } catch (Exception e) {
                LOG.warn("Failed to consume parsed Point {}.", point, e);
            }
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
