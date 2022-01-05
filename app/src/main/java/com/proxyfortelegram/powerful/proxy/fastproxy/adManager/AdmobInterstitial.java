package com.proxyfortelegram.powerful.proxy.fastproxy.adManager;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.scottyab.rootbeer.RootBeer;
import com.yandex.metrica.YandexMetrica;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class AdmobInterstitial implements LifecycleObserver {
    public static InterstitialAd mInterstitialAd;
    private IMobAdListener iMobAdListener;
    private int adRequestChecker;
    private Activity mActivity;
    private static final String REQTAG = "AdRequestChecker";
    private static final String TAG = "MyAdManager";
    private boolean isInBackground = false;

    public AdmobInterstitial(Activity activity, IMobAdListener iMobAdListener) {
        mActivity = activity;
        this.iMobAdListener = iMobAdListener;
    }

    private void loadInterstitialAdMob(IMobAdListener iMobAdListener, String adUnit) {
        this.iMobAdListener = iMobAdListener;
        AdRequest adRequest = new AdRequest.Builder().build();
        iMobAdListener.onAdRequestAdMob();

        adRequestChecker++;
        Log.i(REQTAG, "Interstitial sent " + adRequestChecker + " Request");

        InterstitialAd.load(mActivity, adUnit, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "ADMOB - on Ad Loaded");
                        showInterstitialAdMob(iMobAdListener);
                        iMobAdListener.onDismissDialogAdMob();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        iMobAdListener.onFailedAdMob();
                        mInterstitialAd = null;

                        Log.i(TAG, "ADMOB - on Ad Failed To Load " + "Error is " + loadAdError.getMessage());

                        try{
                            String errorMessage = loadAdError.getMessage();
                            Map<String, Object> eventParameters = new HashMap<String, Object>();
                            eventParameters.put(errorMessage, "");
                            YandexMetrica.reportEvent("Error Message", eventParameters);
                        } catch (Exception e){
                            //nothing
                        }
                    }
                });
    }

    public void setCallbacks() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                iMobAdListener.onAdClickedAdMob();
                Log.i(TAG, "ADMOB - on Ad Clicked");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                iMobAdListener.onFailedAdMob();
                Log.i(TAG, "ADMOB - on Ad Failed To Show Full Screen Content");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.i(TAG, "ADMOB - on Ad Showed Full Screen Content");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                iMobAdListener.onAdCloseAdMob();
                mInterstitialAd = null;
                Log.i(TAG, "ADMOB - on Ad Dismissed Full Screen Content");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.i(TAG, "ADMOB - on Ad Impression");
                iMobAdListener.onAdImpressionAdmob();
            }
        });
    }

    private void showInterstitialAdMob(IMobAdListener iMobAdListener) {
        this.iMobAdListener = iMobAdListener;
        if (mInterstitialAd != null) {
            iMobAdListener.onDismissDialogAdMob();
            setCallbacks();
            if (!isInBackground || this.mActivity.isFinishing() || this.mActivity.isDestroyed()) {
                mInterstitialAd.show(mActivity);
            }
        }
    }

    public void requestInterstitialAd(String adUnit, IMobAdListener iMobAdListener) {
        RootBeer rootBeer = new RootBeer(mActivity);
        if (!rootBeer.isRooted()) {
            iMobAdListener.onShowDialogAdMob();
            if (mInterstitialAd == null) {
                loadInterstitialAdMob(iMobAdListener, adUnit);
            } else {
                showInterstitialAdMob(iMobAdListener);
            }
        } else {
            iMobAdListener.onDeviceRootedAdMob();
            Log.e(TAG, "DEVICE IS ROOTED");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.isInBackground = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.isInBackground = true;
    }

}
