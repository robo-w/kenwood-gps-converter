package at.or.eru.gps.converter.configuration;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class GeobrokerConfiguration {
    private final String baseUrl;
    private final List<UnitConfiguration> unitConfigurationList;

    public GeobrokerConfiguration(
            final String baseUrl,
            final List<UnitConfiguration> unitConfigurationList) {
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.unitConfigurationList = ImmutableList.copyOf(unitConfigurationList);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<UnitConfiguration> getUnitConfigurationList() {
        return unitConfigurationList;
    }
}
