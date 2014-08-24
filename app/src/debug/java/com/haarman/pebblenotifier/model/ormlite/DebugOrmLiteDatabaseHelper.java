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

package com.haarman.pebblenotifier.model.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.haarman.pebblenotifier.model.App;
import com.haarman.pebblenotifier.model.Notification;
import com.haarman.pebblenotifier.util.ForApplication;
import com.haarman.pebblenotifier.util.Injector;
import com.j256.ormlite.support.ConnectionSource;

import javax.inject.Inject;

@SuppressWarnings("rawtypes")
public class DebugOrmLiteDatabaseHelper extends OrmLiteDatabaseHelper {

    @Inject @ForApplication
    protected Context mContext;

    @Inject
    public DebugOrmLiteDatabaseHelper(@ForApplication final Context context) {
        super(context);
    }

    @Override
    public void onCreate(final SQLiteDatabase database, final ConnectionSource connectionSource) {
        super.onCreate(database, connectionSource);
        Injector injector = Injector.from(mContext);

        App app = new OrmLiteApp();
        injector.inject(app);
        app.setPackageName(getClass().getPackage().getName());
        app.create();

        for (int i = 0; i < 20; i++) {
            Notification notification = new OrmLiteNotification();
            injector.inject(notification);
            notification.setText("Notification no.: " + i);
            notification.setTitle("TITLE!");
            notification.setApp(app);
            notification.create();
        }

        for (int i = 0; i < 20; i++) {
            app = new OrmLiteApp();
            injector.inject(app);
            app.setPackageName("packagename" + i);
            app.setName("This is app " + i);
            app.setMuted(true);
            app.create();
        }
    }
}
