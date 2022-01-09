package com.proxyfortelegram.powerful.proxy.fastproxy.adManager;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.RemoteConfig;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.listener.IDealAdListener;
import com.scottyab.rootbeer.RootBeer;


public class AppodealInterstitial implements LifecycleObserver {

    private Runnable runTaskAppodeal;
    private Handler handlerAppodeal;
    private IDealAdListener iDealAdListener;
    private Activity mActivity;
    private String APPODEAL_APP_ID;
    private boolean isFailedToLoadAppodealAd = false;
    private static final String TAG = "MyAdManager";
    public boolean isInBackground = false;

    public AppodealInterstitial(Activity activity, IDealAdListener iDealAdListener) {
        mActivity = activity;
        this.iDealAdListener = iDealAdListener;

        handlerAppodeal = new Handler();

        APPODEAL_APP_ID = RemoteConfig.getInstance().appodealAppId();

    }

    private void CancelTimerAppodeal() {
        if (runTaskAppodeal != null) {
            Log.i(TAG, "APPODEAL - Timer Canceled");
            handlerAppodeal.removeCallbacks(runTaskAppodeal);
            runTaskAppodeal.run();
        }
    }

    public void initializeAppodeal() {
        if (!Appodeal.isInitialized(Appodeal.INTERSTITIAL)) {
            Appodeal.initialize(mActivity, APPODEAL_APP_ID, Appodeal.INTERSTITIAL);
        }
    }

    private void callbacksAppodealAd(IDealAdListener iDealAdListener) {
        this.iDealAdListener = iDealAdListener;
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                CancelTimerAppodeal();
                Log.i(TAG, "APPODEAL - On Interstitial Loaded");
            }

            @Override
            public void onInterstitialFailedToLoad() {
                CancelTimerAppodeal();
                Log.i(TAG, "APPODEAL - On Interstitial Failed To Load");
                isFailedToLoadAppodealAd = true;
            }

            @Override
            public void onInterstitialShown() {
                // Called when interstitial is shown
                Log.i(TAG, "APPODEAL - On Interstitial Shown");
                iDealAdListener.onAdImpressionAppodeal();
            }

            @Override
            public void onInterstitialShowFailed() {
                // Called when interstitial show failed
                CancelTimerAppodeal();
                Log.i(TAG, "APPODEAL - On Interstitial Show Failed");
            }

            @Override
            public void onInterstitialClicked() {
                // Called when interstitial is clicked
                Log.i(TAG, "APPODEAL - On Interstitial Clicked");
                iDealAdListener.onAdClickedAppodeal();
            }

            @Override
            public void onInterstitialClosed() {
                iDealAdListener.onAdCloseAppodeal();
                Log.i(TAG, "APPODEAL - On Interstitial Closed");
            }

            @Override
            public void onInterstitialExpired() {
                // Called when interstitial is expired
                CancelTimerAppodeal();
                Log.i(TAG, "APPODEAL - On Interstitial expired");
            }
        });
            Appodeal.show(mActivity, Appodeal.INTERSTITIAL);
    }

    private void showAppodealAd(IDealAdListener iDealAdListener) {

        initializeAppodeal();

        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            iDealAdListener.onDismissDialogAppodeal();
            callbacksAppodealAd(iDealAdListener);
            Log.i(TAG, "APPODEAL - Request to Show if is loaded");
        } else {
            runTaskAppodeal = new Runnable() {
                @Override
                public void run() {
                    if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                        iDealAdListener.onDismissDialogAppodeal();
                        callbacksAppodealAd(iDealAdListener);
                        isFailedToLoadAppodealAd = false;
                    } else {
                        if (isFailedToLoadAppodealAd) {
                            iDealAdListener.onDismissDialogAppodeal();
                            iDealAdListener.onFailedAppodeal();
                            isFailedToLoadAppodealAd = false;
                        } else {
                            iDealAdListener.onDismissDialogAppodeal();
                            iDealAdListener.onTimeOutAppodeal();
                            Log.i(TAG, "APPODEAL - Time Out, Showing Appodeal canceled");
                        }
                    }
                    runTaskAppodeal = null;
                }
            };
            handlerAppodeal.postDelayed(runTaskAppodeal, 45000);
        }
    }

    public void requestInterstitialAd(IDealAdListener iDealAdListener) {
        RootBeer rootBeer = new RootBeer(mActivity);
        if (!rootBeer.isRooted()) {
            showAppodealAd(iDealAdListener);
        } else {
            iDealAdListener.onDeviceRootedAppodeal();
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
