/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2019 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.gpx;

import at.or.eru.gps.converter.data.UnitId;

public interface TrackFinishedCallback {
    void trackFinished(UnitId unitId);
}
