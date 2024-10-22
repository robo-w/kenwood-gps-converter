/*
 * Licensed under MIT license. See LICENSE for details.
 *
 * Copyright (c) 2022 Robert Wittek, https://github.com/robo-w
 */

package at.or.eru.gps.converter.configuration;

import jakarta.inject.Singleton;
import java.io.InputStream;
import java.io.PrintStream;

@Singleton
public class SystemIoProvider implements IoProvider {
    @Override
    public InputStream getStreamSource() {
        return System.in;
    }

    @Override
    public PrintStream getStreamTarget() {
        return System.out;
    }
}
