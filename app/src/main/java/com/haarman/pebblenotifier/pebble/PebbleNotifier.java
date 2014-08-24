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

package com.haarman.pebblenotifier.pebble;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.haarman.pebblenotifier.events.NewNotificationEvent;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.ForApplication;
import com.haarman.pebblenotifier.util.Preferences;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class PebbleNotifier {

    private static final int IGNORE_INTERVAL_MILLIS = 1 * 60 * 1000;

    @Inject
    @NotNull @ForApplication
    protected Context mContext;

    @Inject
    @NotNull
    protected Preferences mPreferences;

    @Inject
    public PebbleNotifier(@NotNull final AppBus appBus) {
        appBus.register(this);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    @Subscribe
    public void onNewNotification(@NotNull final NewNotificationEvent event) {
        Notification notification = event.getNotification();
        App app = notification.getApp();

        if (!shouldNotify(app)) {
            return;
        }

        Intent intent = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map<String, String> data = new HashMap();
        data.put("title", notification.getTitle());
        data.put("body", notification.getText());

        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        intent.putExtra("messageType", "PEBBLE_ALERT");
        intent.putExtra("sender", app.getName());
        intent.putExtra("notificationData", notificationData);

        mContext.sendBroadcast(intent);

        app.updateLastNotified();
        app.update();
    }

    private boolean shouldNotify(@NotNull final App app) {
        if (app.isMuted()) {
            return false;
        }

        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (powerManager.isScreenOn() && !mPreferences.shouldSendWhenScreenOn()) {
            return false;
        }

        if (app.getLastNotified().plusMillis(IGNORE_INTERVAL_MILLIS).isAfterNow()) {
            return !mPreferences.shouldIgnoreMultipleNotifications();
        }

        return true;
    }
}
