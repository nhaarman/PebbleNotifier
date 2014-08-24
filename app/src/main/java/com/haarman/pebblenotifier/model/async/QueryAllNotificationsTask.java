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

package com.haarman.pebblenotifier.model.async;

import android.content.Context;

import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.model.OrmManager;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.Injector;
import com.label305.stan.async.AsyncTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class QueryAllNotificationsTask extends AsyncTask<List<Notification>> {

    @NotNull
    private final Context mContext;

    @Inject
    @NotNull
    protected OrmManager mOrmManager;

    @Inject
    @NotNull
    protected AppBus mBus;

    @Nullable
    private Comparator<Notification> mComparator;

    public QueryAllNotificationsTask(@NotNull final Context context) {
        Injector.from(context).inject(this);
        mContext = context;
    }

    public void setComparator(@Nullable final Comparator<Notification> comparator) {
        mComparator = comparator;
    }

    @Nullable
    @Override
    public List<Notification> call() {
        List<Notification> results = mOrmManager.all(Notification.class);

        if (results != null && !results.isEmpty()) {
            if (mComparator != null) {
                Collections.sort(results, mComparator);
            }

            for (Notification notification : results) {
                Injector.from(mContext).inject(notification);
            }
        }

        return results;
    }

    @Override
    protected void onSuccess(@Nullable final List<Notification> list) {
        mBus.postRetrievedNotificationListEvent(list == null ? new ArrayList<Notification>() : list);
    }

    @Override
    protected void onException(@NotNull final Exception e) {
        throw new RuntimeException(e);
    }
}