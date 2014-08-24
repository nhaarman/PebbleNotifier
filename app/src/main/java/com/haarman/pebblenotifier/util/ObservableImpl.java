/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.haarman.pebblenotifier.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Observable is used to notify a group of Observer<T> objects when a change
 * occurs. On creation, the set of observers is empty. After a change occurred,
 * the application can call the {@link #notifyObservers()} method. This will
 * cause the invocation of the {@code update()} method of all registered
 * Observers. The order of invocation is not specified. This implementation will
 * call the Observers in the order they registered. Subclasses are completely
 * free in what order they call the update methods.
 *
 * @see Observer<T>
 */
public class ObservableImpl<T> implements Observable<T> {

    @NotNull
    private final Collection<WeakReference<Observer<T>>> mObservers = new ArrayList<>();

    @NotNull
    private final Object mLock = new Object();

    private boolean mChanged;

    /**
     * Adds the specified observer to the list of observers. If it is already
     * registered, it is not added a second time.
     *
     * @param observer the Observer<T> to add.
     */
    @Override
    public void addObserver(@NotNull final Observer<T> observer) {
        synchronized (mLock) {
            mObservers.add(new WeakReference<>(observer));
        }
    }

    /**
     * Removes the specified observer from the list of observers. Passing null
     * won't do anything.
     *
     * @param observer the observer to remove.
     */
    @Override
    public void deleteObserver(@Nullable final Observer<T> observer) {
        synchronized (mLock) {
            for (Iterator<WeakReference<Observer<T>>> iterator = mObservers.iterator(); iterator.hasNext(); ) {
                if (iterator.next().equals(observer)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void setChanged() {
        mChanged = true;
    }

    @Override
    public boolean hasChanged() {
        return mChanged;
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every observer in the list of observers using null as the
     * argument. Afterwards, calls {@code clearChanged()}.
     * <p/>
     * Equivalent to calling {@code notifyObservers(null)}.
     */
    @Override
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every Observer<T> in the list of observers using the specified
     * argument. Afterwards calls {@code clearChanged()}.
     *
     * @param data the argument passed to {@code update()}.
     */
    @Override
    public void notifyObservers(@Nullable final T data) {
        if (!hasChanged()) {
            return;
        }

        mChanged = true;

        Collection<WeakReference<Observer<T>>> references;
        synchronized (mLock) {
            references = new ArrayList<>(mObservers);
        }
        for (Iterator<WeakReference<Observer<T>>> iterator = references.iterator(); iterator.hasNext(); ) {
            WeakReference<Observer<T>> reference = iterator.next();
            Observer<T> observer = reference.get();
            if (observer != null) {
                observer.update(this, data);
            } else {
                iterator.remove();
            }
        }
    }
}
