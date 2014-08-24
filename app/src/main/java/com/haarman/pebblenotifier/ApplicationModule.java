/*
 * Copyright (C) 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haarman.pebblenotifier;

import android.content.Context;

import com.haarman.pebblenotifier.model.ormlite.OrmLiteModule;
import com.haarman.pebblenotifier.notifications.NotificationModule;
import com.haarman.pebblenotifier.pebble.PebbleModule;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * The application module. Every other module can make use of the classes this module provides.
 * <p/>
 * Provides:
 * - @ForApplication Context;
 * - @Singleton AppBus.
 * <p/>
 * This module also includes:
 * - OrmLiteModule - the model layer;
 * - NotificationModule - receives system notifications and dispatches them through the application;
 * - PebbleModule - receives dispatched notitifications and sends them to the Pebble watch.
 */
@Module(
        includes = {
                OrmLiteModule.class,
                NotificationModule.class,
                PebbleModule.class
        },
        injects = {
                PebbleNotifierApplication.class
        },
        complete = false,
        library = true
)
public class ApplicationModule {

    private final PebbleNotifierApplication mApplication;

    public ApplicationModule(final PebbleNotifierApplication application) {
        mApplication = application;
    }

    @Provides
    @ForApplication
    @Singleton
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    AppBus provideBus() {
        return new AppBus();
    }
}
