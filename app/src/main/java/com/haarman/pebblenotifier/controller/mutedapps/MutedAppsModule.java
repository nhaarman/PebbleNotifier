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

import com.haarman.pebblenotifier.model.async.QueryMutedAppsTask;
import com.haarman.pebblenotifier.view.mutedapps.MutedAppAdapter;
import com.haarman.pebblenotifier.view.mutedapps.MutedAppsView;

import dagger.Module;

@Module(
        injects = {
                MutedAppsActivity.class,
                MutedAppsView.class,
                MutedAppAdapter.class,
                QueryMutedAppsTask.class
        },
        complete = false,
        library = true
)
public class MutedAppsModule {

}
