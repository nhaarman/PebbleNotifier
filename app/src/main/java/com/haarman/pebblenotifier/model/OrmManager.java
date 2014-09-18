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

package com.haarman.pebblenotifier.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface OrmManager {

    <T> void create(T t);

    <T> void update(T t);

    <T> void refresh(T t);

    <T> void delete(T t);

    <T, V> T findById(Class<T> clzz, V id);

    <T> List<T> all(Class<T> clzz);

    List<App> getMutedApps();

    @Nullable
    Notification getLastNotification();
}
