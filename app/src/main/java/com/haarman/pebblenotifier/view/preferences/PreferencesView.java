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

package com.haarman.pebblenotifier.view.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.util.Injector;
import com.haarman.pebblenotifier.util.Preferences;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.Optional;

/**
 * Shows a view for managing user preferences.
 */
public class PreferencesView extends RelativeLayout {

    @Inject
    protected Preferences mPreferences;

    @Optional @InjectView(R.id.view_preferences_ignoremultiplecb)
    protected CheckBox mIgnoreMultipleTV;

    @Optional @InjectView(R.id.view_preferences_sendwhenscreenoncb)
    protected CheckBox mSendWhenScreenOnTV;

    public PreferencesView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferencesView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injector.from(context).inject(this);
    }

    /**
     * Because of the way this view is created, here we intercept the child views
     * being added, and assign them to our fields.
     */
    @Override
    public void addView(@NotNull final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ButterKnife.inject(this);

        if (child.equals(mIgnoreMultipleTV)) {
            mIgnoreMultipleTV.setChecked(mPreferences.shouldIgnoreMultipleNotifications());
        } else if (child.equals(mSendWhenScreenOnTV)) {
            mSendWhenScreenOnTV.setChecked(mPreferences.shouldSendWhenScreenOn());
        }
    }

    /**
     * Toggles the 'should ignore multiple notifications' preference setting.
     */
    @Optional
    @OnCheckedChanged(R.id.view_preferences_ignoremultiplecb)
    public void onIgnoreMultipleCheckedChanged() {
        mPreferences.setShouldIgnoreMultipleNotifications(mIgnoreMultipleTV.isChecked());
    }

    /**
     * Toggles the 'should send when screen on' preference setting.
     */
    @Optional
    @OnCheckedChanged(R.id.view_preferences_sendwhenscreenoncb)
    public void onSendWhenScreenOnCheckedChanged() {
        mPreferences.setShouldSendWhenScreenOn(mSendWhenScreenOnTV.isChecked());
    }
}
