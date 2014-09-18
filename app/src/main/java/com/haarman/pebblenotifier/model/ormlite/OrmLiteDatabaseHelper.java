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
import android.database.sqlite.SQLiteDatabase;

import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.model.OrmManager;
import com.haarman.pebblenotifier.util.ForApplication;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("rawtypes")
public class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper implements OrmManager {

    private static final String DATABASE_NAME = "pebblenotifier-2";

    private static final int DATABASE_VERSION = 1;

    private static final Class<?>[] CLASSES = {OrmLiteNotification.class, OrmLiteApp.class};

    @Inject
    public OrmLiteDatabaseHelper(@ForApplication @NotNull final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        enableCache();
    }

    private void enableCache() {
        for (Class<?> clzz : CLASSES) {
            getRuntimeExceptionDao(clzz).setObjectCache(true);
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase database, final ConnectionSource connectionSource) {
        try {
            for (Class<?> clzz : CLASSES) {
                TableUtils.createTable(connectionSource, clzz);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final ConnectionSource connectionSource, final int oldVersion, final int newVersion) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public <T> void create(@NotNull final T object) {
        if (!(object instanceof OrmLiteModel)) {
            throw new IllegalArgumentException("Only OrmLiteModel instances are supported.");
        }

        RuntimeExceptionDao dao = getRuntimeExceptionDao(object.getClass());
        dao.create(object);
    }

    @Override
    public <T> void update(@NotNull final T object) {
        if (!(object instanceof OrmLiteModel)) {
            throw new IllegalArgumentException("Only OrmLiteModel instances are supported.");
        }

        ((OrmLiteModel) object).updateModified();

        RuntimeExceptionDao dao = getRuntimeExceptionDao(object.getClass());
        dao.update(object);
    }

    @Override
    public <T> void refresh(final T object) {
        if (!(object instanceof OrmLiteModel)) {
            throw new IllegalArgumentException("Only OrmLiteModel instances are supported.");
        }

        RuntimeExceptionDao dao = getRuntimeExceptionDao(object.getClass());
        dao.refresh(object);

        for (Object o : getForeignObjects(object)) {
            refresh(o);
        }
    }

    @Override
    public <T> void delete(final T object) {
        if (!(object instanceof OrmLiteModel)) {
            throw new IllegalArgumentException("Only OrmLiteModel instances are supported.");
        }

        RuntimeExceptionDao dao = getRuntimeExceptionDao(object.getClass());
        dao.delete(object);
    }

    @Override
    public <T, V> T findById(final Class<T> clzz, final V id) {
        RuntimeExceptionDao dao = getRuntimeExceptionDao(translateClass(clzz));
        return (T) dao.queryForId(id);
    }

    private List<Object> getForeignObjects(final Object object) {
        List<Object> results = new ArrayList<>();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            DatabaseField annotation = field.getAnnotation(DatabaseField.class);
            if (annotation != null) {
                if (annotation.foreign()) {
                    try {
                        field.setAccessible(true);
                        results.add(field.get(object));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public <T> List<T> all(@NotNull final Class<T> clzz) {
        Class<?> clazz = translateClass(clzz);
        return (List<T>) getRuntimeExceptionDao(clazz).queryForAll();
    }

    @Override
    public List<App> getMutedApps() {
        RuntimeExceptionDao dao = getRuntimeExceptionDao(OrmLiteApp.class);
        QueryBuilder queryBuilder = dao.queryBuilder();
        try {
            queryBuilder.where().eq(OrmLiteApp.COLUMN_MUTED, true);
            queryBuilder.orderBy(OrmLiteApp.COLUMN_NAME, true);
            return queryBuilder.query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public Notification getLastNotification() {
        RuntimeExceptionDao<OrmLiteNotification, ?> dao = getRuntimeExceptionDao(OrmLiteNotification.class);
        QueryBuilder<OrmLiteNotification, ?> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(OrmLiteModel.COLUMN_CREATED, false);
        try {
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Class<?> translateClass(@NotNull final Class<T> clzz) {
        for (Class<?> tableClzz : CLASSES) {
            if (clzz.isAssignableFrom(tableClzz)) {
                return tableClzz;
            }
        }

        throw new IllegalArgumentException("Could not create OrmLiteModel class from: " + clzz.getName());
    }
}
