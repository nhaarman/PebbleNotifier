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

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;

import com.haarman.pebblenotifier.notifications.strategies.DefaultNotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.KitKatNotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.NotificationTextStrategy;
import com.haarman.pebblenotifier.notifications.strategies.SpotifyNotificationStrategy;
import com.haarman.pebblenotifier.notifications.strategies.ThreemaNotificationStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.KITKAT_WATCH;
import static android.os.Build.VERSION_CODES.L;

public class NotificationTextStrategyFactory {

    /**
     * A Map which keeps a reference to the cached strategies.
     * The keyset of the Map is always filled with the package names of the supported apps.
     */
    private static final Map<String, NotificationTextStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        /* Fill the strategy map with the package names of the supported apps. */
        STRATEGY_MAP.put(SpotifyNotificationStrategy.PACKAGE_SPOTIFY, null);
        STRATEGY_MAP.put(ThreemaNotificationStrategy.PACKAGE_THREEMA, null);
    }

    private NotificationTextStrategyFactory() {
    }

    @NotNull
    public static NotificationTextStrategy getNotificationTextStrategy(@NotNull final Context context, @NotNull final StatusBarNotification notification) {
        NotificationTextStrategy result = getCustomStrategy(notification);

        if (result == null) {
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

    @Nullable
    private static NotificationTextStrategy getCustomStrategy(@NotNull final StatusBarNotification statusBarNotification) {
        NotificationTextStrategy result = null;

        if (STRATEGY_MAP.keySet().contains(statusBarNotification.getPackageName())) {
            result = STRATEGY_MAP.get(statusBarNotification.getPackageName());
            if (result == null) {
                result = createAndCacheCustomStrategy(statusBarNotification);
            }
        }

        return result;
    }

    @Nullable
    private static NotificationTextStrategy createAndCacheCustomStrategy(@NotNull final StatusBarNotification statusBarNotification) {
        NotificationTextStrategy result;

        switch (statusBarNotification.getPackageName()) {
            case SpotifyNotificationStrategy.PACKAGE_SPOTIFY:
                result = new SpotifyNotificationStrategy();
                break;
            case ThreemaNotificationStrategy.PACKAGE_THREEMA:
                result = new ThreemaNotificationStrategy();
                break;
            default:
                result = null;
        }

        STRATEGY_MAP.put(statusBarNotification.getPackageName(), result);

        return result;
    }

}
