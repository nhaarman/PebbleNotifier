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
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.haarman.pebblenotifier.PebbleNotifierApplication;
import com.haarman.pebblenotifier.controller.main.MainActivity;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.model.OrmManager;
import com.haarman.pebblenotifier.notifications.strategies.NotificationTextStrategy;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.Injector;
import com.haarman.pebblenotifier.util.Preferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Seconds;

import javax.inject.Inject;

public class MyNotificationListenerService extends NotificationListenerService {

    @Inject
    @NotNull
    protected Preferences mPreferences;

    @Inject
    @NotNull
    protected OrmManager mOrmManager;

    @Inject
    @NotNull
    protected AppBus mAppBus;

    @Override
    public IBinder onBind(final Intent intent) {
        Injector.from(this).inject(this);

        mPreferences.setHasNotificationAccess(true);
        if (mPreferences.isGivingNotificationAccess()) {
            mPreferences.setIsGivingNotificationAccess(false);

            Intent activityIntent = new Intent(this, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(activityIntent);
        }
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        mPreferences.setHasNotificationAccess(false);

        return super.onUnbind(intent);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        handleNotification(sbn);
    }

    @Override
    public void onNotificationRemoved(final StatusBarNotification sbn) {
    }

    @TargetApi(Build.VERSION_CODES.L)
    @Override
    public void onNotificationPosted(final StatusBarNotification sbn, final RankingMap rankingMap) {
        handleNotification(sbn);
    }

    private void handleNotification(@NotNull final StatusBarNotification statusBarNotification) {
        App app = mOrmManager.findById(App.class, statusBarNotification.getPackageName());
        if (app == null) {
            app = ((PebbleNotifierApplication) getApplication()).getGraph().get(App.class);
            app.setPackageName(statusBarNotification.getPackageName());
            app.create();
        } else {
            Injector.from(this).inject(app);
        }

        Notification notification = createNotification(app, statusBarNotification);
        if (notification != null && !shouldIgnoreNotification(notification)) {
            notification.create();
            mAppBus.postNewNotificationEvent(notification);
        }
    }

    @Nullable
    private Notification createNotification(@NotNull final App app, @NotNull final StatusBarNotification statusBarNotification) {
        NotificationTextStrategy notificationTextStrategy = NotificationTextStrategyFactory.getNotificationTextStrategy(this, statusBarNotification);
        String title = notificationTextStrategy.createTitle(statusBarNotification);
        String text = notificationTextStrategy.createText(statusBarNotification);

        Notification result = null;

        if (title != null && text != null) {
            result = ((PebbleNotifierApplication) getApplication()).getGraph().get(Notification.class);
            result.setApp(app);
            result.setTitle(title);
            result.setText(text);
        }

        return result;
    }

    private boolean shouldIgnoreNotification(@NotNull final Notification notification) {
        Notification lastNotification = mOrmManager.getLastNotification();
        if (lastNotification == null) {
            return false;
        }

        boolean result = false;
        if (notification.getTitle().equals(lastNotification.getTitle())) {
            if (notification.getText().equals(lastNotification.getText())) {
                if (Seconds.secondsBetween(lastNotification.getCreated(), notification.getCreated()).getSeconds() < 60) {
                    result = true;
                }
            }
        }

        return result;
    }

}
