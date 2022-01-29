/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

import okhttp3.HttpUrl;

import java.util.List;

public class GeobrokerConfiguration {
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
        return baseUrl != null && unitConfigurationList != null && checkUrl();
    }

    private boolean checkUrl() {
        boolean valid;
        try {
            HttpUrl.get(baseUrl);
            valid = true;
        } catch (IllegalArgumentException e) {
            valid = false;
        }

        return valid;
    }
}
