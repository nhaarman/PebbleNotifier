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

package com.haarman.pebblenotifier.controller.main;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.haarman.pebblenotifier.PebbleNotifierActivity;
import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.controller.about.AboutActivity;
import com.haarman.pebblenotifier.controller.mutedapps.MutedAppsActivity;
import com.haarman.pebblenotifier.controller.preferences.PreferencesActivity;
import com.haarman.pebblenotifier.events.NotificationRemovedEvent;
import com.haarman.pebblenotifier.events.RetrievedNotificationListEvent;
import com.haarman.pebblenotifier.events.SendNotificationEvent;
import com.haarman.pebblenotifier.events.ToggleMuteNotificationEvent;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.model.async.DeleteTask;
import com.haarman.pebblenotifier.model.async.QueryAllNotificationsTask;
import com.haarman.pebblenotifier.model.async.UpdateTask;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.Preferences;
import com.haarman.pebblenotifier.view.main.MainView;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * The Main controller Activity, which hosts a MainView.
 */
public class MainActivity extends PebbleNotifierActivity {

    private static final String SAVEDINSTANCESTATE_MAINVIEW = "mainview";

    /**
     * The view this Activity shows.
     */
    @NotNull
    @InjectView(R.id.activity_main_mainpresenter)
    protected MainView mMainView;

    /**
     * The AppBus this Activity subscribes and posts to.
     */
    @Inject
    @NotNull
    protected AppBus mAppBus;

    /**
     * The user's preferences.
     */
    @Inject
    @NotNull
    protected Preferences mPreferences;

    /**
     * The list of Notifications to be shown.
     */
    @NotNull
    private List<Notification> mNotificationList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppBus.register(this);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    /**
     * Fires a QueryAllNotificationsTask which will eventually post a RetrievedNotificationListEvent
     * with the retrieved Notifications on the AppBus.
     */
    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        QueryAllNotificationsTask queryAllNotificationsTask = new QueryAllNotificationsTask(this);
        queryAllNotificationsTask.setComparator(new ReverseCreatedNotificationComparator());
        queryAllNotificationsTask.execute();
    }

    /**
     * If the user hasn't given notification access before, shows an AlertDialog asking the user
     * to grant this permission.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!mPreferences.hasNotificationAccess()) {
            mPreferences.setIsGivingNotificationAccess(true);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.activity_main_dialog_enableaccess_title))
                    .setMessage(getString(R.string.activity_main_dialog_enableaccess_message))
                    .setPositiveButton(android.R.string.ok, new EnableAccessOnClickListener())
                    .setNegativeButton(android.R.string.cancel, new CancelOnClickListener())
                    .show();
        }
    }

    /**
     * Updates the Notification list with the list in the event.
     *
     * @param event the RetrievedNotificationListEvent which contains the list of Notifications.
     */
    @Subscribe
    public void setNotificationList(@NotNull final RetrievedNotificationListEvent event) {
        mNotificationList = event.getList();
    }

    /**
     * Toggles the mute status for the Notification provided by the event.
     *
     * @param event the ToggleMuteNotificationEvent which contains the Notification.
     */
    @Subscribe
    public void onMuteNotificationClicked(@NotNull final ToggleMuteNotificationEvent event) {
        Crashlytics.log("MainActivity.onMuteNotificationClicked");

        App app = event.getNotification().getApp();
        inject(app);

        app.setMuted(!app.isMuted());
        app.notifyObservers();

        UpdateTask<App> appUpdateTask = new UpdateTask<>(app);
        appUpdateTask.execute();
    }

    /**
     * Fires a new Notification to the Android NotificationManager.
     *
     * @param event the SendNotificationEvent.
     */
    @Subscribe
    public void onSendNotificationClicked(@Nullable final SendNotificationEvent event) {
        Crashlytics.log("MainActivity.onSendNotificationClicked");

        android.app.Notification notification = new android.app.Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.activity_main_testnotification_message))
                .setTicker(getString(R.string.activity_main_testnotification_message))
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setPriority(android.app.Notification.PRIORITY_DEFAULT)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }

    /**
     * Removes the Notification provided by the event from the list and from the database.
     *
     * @param event the NotificationRemovedEvent which contains the Notification.
     */
    @Subscribe
    public void onNotificationRemovedEvent(@NotNull final NotificationRemovedEvent event) {
        Crashlytics.log("MainActivity.onNotificationRemovedEvent");

        Notification notification = event.getNotification();
        mNotificationList.remove(notification);

        new DeleteTask<>(notification).execute();
    }

    /**
     * Unregisters this instance with the AppBus, to avoid leakage.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBus.unregister(this);
    }

    /**
     * Saves the state of the View.
     */
    @Override
    protected void onSaveInstanceState(@NotNull final Bundle outState) {
        outState.putParcelable(SAVEDINSTANCESTATE_MAINVIEW, mMainView.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    /**
     * Restores the View with given configuration.
     */
    @Override
    protected void onRestoreInstanceState(@NotNull final Bundle savedInstanceState) {
        mMainView.onRestoreInstanceState(savedInstanceState.getParcelable(SAVEDINSTANCESTATE_MAINVIEW));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mutedapps:
                startActivity(new Intent(this, MutedAppsActivity.class));
                return true;
            case R.id.menu_notify:
                onSendNotificationClicked(null);
                return true;
            case R.id.menu_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * An OnClickListener for when the user wants to grant notification access.
     */
    private class EnableAccessOnClickListener implements DialogInterface.OnClickListener {

        /**
         * Opens the settings menu where the user can grant notification access to this application.
         */
        @Override
        public void onClick(final DialogInterface dialogInterface, final int i) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"); //NON-NLS
            startActivity(intent);
        }
    }

    /**
     * An OnClickListener for when the user doesn't want to grant notification access.
     */
    private class CancelOnClickListener implements DialogInterface.OnClickListener {

        /**
         * Finishes the application, as the application won't work without permission access.
         */
        @Override
        public void onClick(final DialogInterface dialogInterface, final int i) {
            finish();
        }
    }

    /**
     * A Comparator where the Notifications are sorted by decreasing created date.
     */
    private static class ReverseCreatedNotificationComparator implements Comparator<Notification>, Serializable {

        @Override
        public int compare(@NotNull final Notification lhs, @NotNull final Notification rhs) {
            return rhs.getCreated().compareTo(lhs.getCreated());
        }
    }

}
