package at.or.eru.gsm.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GsmConverter {
    private static final Logger LOG = LoggerFactory.getLogger(GsmConverter.class);

    public static void main(String... args) throws IOException {
        LOG.info("Starting GSM Converter");

        StreamParser streamParser = new StreamParser();
        String coordinates = streamParser.readCoordinateList();

        System.err.println(coordinates);
    }
}
