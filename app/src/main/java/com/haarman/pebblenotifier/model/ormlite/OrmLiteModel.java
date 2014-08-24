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

import com.haarman.pebblenotifier.model.Model;
import com.haarman.pebblenotifier.model.OrmManager;
import com.haarman.pebblenotifier.util.ObservableImpl;
import com.j256.ormlite.field.DatabaseField;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.inject.Inject;

import dagger.Lazy;

public abstract class OrmLiteModel extends ObservableImpl implements Model {

    public static final String COLUMN_CREATED = "created";

    public static final String COLUMN_MODIFIED = "modified";

    @DatabaseField(columnName = COLUMN_CREATED, canBeNull = false)
    private final long mCreated = System.currentTimeMillis();

    @Inject
    @NotNull
    protected Lazy<OrmManager> mOrmManager;

    @DatabaseField(columnName = COLUMN_MODIFIED, canBeNull = false)
    private long mModified = System.currentTimeMillis();

    @Override
    public void create() {
        mOrmManager.get().create(this);
    }

    @Override
    public void update() {
        mOrmManager.get().update(this);
    }

    @Override
    public void refresh() {
        mOrmManager.get().refresh(this);
    }

    @Override
    public void delete() {
        mOrmManager.get().delete(this);
    }

    @Override
    @NotNull
    public DateTime getCreated() {
        return new DateTime(mCreated);
    }

    @Override
    @NotNull
    public DateTime getModified() {
        return new DateTime(mModified);
    }

    public void updateModified() {
        mModified = System.currentTimeMillis();
    }

    public void setOrmManager(@NotNull final OrmManager ormManager) {
        mOrmManager = new Lazy<OrmManager>() {
            @Override
            public OrmManager get() {
                return ormManager;
            }
        };
    }
}
