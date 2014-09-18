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

import java.util.List;

/**
 * A NotificationTextStrategy which uses reflection to find text in the notification.
 */
public class ViewInspectorNotificationStrategy implements NotificationTextStrategy {

    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());
        if (strings.isEmpty()) {
            return null;
        } else {
            return strings.get(0);
        }
    }

    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = NotificationUtils.getStrings(statusBarNotification.getNotification());
        if (strings.size() <= 1) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(255);

        for (int i = 1; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i)).append('\n');
        }

        return stringBuilder.toString();
    }


}
