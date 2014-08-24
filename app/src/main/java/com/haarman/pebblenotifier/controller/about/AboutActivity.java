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

package com.haarman.pebblenotifier.controller.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.haarman.pebblenotifier.R;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.entity.Library;
import com.tundem.aboutlibraries.ui.LibsFragment;

import java.io.Serializable;
import java.util.Comparator;

public class AboutActivity extends Activity {

    private static final String[] LIBRARIES = {"butterknife", "dagger", "joda-time", "listviewanimations", "ormlite", "otto", "stan"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putStringArray(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
        bundle.putStringArray(Libs.BUNDLE_LIBS, LIBRARIES);
        bundle.putBoolean(Libs.BUNDLE_VERSION, true);
        bundle.putBoolean(Libs.BUNDLE_LICENSE, true);
        bundle.putBoolean(Libs.BUNDLE_LICENSE_DIALOG, true);

        LibsFragment fragment = new LibsFragment();
        fragment.setLibraryComparator(new LibraryComparator());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    private static class LibraryComparator implements Comparator<Library>, Serializable {

        @Override
        public int compare(final Library lhs, final Library rhs) {
            if (lhs.getDefinedName().equals("pebblenotifier")) {
                return -1;
            } else if (rhs.getDefinedName().equals("pebblenotifier")) {
                return 1;
            } else {
                return lhs.getLibraryName().compareTo(rhs.getLibraryName());
            }
        }
    }
}
