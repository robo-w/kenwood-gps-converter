package at.or.eru.gps.converter.configuration;

import com.google.inject.AbstractModule;

import java.util.Objects;

public class GeobrokerModule extends AbstractModule {
    private final GeobrokerConfiguration configuration;
    private final boolean ignoreTimestamps;

    public GeobrokerModule(final GeobrokerConfiguration configuration, final boolean ignoreTimestamps) {
        this.configuration = Objects.requireNonNull(configuration, "Configuration must not be null.");
        this.ignoreTimestamps = ignoreTimestamps;
    }

    @Override
    protected void configure() {
        bind(GeobrokerConfiguration.class).toInstance(configuration);
        bind(Boolean.class).annotatedWith(IgnoreTimestamp.class).toInstance(ignoreTimestamps);
    }
}
