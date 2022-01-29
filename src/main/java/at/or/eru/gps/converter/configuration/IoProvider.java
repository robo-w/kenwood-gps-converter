/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2022 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

import java.io.InputStream;
import java.io.PrintStream;

public interface IoProvider {
    InputStream getStreamSource();

    PrintStream getStreamTarget();
}
