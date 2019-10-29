package at.or.eru.gps.converter.configuration;

public class StreamingConfiguration {
    private final boolean streamingModeEnabled;

    public StreamingConfiguration(final boolean streamingModeEnabled) {
        this.streamingModeEnabled = streamingModeEnabled;
    }

    public boolean isStreamingModeEnabled() {
        return streamingModeEnabled;
    }
}
