package at.or.eru.gps.converter;

import at.or.eru.gps.converter.configuration.StreamingConfiguration;
import com.google.inject.AbstractModule;

public class StreamingModule extends AbstractModule {
    private final StreamingConfiguration configuration;

    public StreamingModule(final StreamingConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(StreamingConfiguration.class).toInstance(configuration);
    }
}
