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

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import org.jetbrains.annotations.NotNull;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class KitKatNotificationTextStrategy implements NotificationTextStrategy {

    @NotNull
    private final DefaultNotificationTextStrategy mDefaultNotificationTextStrategy;

    public KitKatNotificationTextStrategy(@NotNull final Context context) {
        mDefaultNotificationTextStrategy = new DefaultNotificationTextStrategy(context);
    }

    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        Bundle extras = statusBarNotification.getNotification().extras;

        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence bigTitle = extras.getCharSequence(Notification.EXTRA_TITLE_BIG);

        if (title != null) {
            return bigTitle != null ? String.valueOf(bigTitle) : String.valueOf(title);
        } else if (bigTitle != null) {
            return String.valueOf(bigTitle);
        } else {
            return mDefaultNotificationTextStrategy.createTitle(statusBarNotification);
        }
    }

    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        StringBuilder result = new StringBuilder(255);

        Bundle extras = statusBarNotification.getNotification().extras;

        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence textLines = extras.getCharSequence(Notification.EXTRA_TEXT_LINES);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

        if (text != null) {
            result.append(text).append('\n');
        }

        if (textLines != null) {
            result.append(textLines).append('\n');
        }

        if (subText != null) {
            result.append(subText).append('\n');
        }

        return result.toString().isEmpty() ? mDefaultNotificationTextStrategy.createText(statusBarNotification) : result.toString();
    }
}
