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
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.Observable;
import com.haarman.pebblenotifier.util.Observer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class NotificationView extends LinearLayout implements Observer<Notification> {

    @NotNull
    @Optional @InjectView(R.id.view_notification_appiv)
    protected ImageView mImageView;

    @NotNull
    @Optional @InjectView(R.id.view_notification_titletv)
    protected TextView mTitleTV;

    @NotNull
    @Optional @InjectView(R.id.view_notification_texttv)
    protected TextView mTextTV;

    @NotNull
    @Optional @InjectView(R.id.view_notification_muteiv)
    protected View mMuteView;

    @Nullable
    private Notification mNotification;

    public NotificationView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnMuteButtonClickListener(@NotNull final OnClickListener onClickListener) {
        mMuteView.setOnClickListener(onClickListener);
    }

    @Override
    public void addView(@NotNull final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ButterKnife.inject(this);
    }

    public void setNotification(@NotNull final Notification notification) {
        if (mNotification != null) {
            mNotification.deleteObserver(this);
        }
        mNotification = notification;

        mNotification.addObserver(this);
        applyNotification(mNotification);
    }

    private void applyNotification(@NotNull final Notification notification) {
        mTitleTV.setText(notification.getTitle());
        mTextTV.setText(notification.getText());
        mMuteView.setActivated(notification.getApp().isMuted());

        Drawable applicationIcon = null;
        try {
            applicationIcon = getContext().getApplicationContext().getPackageManager().getApplicationIcon(notification.getApp().getPackageName());
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        mImageView.setImageDrawable(applicationIcon);
    }

    @Override
    public void update(@NotNull final Observable<Notification> observable, @Nullable final Notification notification) {
        if (mNotification != null) {
            applyNotification(mNotification);
        } else {
            observable.deleteObserver(this);
        }
    }
}
