package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.GeobrokerConfiguration;
import at.or.eru.gps.converter.configuration.GpxConfiguration;
import at.or.eru.gps.converter.configuration.StreamingConfiguration;
import at.or.eru.gps.converter.geobroker.GeobrokerModule;
import at.or.eru.gps.converter.gpx.GpxModule;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CommandLineBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineBootstrapper.class);

    public static void main(String... args) throws IOException {
        Options options = new Options();
        options.addOption("s", "streaming-mode", false, "Start in streaming mode. Input from stdin is parsed and printed out to stdout.");
        options.addOption(
                "g",
                "geobroker-mode",
                true,
                "Start in Geobroker mode. Input from stdin is parsed and sent to the configured Geobroker server. Configuration JSON file must be provided as argument.");
        options.addOption("i", "ignore-timestamp", false, "Ignore timestamps sent by the radio and generate on server.");
        options.addOption("d", "gpx-directory", true, "Output directory for GPX tracks.");
        DefaultParser parser = new DefaultParser();

        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("Failed to parse command line options.", e);
            System.exit(-1);
            throw new RuntimeException();
        }

        if (!commandLine.hasOption("s")
                && !commandLine.hasOption("g")
                && !commandLine.hasOption("d")) {
            LOG.error("Please select at least one of the operation modes!");
            new HelpFormatter().printHelp("kenwood-gps-converter", options);
            System.exit(-1);
        }

        StreamingConfiguration streamingConfiguration;
        if (commandLine.hasOption("s")) {
            streamingConfiguration = new StreamingConfiguration(true);
        } else {
            streamingConfiguration = new StreamingConfiguration(false);
        }

        GeobrokerConfiguration geobrokerConfiguration;
        if (commandLine.hasOption("g")) {
            geobrokerConfiguration = createGeobrokerConfiguration(commandLine.getOptionValue("g"));
        } else {
            geobrokerConfiguration = new GeobrokerConfiguration(null, List.of());
        }

        GpxConfiguration gpxConfiguration;
        if (commandLine.hasOption("d")) {
            gpxConfiguration = new GpxConfiguration(600, 720, commandLine.getOptionValue("d"), 30);
        } else {
            gpxConfiguration = new GpxConfiguration(0, 0, null, 0);
        }

        Injector injector = Guice.createInjector(
                new GeobrokerModule(geobrokerConfiguration, options.hasOption("i")),
                new GpxModule(gpxConfiguration),
                new StreamingModule(streamingConfiguration)
        );
        EntryPoint entryPoint = injector.getInstance(EntryPoint.class);
        entryPoint.startApplication();
    }

    private static GeobrokerConfiguration createGeobrokerConfiguration(final String configurationFilePath) throws IOException {
        Gson gson = new Gson();

        GeobrokerConfiguration geobrokerConfiguration;
        try (FileReader fileReader = new FileReader(configurationFilePath)) {
            geobrokerConfiguration = gson.fromJson(fileReader, GeobrokerConfiguration.class);
        }

        if (!geobrokerConfiguration.isValid()) {
            LOG.error("Geobroker configuration read from JSON file is not valid.");
            System.exit(-1);
        }

        return geobrokerConfiguration;
    }
}
