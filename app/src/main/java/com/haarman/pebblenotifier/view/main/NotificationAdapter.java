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

package com.haarman.pebblenotifier.view.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.Injector;
import com.nhaarman.listviewanimations.util.Insertable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NotificationAdapter extends BaseAdapter implements Insertable<Notification> {

    @NotNull
    private final Context mContext;

    @NotNull
    @Inject
    protected AppBus mBus;

    @NotNull
    private List<Notification> mNotificationList = new ArrayList<>(0);

    public NotificationAdapter(@NotNull final Context context) {
        mContext = context;
        Injector.from(context).inject(this);
    }

    public void setNotificationList(@NotNull final List<Notification> notificationList) {
        mNotificationList = notificationList;
    }

    @Override
    public int getCount() {
        return mNotificationList.size();
    }

    @Override
    public Notification getItem(final int position) {
        return mNotificationList.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    public void add(final int index, @NotNull final Notification item) {
        mNotificationList.add(index, item);
        notifyDataSetChanged();
    }

    @Override
    @NotNull
    public View getView(final int position, @Nullable final View convertView, @NotNull final ViewGroup parent) {
        NotificationView notificationView = (NotificationView) convertView;
        if (notificationView == null) {
            notificationView = createNotificationView(parent);
        }

        notificationView.setOnMuteButtonClickListener(new OnMuteButtonClickListener(position));
        notificationView.setNotification(getItem(position));

        return notificationView;
    }

    @NotNull
    private NotificationView createNotificationView(@NotNull final ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return (NotificationView) inflater.inflate(R.layout.view_notification, parent, false);
    }

    public void remove(final int position) {
        mNotificationList.remove(position);
    }

    private class OnMuteButtonClickListener implements View.OnClickListener {

        private final int mPosition;

        private OnMuteButtonClickListener(final int position) {
            mPosition = position;
        }

        @Override
        public void onClick(final View view) {
            Notification notification = getItem(mPosition);
            mBus.postToggleMuteNotificationEvent(notification);
        }
    }
}