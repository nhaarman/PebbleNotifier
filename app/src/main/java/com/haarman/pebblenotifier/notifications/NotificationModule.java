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

package com.haarman.pebblenotifier.notifications;

import com.haarman.pebblenotifier.model.ormlite.OrmLiteModule;

import dagger.Module;

/**
 * The notification Module.
 * <p/>
 * This module only includes the OrmLiteModule, and makes it available to the NotificationListenerService.
 */
@Module(
        includes = {
                OrmLiteModule.class,
        },
        injects = {
                MyNotificationListenerService.class
        },
        library = true,
        complete = false
)
public class NotificationModule {

}
