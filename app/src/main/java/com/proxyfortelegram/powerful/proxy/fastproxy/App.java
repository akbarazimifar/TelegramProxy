package com.proxyfortelegram.powerful.proxy.fastproxy;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.AppOpenManager;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.RemoteConfig;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class App extends Application {
    private boolean DEBUGGABLE = false;
    public static Context context;
    private static AppOpenManager appOpenManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (Exception e) {
            //
        }
    }

    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
        appOpenManager = new AppOpenManager(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int flags = info.applicationInfo.flags;
            DEBUGGABLE = (0 != (flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            //
        }

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("8dbe6375-4ed2-4f02-aa6a-6e0b9c841985").build();
        YandexMetrica.activate(getApplicationContext(), config);
        YandexMetrica.enableActivityAutoTracking(this);
        FirebaseApp.initializeApp(getApplicationContext());
        RemoteConfig.getInstance().init();

    }
}
