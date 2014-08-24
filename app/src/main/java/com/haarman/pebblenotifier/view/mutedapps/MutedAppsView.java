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

package com.haarman.pebblenotifier.view.mutedapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.haarman.pebblenotifier.events.RetrievedAppListEvent;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.events.RetrievedListEvent;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.util.Injector;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MutedAppsView extends RelativeLayout {

    @NotNull
    @Inject
    protected AppBus mBus;

    @NotNull
    @InjectView(R.id.view_mutedapps_listview)
    protected DynamicListView mListView;

    public MutedAppsView(@NotNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutedAppsView(@NotNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injector.from(context).inject(this);
        mBus.register(this);
    }

    @Override
    public void addView(@NotNull final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ButterKnife.inject(this);
    }

    @Subscribe
    public void setAppList(@NotNull final RetrievedAppListEvent event) {
        MutedAppAdapter adapter = new MutedAppAdapter(getContext(), event.getList());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);

        if (adapter.getCount() == 0) {
            findViewById(R.id.view_mutedapps_nomutedappstv).setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBus.unregister(this);
    }

}
