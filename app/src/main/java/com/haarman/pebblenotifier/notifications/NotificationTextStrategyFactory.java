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

package com.haarman.pebblenotifier.notifications;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.haarman.pebblenotifier.notifications.strategies.DefaultNotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.KitKatNotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.NotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.SpotifyNotificationStrategy;

import org.jetbrains.annotations.NotNull;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.KITKAT_WATCH;
import static android.os.Build.VERSION_CODES.L;

public class NotificationTextStrategyFactory {

    private NotificationTextStrategyFactory() {
    }

    @NotNull
    public static NotificationTextStrategy getNotificationTextStrategy(@NotNull final Context context, @NotNull final StatusBarNotification notification) {
        NotificationTextStrategy result;

        String packageName = notification.getPackageName();
        if (packageName.equals(SpotifyNotificationStrategy.PACKAGE_SPOTIFY)) {
            result = new SpotifyNotificationStrategy();
        } else {
            switch (VERSION.SDK_INT) {
                case L:
                case KITKAT_WATCH:
                case KITKAT:
                    result = new KitKatNotificationTextStrategy(context);
                    break;
                default:
                    result = new DefaultNotificationTextStrategy(context);
            }
        }

        return result;
    }

}
