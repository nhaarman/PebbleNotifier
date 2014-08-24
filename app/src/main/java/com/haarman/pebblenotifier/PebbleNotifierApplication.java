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

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.haarman.pebblenotifier.pebble.PebbleNotifier;
import com.haarman.pebblenotifier.util.Injectable;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * The Application class which handles the initial object graph.
 * It uses the Modules class to statically retrieve the modules used in this application.
 */
public class PebbleNotifierApplication extends Application implements Injectable {

    /**
     * The PebbleNotifier which receives the notifications and sends them through to the Pebble watch.
     * Instantiating this class is enough for it to work, this is our reference to it.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Inject
    protected PebbleNotifier mPebbleNotifier;

    /**
     * The initial object graph for dependency injection.
     */
    @NotNull
    private ObjectGraph mObjectGraph;

    /**
     * Returns the active PebbleNotifierApplication for given Context.
     *
     * @param context the Context to retrieve the PebbleNotifierApplication from.
     *
     * @return the active PebbleNotifierApplication.
     */
    public static PebbleNotifierApplication get(@NotNull final Context context) {
        return (PebbleNotifierApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        mObjectGraph = ObjectGraph.create(Modules.list(this));
        inject(this);
    }

    @Override
    public <T> T inject(final T t) {
        return mObjectGraph.inject(t);
    }

    /**
     * @return the ObjectGraph.
     */
    @NotNull
    public ObjectGraph getGraph() {
        return mObjectGraph;
    }

}
