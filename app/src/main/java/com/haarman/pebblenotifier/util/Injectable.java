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

package com.haarman.pebblenotifier.util;


// http://tech.pristine.io/another-take-on-mvc-in-android/

/**
 * An interface to specify whether a class can inject other classes.
 */
public interface Injectable {

    /**
     * Injects injectable members into given instance.
     *
     * @param t the instance to be injected into.
     *
     * @return the injected instance.
     */
    <T> T inject(T t);
}