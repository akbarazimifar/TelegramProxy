package com.proxyfortelegram.powerful.proxy.fastproxy.adManager;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.RemoteConfig;
import com.yandex.metrica.YandexMetrica;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static boolean isShowingAd = false;
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    private long loadTime = 0;
    private int AppOpenRequestCounter = 0;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final Application application;
    private static final String LOG_TAG = "AppOpenManager";
    private String TAG = "MyAdManager";
    private String REQTAG = "AdRequestChecker";

    private enum Admob {
        adRequest,
        adDismissed,
        adClicked,
        adImpression
    }

    /**
     * Constructor
     */
    public AppOpenManager(@NotNull Application application) {
        this.application = application;
        this.application.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.i(LOG_TAG, "onStart");
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
// Have unused ad, no need to fetch another.
// Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        AppOpenManager.this.appOpenAd = appOpenAd;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }
                };

        AdRequest request = getAdRequest();
        AdmobMetricaEvent(Admob.adRequest);
        AppOpenRequestCounter++;
        Log.i(REQTAG, "App Open sent " + AppOpenRequestCounter + " Request");

        AppOpenAd.load(
                application, RemoteConfig.getInstance().appOpenUnitId(), request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);

    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (RemoteConfig.getInstance().appOpenAdsRules()) {
            Log.i(TAG, "AppOpen - Remote Config allow to show app open ad");
            if (!isShowingAd && isAdAvailable()) {
                Log.i(TAG, "AppOpen - Will show ad.");
                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Set the reference to null so isAdAvailable() returns false.
                                Log.i(TAG, "AppOpen - On Ad Dismissed Full Screen Content");
                                AppOpenManager.this.appOpenAd = null;
                                isShowingAd = false;
                                fetchAd();
                                AdmobMetricaEvent(Admob.adDismissed);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.i(TAG, "AppOpen - On Ad Failed To Show Full Screen Content" + adError);
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                                AdmobMetricaEvent(Admob.adImpression);
                                Log.i(TAG, "AppOpen - On Ad Impression");
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                AdmobMetricaEvent(Admob.adClicked);
                                Log.i(TAG, "AppOpen - On Ad Clicked");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                isShowingAd = true;
                                Log.i(TAG, "AppOpen - On Ad Showed Full Screen Content");
                            }
                        };
                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);

            } else {
                Log.i(TAG, "AppOpen - Can not show ad");
                fetchAd();
            }
        } else {
            Log.i(TAG, "AppOpen - Remote Config not allow to show app open ad");
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    private void AdmobMetricaEvent(Admob Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (Status) {
            case adRequest:
                eventParameters.put("App Open Ad", "Ad Request Sent");
                break;
            case adDismissed:
                eventParameters.put("App Open Ad", "Ad Show And Dismissed");
                break;
            case adClicked:
                eventParameters.put("App Open Ad", "Ad Clicked");
                break;
            case adImpression:
                eventParameters.put("App Open Ad", "Ad Impression");
                break;
        }
        YandexMetrica.reportEvent("AdMob", eventParameters);
    }
}
