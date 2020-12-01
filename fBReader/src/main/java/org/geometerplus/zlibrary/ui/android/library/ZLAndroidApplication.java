/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.library;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.dyg.android.reader.BuildConfig;
import com.sspsdk.RYSDK;
import com.sspsdk.databean.ExpInitParams;
import com.tencent.bugly.Bugly;

import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;

import org.geometerplus.android.fbreader.config.ConfigShadow;

public abstract class ZLAndroidApplication extends Application {
    private ZLAndroidLibrary myLibrary;
    private ConfigShadow myConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        // this is a workaround for strange issue on some devices:
        //    NoClassDefFoundError for android.os.AsyncTask
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable t) {
        }
        myConfig = new ConfigShadow(this);
        new ZLAndroidImageManager();
        myLibrary = new ZLAndroidLibrary(this);
        Bugly.init(getApplicationContext(), "ffe0cb6f8c", false);

        initManServiceFromAli();


        ExpInitParams expInitParams = new ExpInitParams.Builder()
                .channelId("DEBUG") // 可选
                .mediaUid("reyun_yingwenyuanzhu")// 可选
                .build();
        RYSDK.init(this, "b1a0fa5b", expInitParams);
        // 正式环境请务必设置为 false 或者 不设置值 默认为false
        RYSDK.setDebugModel(false);
    }

    public final ZLAndroidLibrary library() {
        return myLibrary;
    }

    public void initManServiceFromAli() {
        MANService manService = MANServiceProvider.getService();
        // 打开调试日志，线上版本建议关闭
        // manService.getMANAnalytics().turnOnDebug();
        // 若需要关闭 SDK 的自动异常捕获功能可进行如下操作(如需关闭crash report，建议在init方法调用前关闭crash),详见文档5.4
        manService.getMANAnalytics().turnOffCrashReporter();
        // 设置渠道（用以标记该app的分发渠道名称），如果不关心可以不设置即不调用该接口，渠道设置将影响控制台【渠道分析】栏目的报表展现。如果文档3.3章节更能满足您渠道配置的需求，就不要调用此方法，按照3.3进行配置即可；1.1.6版本及之后的版本，请在init方法之前调用此方法设置channel.
        manService.getMANAnalytics().setChannel("home_dyg");
        // MAN初始化方法之一，从AndroidManifest.xml中获取appKey和appSecret初始化，若您采用上述 2.3中"统一接入的方式"，则使用当前init方法即可。
        manService.getMANAnalytics().init(this, getApplicationContext());
        // MAN另一初始化方法，手动指定appKey和appSecret
        // 若您采用上述2.3中"统一接入的方式"，则无需使用当前init方法。
        // String appKey = "******";
        // String appSecret = "******";
        // manService.getMANAnalytics().init(this, getApplicationContext(), appKey, appSecret);
        // 通过此接口关闭页面自动打点功能，详见文档4.2
//        manService.getMANAnalytics().turnOffAutoPageTrack();
        manService.getMANAnalytics().turnOnDebug();
        // 若AndroidManifest.xml 中的 android:versionName 不能满足需求，可在此指定
        // 若在上述两个地方均没有设置appversion，上报的字段默认为null
        manService.getMANAnalytics().setAppVersion(getVersionName(this));

//        Thread.setDefaultUncaughtExceptionHandler(
//                new org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(this)
//        );
    }

    /**
     * get App versionName
     *
     * @param context
     * @return
     */
    public String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
