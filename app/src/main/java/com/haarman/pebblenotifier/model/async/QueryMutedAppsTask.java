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

import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.OrmManager;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.Injector;
import com.label305.stan.async.AsyncTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class QueryMutedAppsTask extends AsyncTask<List<App>> {

    @NotNull
    private final Context mContext;

    @Inject
    @NotNull
    protected OrmManager mOrmManager;

    @Inject
    @NotNull
    protected AppBus mBus;

    public QueryMutedAppsTask(@NotNull final Context context) {
        Injector.from(context).inject(this);
        mContext = context;
    }

    @Nullable
    @Override
    public List<App> call() {
        List<App> mutedApps = mOrmManager.getMutedApps();

        for (App app : mutedApps) {
            Injector.from(mContext).inject(app);
        }

        return mutedApps;
    }

    @Override
    protected void onSuccess(@Nullable final List<App> list) {
        mBus.postRetrievedAppListEvent(list == null ? new ArrayList<App>() : list);
    }

    @Override
    protected void onException(@NotNull final Exception e) {
        throw new RuntimeException(e);
    }

}