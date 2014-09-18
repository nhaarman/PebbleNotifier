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
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A NotificationTextStrategy which uses reflection to find text in the notification.
 */
public class ViewInspectorNotificationStrategy implements NotificationTextStrategy {

    @Override
    public String createTitle(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = getStrings(statusBarNotification.getNotification());
        if (strings.isEmpty()) {
            return null;
        } else {
            return strings.get(0);
        }
    }

    @Override
    public String createText(@NotNull final StatusBarNotification statusBarNotification) {
        List<String> strings = getStrings(statusBarNotification.getNotification());
        if (strings.size() <= 1) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(255);

        for (int i = 1; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i)).append('\n');
        }

        return stringBuilder.toString();
    }

    /**
     * Retrieves the list of Strings using reflection.
     * <p/>
     * See http://stackoverflow.com/a/20322326/675383.
     *
     * @param notification the Notification
     *
     * @return the list of Strings.
     */
    @NotNull
    private List<String> getStrings(@NotNull final Notification notification) {
        List<String> result = new ArrayList<>();

        // We have to extract the information from the view
        RemoteViews views = notification.bigContentView;
        if (views == null) {
            views = notification.contentView;
        }
        if (views == null) {
            return result;
        }

        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            Iterable<Parcelable> actions = (Iterable<Parcelable>) field.get(views);

            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) {
                    continue;
                }

                // View ID
                parcel.readInt();

                String methodName = parcel.readString();
                if (methodName == null) {
                    continue;
                }

                // Save strings
                if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();

                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    result.add(t);
                }

                parcel.recycle();
            }
        }

        // It's not usually good style to do this, but then again, neither is the use of reflection...
        catch (Exception e) {
            Log.e("NotificationClassifier", e.toString());
        }

        return result;
    }
}
