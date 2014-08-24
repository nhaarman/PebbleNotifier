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

import android.app.Activity;
import android.os.Bundle;

import com.haarman.pebblenotifier.util.Injectable;

import dagger.ObjectGraph;

/**
 * A base Activity class to be used throughout the application.
 * This class adds the ActivityModule to the existing object graph.
 */
public class PebbleNotifierActivity extends Activity implements Injectable {

    /**
     * The extended ObjectGraph.
     */
    private ObjectGraph mObjectGraph;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObjectGraph = PebbleNotifierApplication.get(this).getGraph().plus(new ActivityModule(this));
        mObjectGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        mObjectGraph = null;
        super.onDestroy();
    }

    @Override
    public <T> T inject(final T object) {
        return mObjectGraph.inject(object);
    }

}
