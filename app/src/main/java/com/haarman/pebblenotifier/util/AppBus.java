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

import android.os.Handler;
import android.os.Looper;

import com.haarman.pebblenotifier.events.NewNotificationEvent;
import com.haarman.pebblenotifier.events.NotificationRemovedEvent;
import com.haarman.pebblenotifier.events.RetrievedAppListEvent;
import com.haarman.pebblenotifier.events.RetrievedNotificationListEvent;
import com.haarman.pebblenotifier.events.SendNotificationEvent;
import com.haarman.pebblenotifier.events.ToggleMuteAppEvent;
import com.haarman.pebblenotifier.events.ToggleMuteNotificationEvent;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.squareup.otto.Bus;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppBus extends Bus {

    private final Handler mMainThread = new Handler(Looper.getMainLooper());

    public void postToggleMuteNotificationEvent(@NotNull final Notification notification) {
        post(new ToggleMuteNotificationEvent(notification));
    }

    public void postToggleMuteAppEvent(@NotNull final App app) {
        post(new ToggleMuteAppEvent(app));
    }

    public void postSendNotificationEvent() {
        post(new SendNotificationEvent());
    }

    public void postRetrievedNotificationListEvent(@NotNull final List<Notification> notificationList) {
        post(new RetrievedNotificationListEvent(notificationList));
    }

    public void postRetrievedAppListEvent(@NotNull final List<App> notificationList) {
        post(new RetrievedAppListEvent(notificationList));
    }

    public void postNewNotificationEvent(final Notification notification) {
        post(new NewNotificationEvent(notification));
    }

    public void postNotificationRemovedEvent(final Notification notification) {
        post(new NotificationRemovedEvent(notification));
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mMainThread.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            AppBus.super.post(event);
                        }
                    }
            );
        }
    }
}
