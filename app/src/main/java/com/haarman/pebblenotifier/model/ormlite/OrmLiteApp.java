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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.util.ForApplication;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.inject.Inject;

@DatabaseTable(tableName = OrmLiteApp.TABLE_NAME)
public class OrmLiteApp extends OrmLiteModel implements App {

    public static final String TABLE_NAME = "apps";

    public static final String COLUMN_PACKAGENAME = "packagename";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_MUTED = "muted";

    private static final String COLUMN_LAST_NOTIFIED = "last_notified";

    @Inject
    @ForApplication
    protected Context mContext;

    @NotNull
    @DatabaseField(id = true, canBeNull = false, columnName = COLUMN_PACKAGENAME)
    private String mPackageName = "";

    @NotNull
    @DatabaseField(defaultValue = "", columnName = COLUMN_NAME)
    private String mName = "";

    @DatabaseField(columnName = COLUMN_MUTED)
    private boolean mMuted;

    @DatabaseField(columnName = COLUMN_LAST_NOTIFIED)
    private long mLastNotified = System.currentTimeMillis();

    public static OrmLiteApp empty() {
        OrmLiteApp ormLiteApp = new OrmLiteApp();
        ormLiteApp.setPackageName(OrmLiteApp.class.getPackage().getName());
        return ormLiteApp;
    }

    @Override
    @NotNull
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public void setPackageName(@NotNull final String packageName) {
        mPackageName = packageName;

        PackageManager pm = mContext.getApplicationContext().getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            setName(ai != null ? (String) pm.getApplicationLabel(ai) : "");
        } catch (final PackageManager.NameNotFoundException ignored) {
        }

        setChanged();
    }

    @Override
    @NotNull
    public String getName() {
        return mName;
    }

    @Override
    public void setName(@NotNull final String name) {
        mName = name;
        setChanged();
    }

    @Override
    public boolean isMuted() {
        return mMuted;
    }

    @Override
    public void setMuted(final boolean muted) {
        mMuted = muted;
        setChanged();
    }

    @NotNull
    @Override
    public DateTime getLastNotified() {
        return new DateTime(mLastNotified);
    }

    @Override
    public void updateLastNotified() {
        mLastNotified = System.currentTimeMillis();
    }
}
