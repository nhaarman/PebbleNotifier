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

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@SuppressWarnings("HardCodedStringLiteral")
public class DefaultNotificationTextStrategy implements NotificationTextStrategy {

    @NotNull
    private final Context mContext;

    @NotNull private final NotificationTextStrategy mNotificationTextStrategy;

    public DefaultNotificationTextStrategy(@NotNull final Context context) {
        mContext = context;
        mNotificationTextStrategy = new ViewInspectorNotificationStrategy();
    }

    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        String result;

        Bundle extras = getExtras(statusBarNotification);
        if (extras == null) {
            result = getAppName(statusBarNotification);
        } else {
            result = getExtrasTitle(extras);
        }

        return result == null ? mNotificationTextStrategy.createTitle(statusBarNotification) : result;
    }

    private String getAppName(@NotNull final StatusBarNotification statusBarNotification) {
        String result = "";

        PackageManager pm = mContext.getApplicationContext().getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(statusBarNotification.getPackageName(), 0);
            result = ai != null ? (String) pm.getApplicationLabel(ai) : "";
        } catch (final PackageManager.NameNotFoundException ignored) {
            /* Unfortunately, we cannot show anything. */
        }
        return result;
    }

    private String getExtrasTitle(@NotNull final Bundle extras) {
        String result;

        CharSequence title = extras.getCharSequence("android.title");
        CharSequence bigTitle = extras.getCharSequence("android.title.big");

        if (title != null) {
            result = bigTitle != null ? String.valueOf(bigTitle) : String.valueOf(title);
        } else {
            result = bigTitle != null ? String.valueOf(bigTitle) : "";
        }

        return result;
    }

    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        Bundle extras = getExtras(statusBarNotification);
        if (extras == null) {
            return String.valueOf(statusBarNotification.getNotification().tickerText);
        }

        StringBuilder result = new StringBuilder(255);

        CharSequence text = extras.getCharSequence("android.text");
        CharSequence textLines = extras.getCharSequence("android.textLines");
        CharSequence subText = extras.getCharSequence("android.subText");

        if (text != null) {
            result.append(text).append('\n');
        }

        if (textLines != null) {
            result.append(textLines).append('\n');
        }

        if (subText != null) {
            result.append(subText).append('\n');
        }

        return result.toString().isEmpty() ? mNotificationTextStrategy.createText(statusBarNotification) : result.toString();
    }

    @Nullable
    private Bundle getExtras(@NotNull final StatusBarNotification statusBarNotification) {
        try {
            Notification notification = statusBarNotification.getNotification();
            Field extrasField = notification.getClass().getField("extras");
            return (Bundle) extrasField.get(notification);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Crashlytics.logException(e);
            return null;
        }
    }
}
