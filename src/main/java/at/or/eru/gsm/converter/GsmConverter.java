package at.or.eru.gsm.converter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GsmConverter {
    private static final Logger LOG = LoggerFactory.getLogger(GsmConverter.class);

    public static void main(String... args) throws IOException {
        LOG.info("Starting Kenwood GPS Converter");

        Injector injector = Guice.createInjector();
        StreamParser streamParser = injector.getInstance(StreamParser.class);
        streamParser.readCoordinateList(System.in, System.out::println);
    }
}
