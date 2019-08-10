package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.GeobrokerModule;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import okhttp3.HttpUrl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wien.dragon.geobroker.lib.GeobrokerLibModule;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public class CommandLineBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineBootstrapper.class);

    public static void main(String... args) throws IOException {
        Options options = new Options();
        options.addOption("s", "streaming-mode", false, "Start in streaming mode.");
        options.addOption("g", "geobroker-mode", true, "Start in Geobroker mode.");
        options.addOption("i", "ignore-timestamp", false, "Ignore timestamps sent by the radio and generate on server.");
        DefaultParser parser = new DefaultParser();

        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("Failed to parse command line options.", e);
            System.exit(-1);
            throw new RuntimeException();
        }

        if (commandLine.hasOption("s")) {
            startStreamingMode();
        } else if (commandLine.hasOption("g")) {
            startGeobrokerMode(commandLine.getOptionValue("g"), commandLine.hasOption("i"));
        } else {
            LOG.error("Please specify the operation mode (streaming or geobroker).");
            new HelpFormatter().printHelp("kenwood-gps-converter", options);
            System.exit(-1);
        }
    }

    private static void startGeobrokerMode(final String configurationFilePath, final boolean ignoreTimestamps) throws IOException {
        Gson gson = new Gson();

        GeobrokerConfiguration geobrokerConfiguration;
        try (FileReader fileReader = new FileReader(configurationFilePath)) {
            geobrokerConfiguration = gson.fromJson(fileReader, GeobrokerConfiguration.class);
        }

        String baseUrl = geobrokerConfiguration.getBaseUrl();
        checkUrl(baseUrl);

        Injector injector = Guice.createInjector(
                new GeobrokerLibModule(baseUrl),
                new GeobrokerModule(geobrokerConfiguration, ignoreTimestamps)
        );
        StreamParser streamParser = injector.getInstance(StreamParser.class);
        GeobrokerUnitDataHandler dataHandler = injector.getInstance(GeobrokerUnitDataHandler.class);

        streamParser.readCoordinateList(System.in, dataHandler::handle);
    }

    private static void checkUrl(final String baseUrl) {
        try {
            HttpUrl.get(baseUrl);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to read configured URL: {}", e.getMessage());
            System.exit(-1);
        }
    }

    private static void startStreamingMode() throws IOException {
        LOG.info("Starting Kenwood GPS Converter in streaming mode.");

        Injector injector = Guice.createInjector();
        StreamParser streamParser = injector.getInstance(StreamParser.class);
        streamParser.readCoordinateList(System.in, System.out::println);
    }
}
