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
import android.content.Context;

import com.haarman.pebblenotifier.ApplicationModule;
import com.haarman.pebblenotifier.controller.main.MainModule;
import com.haarman.pebblenotifier.controller.mutedapps.MutedAppsModule;
import com.haarman.pebblenotifier.controller.preferences.PreferencesModule;
import com.haarman.pebblenotifier.util.ForActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        addsTo = ApplicationModule.class,
        includes = {
                MainModule.class,
                MutedAppsModule.class,
                PreferencesModule.class
        },
        library = true
)
public class ActivityModule {

    @NotNull
    private final Activity mActivity;

    public ActivityModule(@NotNull final Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ForActivity
    @Singleton
    Context provideActivityContext() {
        return mActivity;
    }

}
