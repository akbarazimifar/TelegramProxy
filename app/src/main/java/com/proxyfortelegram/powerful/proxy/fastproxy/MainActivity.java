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
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.IDealAdListener;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.IMobAdListener;
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
            //showAd();
            nextActivity();
        });

        guidebtn.setOnClickListener(v -> {
            FirstActivityEvent(0);
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        });

        sharebtn.setOnClickListener(v -> {
            FirstActivityEvent(1);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, " ");
            String sAux = getResources().getString(R.string.shareUs);
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
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE, this, MY_UPDATE_REQUEST_CODE);
                    FirstActivityEvent(3);
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
                                    // If an in-app update is already running, resume the update.
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
                // We can get the ReviewInfo object
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
            }

            @Override
            public void onFailedAdMob() {
                showDealAd();
            }

            @Override
            public void onAdImpressionAdmob() {

            }

            @Override
            public void onAdClickedAdMob() {

            }

            @Override
            public void onAdRequestAdMob() {

            }

            @Override
            public void onDeviceRootedAdMob() {
                nextActivity();
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
        appodealManager.requestInterstitialAd(new IDealAdListener() {
            @Override
            public void onAdCloseAppodeal() {
                nextActivity();
            }

            @Override
            public void onFailedAppodeal() {
                nextActivity();
            }

            @Override
            public void onAdImpressionAppodeal() {

            }

            @Override
            public void onAdClickedAppodeal() {

            }

            @Override
            public void onTimeOutAppodeal() {
                nextActivity();
            }

            @Override
            public void onDeviceRootedAppodeal() {
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
    }

    private void nextActivity() {
        Intent intent = new Intent(MainActivity.this, ProxyActivity.class);
        startActivity(intent);
    }

    private void InterstitialEvent(int Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (Status) {
            case 0:
                eventParameters.put("Interstitial  Button", "Ad Request Sent");
                break;
            case 1:
                eventParameters.put("Interstitial  Button", "Failed");
                break;
            case 2:
                eventParameters.put("Interstitial  Button", "Ad Failed To Load");
                break;
            case 3:
                eventParameters.put("Interstitial  Button", "Ad Show And Dismissed");
                break;
            case 4:
                eventParameters.put("Interstitial  Button", "Ad Failed To Show FullScreen");
                break;
        }
        YandexMetrica.reportEvent("AdMob", eventParameters);
    }

    private void FirstActivityEvent(int staus) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (staus) {
            case 0:
                eventParameters.put("How To Use Button", "Touched");
                break;
            case 1:
                eventParameters.put("Share Button", "Touched");
                break;
            case 2:
                eventParameters.put("Review Button", "Touched");
                break;
            case 3:
                eventParameters.put("In App Update", "Showed");
                break;
        }
        YandexMetrica.reportEvent(" Activity", eventParameters);
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