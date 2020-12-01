/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.adhoc.adhocsdk.AdhocConfig;
import com.adhoc.adhocsdk.AdhocTracker;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;


public class FBReaderApplication extends ZLAndroidApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AdhocTracker.init(this, AdhocConfig.defaultConfig()
                .appKey("ADHOC_571a04e7-c241-4b53-ad2e-3f8845bf5fae")
                .enableDebugAssist(true)
                .reportImmediately());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
