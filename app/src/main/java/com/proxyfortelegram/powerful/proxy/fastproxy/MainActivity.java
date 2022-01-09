package com.proxyfortelegram.powerful.proxy.fastproxy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.AdmobInterstitial;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.AppodealInterstitial;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.listener.IDealAdListener;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.listener.IMobAdListener;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.RemoteConfig;
import com.proxyfortelegram.powerful.proxy.fastproxy.proxy.ProxyActivity;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageButton probtn;
    private ImageButton guidebtn;
    private ImageButton sharebtn;
    private ImageButton policybtn;
    private TextView guide;
    private TextView buttonText;
    private AppUpdateManager appUpdateManager;
    private static final int MY_UPDATE_REQUEST_CODE = 300;
    private String TAG = "FirstActivityAdChecker";
    private String REQTAG = "CheckRequest";
    public AdmobInterstitial admobManager;
    public AppodealInterstitial appodealManager;
    private IMobAdListener iMobAdListener;
    private IDealAdListener iDealAdListener;
    private ProgressDialog mainLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        init();
        initAdMob();
        update();
        Review();

    }

    private void init() {
        probtn = findViewById(R.id.pro_btn);
        guidebtn = findViewById(R.id.guide_btn);
        sharebtn = findViewById(R.id.share_btn);
        policybtn = findViewById(R.id.policy_btn);
        guide = findViewById(R.id.guidetxt);
        buttonText = findViewById(R.id.btn_txt);

        mainLoadingDialog = new ProgressDialog(MainActivity.this);
        mainLoadingDialog.setMessage("Please Wait...");
        mainLoadingDialog.setCancelable(false);

        admobManager = new AdmobInterstitial(this, iMobAdListener);
        appodealManager = new AppodealInterstitial(this, iDealAdListener);

        probtn.setOnClickListener(v -> {
            if (RemoteConfig.getInstance().buttonMobAdsRules()) {
                showAd();
            } else {
                nextActivity();
            }
        });

        guidebtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        });

        sharebtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, " ");
            String sAux = getResources().getString(R.string.appShareMessage);
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
            intent.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(intent, "choose one"));
        });


        policybtn.setOnClickListener(v -> {
            String url = getString(R.string.address_pri);
            Intent i1 = new Intent(Intent.ACTION_VIEW);
            i1.setData(Uri.parse(url));
            startActivity(i1);
        });

    }

    private void update() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE, this, MY_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
            }
        });
    }

    private void UpdateOnResume() {
        if (appUpdateManager != null)
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                AppUpdateType.IMMEDIATE,
                                                this,
                                                MY_UPDATE_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
    }

    public void requestReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                showReview(manager, reviewInfo);
            } else {
                // There was some problem, continue regardless of the result.
            }
        });
    }

    private void showReview(ReviewManager manager, ReviewInfo reviewInfo) {
        Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
        flow.addOnCompleteListener(task -> {
        });
    }

    private void Review() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        int openTime = sharedPreferences.getInt("firstTime", 0) + 1;
        sharedPreferences.edit().putInt("firstTime", openTime).apply();
        if (openTime >= 5) {
            requestReview();
        }
    }

    private void initAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private void showAd() {
        admobManager.requestInterstitialAd(RemoteConfig.getInstance().buttonInterstitialUnitId(), new IMobAdListener() {
            @Override
            public void onAdCloseAdMob() {
                nextActivity();
                InterstitialMobEvent(0);
            }

            @Override
            public void onFailedAdMob() {
                showDealAd();
                InterstitialMobEvent(1);
            }

            @Override
            public void onAdImpressionAdmob() {
                InterstitialMobEvent(2);
            }

            @Override
            public void onAdClickedAdMob() {
                InterstitialMobEvent(3);
            }

            @Override
            public void onAdRequestAdMob() {
                InterstitialMobEvent(4);
            }

            @Override
            public void onDeviceRootedAdMob() {
                nextActivity();
                InterstitialMobEvent(5);
            }

            @Override
            public void onShowDialogAdMob() {
                if (mainLoadingDialog != null && !mainLoadingDialog.isShowing()) {
                    mainLoadingDialog.show();
                }
            }

            @Override
            public void onDismissDialogAdMob() {
                if (mainLoadingDialog != null && mainLoadingDialog.isShowing()) {
                    mainLoadingDialog.dismiss();
                }
            }
        });
    }

    private void showDealAd() {
        if (RemoteConfig.getInstance().appodealAdRules()) {
            appodealManager.requestInterstitialAd(new IDealAdListener() {
                @Override
                public void onAdCloseAppodeal() {
                    InterstitialDealEvent(0);
                    nextActivity();
                }

                @Override
                public void onFailedAppodeal() {
                    InterstitialDealEvent(1);
                    nextActivity();
                }

                @Override
                public void onAdImpressionAppodeal() {
                    InterstitialDealEvent(2);
                }

                @Override
                public void onAdClickedAppodeal() {
                    InterstitialDealEvent(3);
                }

                @Override
                public void onTimeOutAppodeal() {
                    InterstitialDealEvent(4);
                    nextActivity();
                }

                @Override
                public void onDeviceRootedAppodeal() {
                    InterstitialDealEvent(5);
                    nextActivity();
                }

                @Override
                public void onShowDialogAppodeal() {
                    if (mainLoadingDialog != null && !mainLoadingDialog.isShowing()) {
                        mainLoadingDialog.show();
                    }
                }

                @Override
                public void onDismissDialogAppodeal() {
                    if (mainLoadingDialog != null && mainLoadingDialog.isShowing()) {
                        mainLoadingDialog.dismiss();
                    }
                }
            });
        }  else {
            nextActivity();
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(MainActivity.this, ProxyActivity.class);
        startActivity(intent);
    }

    private void InterstitialMobEvent(int Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();
        switch (Status) {
            case 0:
                eventParameters.put("Interstitial  Button", "on Ad Close");
                break;
            case 1:
                eventParameters.put("Interstitial  Button", "on Failed");
                break;
            case 2:
                eventParameters.put("Interstitial  Button", "on Ad Impression");
                break;
            case 3:
                eventParameters.put("Interstitial  Button", "on Ad Clicked");
                break;
            case 4:
                eventParameters.put("Interstitial  Button", "on Ad Request");
                break;
            case 5:
                eventParameters.put("Interstitial  Button", "on Device Rooted");
                break;
        }
        YandexMetrica.reportEvent("AdMob", eventParameters);
    }

    private void InterstitialDealEvent(int Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();
        switch (Status) {
            case 0:
                eventParameters.put("Interstitial  Button", "on Ad Close");
                break;
            case 1:
                eventParameters.put("Interstitial  Button", "on Failed");
                break;
            case 2:
                eventParameters.put("Interstitial  Button", "on Ad Impression");
                break;
            case 3:
                eventParameters.put("Interstitial  Button", "on Ad Clicked");
                break;
            case 4:
                eventParameters.put("Interstitial  Button", "on Ad Timeout");
                break;
            case 5:
                eventParameters.put("Interstitial  Button", "on Device Rooted");
                break;
        }
        YandexMetrica.reportEvent("Appodeal", eventParameters);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        UpdateOnResume();
        super.onResume();
    }
}