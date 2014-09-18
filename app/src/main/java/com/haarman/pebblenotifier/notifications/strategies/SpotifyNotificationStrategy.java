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

package com.haarman.pebblenotifier.notifications.strategies;

import android.service.notification.StatusBarNotification;

import com.haarman.pebblenotifier.util.NotificationUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A NotificationTextStrategy for Spotify notifications.
 *
 * This class specifically relies on the following notification structure:
 *  - {title}
 *  - {album}
 *  - {artist}
 */
public class SpotifyNotificationStrategy implements NotificationTextStrategy {

    public static final String PACKAGE_SPOTIFY = "com.spotify.music";

    @Nullable
    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());
        if (strings.size() == 3) {
            return strings.get(2) + " - " + strings.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());
        if (strings.size() == 3) {
            return strings.get(1);
        } else {
            return null;
        }
    }
}
