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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.util.Injector;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class MutedAppAdapter extends BaseAdapter {

    @NotNull
    private final Context mContext;

    @NotNull
    private final List<App> mAppList;

    @NotNull
    @Inject
    protected AppBus mBus;

    public MutedAppAdapter(@NotNull final Context context, @NotNull final List<App> appList) {
        mContext = context;
        mAppList = appList;

        Injector.from(context).inject(this);
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public App getItem(final int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getPackageName().hashCode();
    }

    @Override
    @NotNull
    public View getView(final int position, @Nullable final View convertView, @NotNull final ViewGroup parent) {
        AppView appView = (AppView) convertView;
        if (appView == null) {
            appView = createAppView(parent);
        }

        appView.setOnMuteButtonClickListener(new OnMuteButtonClickListener(position));
        appView.setApp(getItem(position));

        return appView;
    }

    @NotNull
    private AppView createAppView(@NotNull final ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return (AppView) inflater.inflate(R.layout.view_app, parent, false);
    }

    private class OnMuteButtonClickListener implements View.OnClickListener {

        private final int mPosition;

        private OnMuteButtonClickListener(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final View view) {
            App app = getItem(mPosition);
            mBus.postToggleMuteAppEvent(app);
        }
    }
}
