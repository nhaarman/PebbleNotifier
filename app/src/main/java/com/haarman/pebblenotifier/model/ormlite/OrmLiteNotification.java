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

package com.haarman.pebblenotifier.model.ormlite;

import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.Observable;
import com.haarman.pebblenotifier.util.Observer;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;


@DatabaseTable(tableName = OrmLiteNotification.TABLE_NAME)
public class OrmLiteNotification extends OrmLiteModel implements Notification {

    public static final String TABLE_NAME = "notifications";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_TEXT = "text";

    public static final String COLUMN_PACKAGE_ID = "app_id";

    @NotNull
    private final Observer<App> mAppObserver = new AppObserver();

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    private int mId;

    @NotNull
    @DatabaseField(canBeNull = false, columnName = COLUMN_TITLE)
    private String mTitle = "";

    @NotNull
    @DatabaseField(canBeNull = false, columnName = COLUMN_TEXT)
    private String mText = "";

    @Nullable
    @DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3, canBeNull = false, useGetSet = true, columnName = COLUMN_PACKAGE_ID)
    private OrmLiteApp mApp;

    @Inject
    public OrmLiteNotification() {
    }

    @Override
    public int getId() {
        return mId;
    }

    @NotNull
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(@NotNull final String title) {
        mTitle = title;
    }

    @NotNull
    @Override
    public String getText() {
        return mText;
    }

    @Override
    public void setText(@NotNull final String text) {
        mText = text;
        setChanged();
    }

    @NotNull
    @Override
    public App getApp() {
        return mApp == null ? OrmLiteApp.empty() : mApp;
    }

    @Override
    public void setApp(@NotNull final App app) {
        mApp = (OrmLiteApp) app;
        app.addObserver(mAppObserver);
        setChanged();
    }

    public OrmLiteApp getMApp() {
        return mApp;
    }

    public void setMApp(@NotNull final OrmLiteApp app) {
        setApp(app);
    }

    private class AppObserver implements Observer<App> {

        @Override
        public void update(@NotNull final Observable<App> observable, @Nullable final App app) {
            setChanged();
            notifyObservers();
        }
    }
}
