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
import java.util.regex.Pattern;

/**
 * A NotificationTextStrategy for Threema notifications.
 * <p/>
 * Whenever the second String in the notification is a number, it skips the first two non-title Strings in the notification for the text value.
 *
 * For example:
 *  - Title
 *  - 2
 *  - 2 new messages
 *  - Hi there!
 * Will result in a title value of "Title", and a text value of "Hi there!".
 */
public class ThreemaNotificationStrategy implements NotificationTextStrategy {

    public static final String PACKAGE_THREEMA = "ch.threema.app";

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    @Nullable
    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());
        if (strings.isEmpty()) {
            return null;
        } else {
            return strings.get(0);
        }
    }

    @Nullable
    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());

        int offset = 1;
        if (strings.size() >= 4 && NUMBER_PATTERN.matcher(strings.get(1)).matches()) {
            offset = 3;
        }

        StringBuilder stringBuilder = new StringBuilder(255);
        for (int i = offset; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i)).append('\n');
        }
        String result = stringBuilder.toString();

        return result.isEmpty() ? null : result;
    }
}
