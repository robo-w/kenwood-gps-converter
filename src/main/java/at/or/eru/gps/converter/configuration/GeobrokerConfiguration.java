/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GeobrokerConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(GeobrokerConfiguration.class);

    public static final GeobrokerConfiguration NO_OP = new GeobrokerConfiguration(null, List.of());

    private final String baseUrl;
    private final List<UnitConfiguration> unitConfigurationList;

    public GeobrokerConfiguration(
            final String baseUrl,
            final List<UnitConfiguration> unitConfigurationList) {
        this.baseUrl = baseUrl;
        this.unitConfigurationList = unitConfigurationList;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<UnitConfiguration> getUnitConfigurationList() {
        return unitConfigurationList;
    }

    public boolean isValid() {
        boolean valid = true;
        if (baseUrl == null) {
            LOG.debug("Base URL is missing in Geobroker configuration!");
            valid = false;
        } else if (!isUrlValid()) {
            valid = false;
        }

        if (unitConfigurationList == null) {
            LOG.debug("Unit configuration list is missing in Geobroker configuration!");
            valid = false;
        }

        return valid;
    }

    private boolean isUrlValid() {
        boolean valid;
        try {
            HttpUrl.get(baseUrl);
            valid = true;
        } catch (IllegalArgumentException e) {
            LOG.debug("Validation of configured URL failed.", e);
            valid = false;
        }

        return valid;
    }
}
