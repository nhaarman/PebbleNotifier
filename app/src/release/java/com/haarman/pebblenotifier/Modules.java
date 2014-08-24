package com.haarman.pebblenotifier;

import android.app.Application;

import com.haarman.pebblenotifier.PebbleNotifierApplication;

import org.jetbrains.annotations.NotNull;

/**
 * Provides the modules for the release build of the application.
 * This only uses the application module.
 */
public class Modules {

    private Modules() {
    }

    static Object[] list(@NotNull final PebbleNotifierApplication app) {
        return new Object[]{
                new ApplicationModule(app)
        };
    }

}
