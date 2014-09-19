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
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.haarman.pebblenotifier.R;
import com.haarman.pebblenotifier.events.NewNotificationEvent;
import com.haarman.pebblenotifier.events.RetrievedNotificationListEvent;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.AppBus;
import com.haarman.pebblenotifier.util.Injector;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Shows a ListView containing Notifications.
 * <p/>
 * This View subscribes to the AppBus to receive a RetrievedNotificationListEvent,
 * and shows the Notifications that come with the event in the ListView.
 * <p/>
 * It also subscribes to NewNotificationEvents, and uses the ListView to insert
 * the new Notification directly in the adapter.
 * Note that at this point, the View controls the data set. This is due to the
 * animation implementation in the ListView.
 * <p/>
 * This View also posts to the AppBus:
 * <ul>
 * <li>SendNotificationEvent - when the user wants to send a notification;</li>
 * <li>NotificationRemovedEvent - when the user removed a notification. At this point,
 * the View does _not_ control the dataset. The controller is expected to remove
 * the Notification from the dataset.</li>
 * </ul>
 */
public class MainView extends RelativeLayout {

    private static final String SAVEDINSTANCESTATE_INSTANCE = "instance_state";

    private static final String SAVEDINSTANCESTATE_LISTVIEW = "listview";

    private static final String SAVEDINSTACESTATE_ANIMATIONADAPTER = "animation_adapter";

    /**
     * A wrapping list adapter that provides animations to the list items.
     * This adapter wrappes mAdapter, and is directly applied to mListView.
     */
    @NotNull
    private final AlphaInAnimationAdapter mAnimationAdapter;

    /**
     * The list adapter which holds the notifications.
     */
    @NotNull
    @Inject
    protected NotificationAdapter mAdapter;

    /**
     * The ListView implementation.
     * This ListView provides us with swipe-to-dismiss and insertion animation functionality.
     */
    @NotNull
    @Optional
    @InjectView(R.id.view_main_listview)
    protected DynamicListView mListView;

    /**
     * The TextView that is shown when there are no notifications.
     */
    @NotNull
    @Optional
    @InjectView(R.id.view_main_nonotificationstv)
    protected View mNoNotificationsView;

    /**
     * The button that can be pressed to send notifications.
     * This is also only shown when there are no notifications.
     */
    @NotNull
    @Optional
    @InjectView(R.id.view_main_sendnotificationbutton)
    protected View mSendNotificationButton;

    /**
     * The AppBus this View subcribes and posts to.
     */
    @NotNull
    @Inject
    protected AppBus mAppBus;

    public MainView(@NotNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainView(@NotNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injector.from(context).inject(this);
        mAppBus.register(this);

        mAnimationAdapter = new AlphaInAnimationAdapter(mAdapter);
        mAnimationAdapter.setAbsListView(mListView);
    }

    /**
     * Because of the way this view is created, here we intercept the child views
     * being added, and assign them to our fields.
     */
    @Override
    public void addView(@NotNull final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ButterKnife.inject(this);

        //noinspection ConstantConditions
        if (mListView != null) {
            mListView.setAdapter(mAnimationAdapter);
            mListView.enableSwipeToDismiss(new MyOnDismissCallback());
        }
    }

    /**
     * Replaces any existing Notification list in the adapter by the list supplied in the event.
     * Will also adjust visibility of the placeholder views when no Notifications are available.
     *
     * @param event the RetrievedNotificationListEvent containing the Notification list.
     */
    @Subscribe
    public void setNotificationList(@NotNull final RetrievedNotificationListEvent event) {
        mAdapter.setNotificationList(event.getList());
        mAdapter.notifyDataSetChanged();

        if (mAdapter.getCount() == 0) {
            mNoNotificationsView.setVisibility(VISIBLE);
            mSendNotificationButton.setVisibility(VISIBLE);
        }
    }

    /**
     * Posts a SendNotificationEvent to the AppBus.
     */
    @Optional
    @OnClick(R.id.view_main_sendnotificationbutton)
    public void onSendNotificationClicked() {
        mAppBus.postSendNotificationEvent();
    }

    /**
     * Inserts the Notification given in the NewNotificationEvent at the top of the list.
     * Will also adjust visibility of the placeholder views if they were visible.
     * <p/>
     * The Notification will animate into view, after which it is added to the dataset.
     *
     * @param event NewNotificationEvent the event containing the added Notification.
     */
    @Subscribe
    public void onNewNotification(@NotNull final NewNotificationEvent event) {
        if (mAdapter.getCount() == 0) {
            mNoNotificationsView.setVisibility(GONE);
            mSendNotificationButton.setVisibility(GONE);
        }

        Notification notification = event.getNotification();
        mListView.insert(0, notification);
    }

    /**
     * Unregisters this instance with the AppBus, so we don't leak anything.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAppBus.unregister(this);
    }

    /**
     * Saves the state of this view.
     *
     * @return a Parcelable containing the current state of the view.
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(SAVEDINSTANCESTATE_INSTANCE, super.onSaveInstanceState());
        bundle.putParcelable(SAVEDINSTANCESTATE_LISTVIEW, mListView.onSaveInstanceState());
        bundle.putParcelable(SAVEDINSTACESTATE_ANIMATIONADAPTER, mAnimationAdapter.onSaveInstanceState());

        return bundle;
    }

    /**
     * Restores the state of this view from given configuration.
     *
     * @param state the configuration to apply.
     */
    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        Parcelable superState = state;

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            superState = bundle.getParcelable(SAVEDINSTANCESTATE_INSTANCE);

            mListView.onRestoreInstanceState(bundle.getParcelable(SAVEDINSTANCESTATE_LISTVIEW));
            mAnimationAdapter.onRestoreInstanceState(bundle.getParcelable(SAVEDINSTACESTATE_ANIMATIONADAPTER));
        }

        super.onRestoreInstanceState(superState);
    }

    /**
     * An OnDismissCallback for dismissing Notifications through swiping.
     * Dismissed notifications are posted to the AppBus using a NotificationRemovedEvent.
     */
    private class MyOnDismissCallback implements OnDismissCallback {

        /**
         * Posts NotificationRemovedEvents for each of the positions, and shows the placeholder views
         * if no Notifications are left.
         */
        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                if (mAdapter.getCount() > position) {
                    mAppBus.postNotificationRemovedEvent(mAdapter.getItem(position));
                }
            }
            mAdapter.notifyDataSetChanged();

            if (mAdapter.getCount() == 0) {
                mNoNotificationsView.setVisibility(VISIBLE);
                mSendNotificationButton.setVisibility(VISIBLE);
            }
        }
    }
}
