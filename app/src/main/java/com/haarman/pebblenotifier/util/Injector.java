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

package com.haarman.pebblenotifier.util;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

// http://tech.pristine.io/another-take-on-mvc-in-android/
public class Injector {

    @NotNull
    private final Injectable mInjectable;

    private Injector(@NotNull final Injectable injectable) {
        mInjectable = injectable;
    }

    public static Injector from(@NotNull final Context context) {
        if (context instanceof Injectable) {
            return new Injector((Injectable) context);
        } else if (context.getApplicationContext() instanceof Injectable) {
            return new Injector((Injectable) context.getApplicationContext());
        } else {
            throw new IllegalStateException("Cannot inject objects from a Context that is not injectable.");
        }
    }

    public <T> T inject(final T t) {
        return mInjectable.inject(t);
    }
}