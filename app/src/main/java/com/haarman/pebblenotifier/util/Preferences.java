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

package com.haarman.pebblenotifier.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Preferences {

    private static final String HAS_NOTIFICATION_ACCESS = "has_notification_access";

    private static final String IS_GIVING_NOTIFICATION_ACCESS = "is_giving_notification_access";

    private static final String SHOULD_IGNORE_MULTIPLE_NOTIFICATIONS = "pref_ignoremultiple";

    private static final String SHOULD_SEND_WHEN_SCREEN_ON = "pref_sendwhenscreenon";

    @NotNull
    private final SharedPreferences mSharedPreferences;

    @Inject
    public Preferences(@ForApplication @NotNull final Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public boolean hasNotificationAccess() {
        return mSharedPreferences.getBoolean(HAS_NOTIFICATION_ACCESS, false);
    }

    public void setHasNotificationAccess(final boolean hasAccess) {
        mSharedPreferences.edit().putBoolean(HAS_NOTIFICATION_ACCESS, hasAccess).apply();
    }

    public void setIsGivingNotificationAccess(final boolean isGivingNotificationAccess) {
        mSharedPreferences.edit().putBoolean(IS_GIVING_NOTIFICATION_ACCESS, isGivingNotificationAccess).apply();
    }

    public boolean isGivingNotificationAccess() {
        return mSharedPreferences.getBoolean(IS_GIVING_NOTIFICATION_ACCESS, false);
    }

    public boolean shouldIgnoreMultipleNotifications() {
        return mSharedPreferences.getBoolean(SHOULD_IGNORE_MULTIPLE_NOTIFICATIONS, true);
    }

    public void setShouldIgnoreMultipleNotifications(final boolean ignore) {
        mSharedPreferences.edit().putBoolean(SHOULD_IGNORE_MULTIPLE_NOTIFICATIONS, ignore).apply();
    }

    public boolean shouldSendWhenScreenOn() {
        return mSharedPreferences.getBoolean(SHOULD_SEND_WHEN_SCREEN_ON, true);
    }

    public void setShouldSendWhenScreenOn(final boolean shouldSend) {
        mSharedPreferences.edit().putBoolean(SHOULD_SEND_WHEN_SCREEN_ON, shouldSend).apply();
    }

}
