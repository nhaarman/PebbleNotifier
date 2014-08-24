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

package com.haarman.pebblenotifier.controller.main;

import android.content.Context;

import com.haarman.pebblenotifier.model.async.QueryAllNotificationsTask;
import com.haarman.pebblenotifier.util.ForActivity;
import com.haarman.pebblenotifier.view.main.MainView;
import com.haarman.pebblenotifier.view.main.NotificationAdapter;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class,
                MainView.class,
                NotificationAdapter.class,
                QueryAllNotificationsTask.class
        },
        complete = false,
        library = true
)
public class MainModule {

    @Provides
    NotificationAdapter provideNotificationAdapter(@NotNull @ForActivity final Context context) {
        return new NotificationAdapter(context);
    }

}
