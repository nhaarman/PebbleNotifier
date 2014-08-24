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

package com.haarman.pebblenotifier.model.async;

import com.haarman.pebblenotifier.model.Model;
import com.label305.stan.async.AsyncTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateTask<T extends Model> extends AsyncTask<T> {

    @NotNull
    private final T mT;

    public CreateTask(@NotNull final T t) {
        mT = t;
    }

    @Nullable
    @Override
    public T call()  {
        mT.create();
        return mT;
    }
}
