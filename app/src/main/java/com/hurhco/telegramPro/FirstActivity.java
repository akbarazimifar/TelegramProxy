package com.hurhco.telegramPro;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends LocalizationActivity {
    private ImageButton probtn;
    private ImageButton guidebtn;
    private ImageButton sharebtn;
    private ImageButton policybtn;
    private ImageButton ratebtn;
    private TextView guide;
    private TextView lang;
    private TextView buttonText;
    private InterstitialAd mInterstitialAd;
    private ImageButton changeLanguage;
    private FrameLayout adContainerView;
    private AdView adView;
    private ProgressDialog progressDialog;
    private AppUpdateManager appUpdateManager;
    private static final int MY_UPDATE_REQUEST_CODE = 300;
    private String TAG = "FirstActivityAdChecker";
    private String REQTAG = "CheckRequest";
    private int InterstitialRequest;
    private int BannerRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        update();
        Review();

        //        Banner Ads
        adContainerView = findViewById(R.id.ad_container);
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.Eagle_Banner));
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        loadBanner();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.wait));
        progressDialog.setCancelable(false);

        probtn = findViewById(R.id.pro_btn);
        guidebtn = findViewById(R.id.guide_btn);
        sharebtn = findViewById(R.id.share_btn);
        policybtn = findViewById(R.id.policy_btn);
        ratebtn = findViewById(R.id.rate_btn);
        guide = findViewById(R.id.guidetxt);
        lang = findViewById(R.id.lang_txt);
        buttonText = findViewById(R.id.btn_txt);
        changeLanguage = findViewById(R.id.lan_btn);

        Typeface typefaceguide = Typeface.createFromAsset(guide.getContext().getAssets(), "font/appfontmed.ttf");
        guide.setTypeface(typefaceguide);
        Typeface typefaceButtonText = Typeface.createFromAsset(buttonText.getContext().getAssets(), "font/appfontmed.ttf");
        lang.setTypeface(typefaceButtonText);
        Typeface typefaceLangtext = Typeface.createFromAsset(lang.getContext().getAssets(), "font/appfontmed.ttf");
        lang.setTypeface(typefaceLangtext);
        Typeface typefacebtn = Typeface.createFromAsset(buttonText.getContext().getAssets(), "font/appfontmed.ttf");
        buttonText.setTypeface(typefacebtn);

        probtn.setOnClickListener(v -> {
            initInterstitialAd();
            showAd();
        });

        guidebtn.setOnClickListener(v -> {
            FirstActivityEvent(0);
            startActivity(new Intent(FirstActivity.this, GuideActivity.class));
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

        ratebtn.setOnClickListener(v -> {
            FirstActivityEvent(2);
            rateMe();
        });

        changeLanguage.setOnClickListener(v -> LanguageDialog());

    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();

        BannerRequest++;
        Log.i(REQTAG, "Banner in First Activity sent " + BannerRequest + " Request");

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void LanguageDialog() {

        final Dialog languageDialog;
        languageDialog = new Dialog(this, R.style.AppDialog);
        LayoutInflater layoutInflaterStepTwo = this.getLayoutInflater();
        final View viewLanguage = layoutInflaterStepTwo.inflate(R.layout.dialog_language, null);
        languageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        languageDialog.setContentView(viewLanguage);
        languageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        languageDialog.setTitle(getResources().getString(R.string.ChooseLanguage));

        Button faButton = languageDialog.findViewById(R.id.fa_btn);
        Button enButton = languageDialog.findViewById(R.id.en_btn);
        TextView languageDes = languageDialog.findViewById(R.id.languag_txt);

        Typeface typefaceFaButton = Typeface.createFromAsset(faButton.getContext().getAssets(), "font/appfontmed.ttf");
        faButton.setTypeface(typefaceFaButton);
        Typeface typefaceEnButton = Typeface.createFromAsset(enButton.getContext().getAssets(), "font/appfontmed.ttf");
        enButton.setTypeface(typefaceEnButton);
        Typeface typefaceLanguageDes = Typeface.createFromAsset(languageDes.getContext().getAssets(), "font/appfontmed.ttf");
        languageDes.setTypeface(typefaceLanguageDes);

        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("fa");
                languageDialog.dismiss();
                RestartApplication();
            }
        });

        enButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");
                languageDialog.dismiss();
                RestartApplication();
            }
        });
        languageDialog.show();
    }

    private void RestartApplication() {
        Intent intent = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateOnResume();
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

    private void InitReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    //Toast.makeText(this, "I Am Here", Toast.LENGTH_SHORT).show();
                });
            } else {
                // There was some problem, continue regardless of the result.
            }
        });
    }

    private void Review() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        int openTime = sharedPreferences.getInt("firstTime", 0) + 1;
        sharedPreferences.edit().putInt("firstTime", openTime).apply();
        if (openTime >= 5) {
            InitReview();
        }
    }

    private void initInterstitialAd() {
        if (mInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialRequest++;
            Log.i(REQTAG, "Interstitial in First Activity sent " + InterstitialRequest + " Request");

            InterstitialEvent(0);
            InterstitialAd.load(this, getResources().getString(R.string.Eagle_Interstitial), adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.getMessage());
                    InterstitialEvent(2);
                    mInterstitialAd = null;
                }
            });
        }
    }

    private void interstitialCallBack() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.i("TAG", "The ad was dismissed.");
                InterstitialEvent(3);
                nextActivity();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.i("TAG", "The ad failed to show.");
                InterstitialEvent(4);
                nextActivity();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null;
                Log.i("TAG", "The ad was shown.");
            }
        });
    }

    private void showAd() {
        if (mInterstitialAd != null) {
            interstitialCallBack();
            mInterstitialAd.show(FirstActivity.this);
        } else {
            Log.i("TAG", "The interstitial ad wasn't ready yet.");
            progressDialog.show();
            Timer waitTimer = new Timer();
            waitTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (mInterstitialAd != null) {
                                interstitialCallBack();
                                mInterstitialAd.show(FirstActivity.this);
                            } else {
                                InterstitialEvent(1);
                                nextActivity();
                            }
                        }
                    });
                }
            }, 9500);
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(FirstActivity.this, ProxyActivity.class);
        startActivity(intent);
    }

    private void InterstitialEvent(int Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (Status) {
            case 0:
                eventParameters.put("Interstitial Eagle Button", "Ad Request Sent");
                break;
            case 1:
                eventParameters.put("Interstitial Eagle Button", "Failed");
                break;
            case 2:
                eventParameters.put("Interstitial Eagle Button", "Ad Failed To Load");
                break;
            case 3:
                eventParameters.put("Interstitial Eagle Button", "Ad Show And Dismissed");
                break;
            case 4:
                eventParameters.put("Interstitial Eagle Button", "Ad Failed To Show FullScreen");
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
        YandexMetrica.reportEvent("Eagle Activity", eventParameters);
    }

}