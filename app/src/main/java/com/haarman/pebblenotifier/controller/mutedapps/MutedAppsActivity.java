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

package com.haarman.pebblenotifier.controller.mutedapps;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.haarman.pebblenotifier.PebbleNotifierActivity;
import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.events.ToggleMuteAppEvent;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.async.QueryMutedAppsTask;
import com.haarman.pebblenotifier.model.async.UpdateTask;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.view.mutedapps.MutedAppsView;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MutedAppsActivity extends PebbleNotifierActivity {

    @NotNull
    @Inject
    protected AppBus mBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutedapps);

        ButterKnife.inject(this);
        mBus.register(this);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new QueryMutedAppsTask(this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @Subscribe
    public void onToggleMuteAppClicked(@NotNull final ToggleMuteAppEvent event) {
        Crashlytics.log("MutedAppsActivity.onToggleMuteAppClicked");

        App app = event.getApp();

        app.setMuted(!app.isMuted());
        app.notifyObservers();

        new UpdateTask<>(app).execute();
    }

}
