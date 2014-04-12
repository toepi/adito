package com.adito.boot;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author toepi <toepi@users.noreply.github.com>
 */
public final class ServerStarter {

    public static void start(final ClassLoader bootLoader, final String[] args) {
        if (bootLoader == null) {
            throw new IllegalStateException("Bootloader must be not null!");
        }
        final Iterator<AditoServerFactory> serverFactoryIterator = ServiceLoader.load(AditoServerFactory.class).iterator();
        if (serverFactoryIterator.hasNext()) {
            serverFactoryIterator.next().createServer(bootLoader, args);
        } else {
            throw new IllegalStateException("No AditoServerFactory found! Check your classpath.");
        }
    }

    private ServerStarter() {
    }
}
