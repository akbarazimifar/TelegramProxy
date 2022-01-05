package com.proxyfortelegram.powerful.proxy.fastproxy;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class Main extends Application {
    private boolean DEBUGGABLE = false;
    public static Context context;

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

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int flags = info.applicationInfo.flags;
            DEBUGGABLE = (0 != (flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            //
        }

        // Creating an extended library configuration.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("d63f30da-9549-4591-928d-9ff1434b41b6").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);

        FirebaseApp.initializeApp(getApplicationContext());
        RemoteConfig.getInstance().init();

    }
}
