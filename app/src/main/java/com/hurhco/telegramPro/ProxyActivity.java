package com.hurhco.telegramPro;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProxyActivity extends LocalizationActivity {

    private LinearLayoutManager linearLayoutManager;
    private List<ProList> dataList;
    private RecyclerView.Adapter adapter;
    private RecyclerView mList;
    private DividerItemDecoration dividerItemDecoration;
    private TextView hint;
    private FrameLayout adContainerView;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private String TAG = "ListAdChecker";
    private String REQTAG = "CheckRequest";
    private SharedPreferences sharedPreferences;
    private int InterstitialRequest;
    private int BannerRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");

        initBackendLess();

//        Banner Ads
        adContainerView = findViewById(R.id.ad_container);
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.List_Banner));
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        loadBanner();

        mList = findViewById(R.id.main_list);
        dataList = new ArrayList<>();
        adapter = new ProItemAdapter(this, dataList, new IClickListner() {
            @Override
            public void Onclick(ProList data) {
                OnClickWorker(data);
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);
        hint = (TextView) findViewById(R.id.hint_txt);
        Typeface typefacepublish = Typeface.createFromAsset(hint.getContext().getAssets(), "font/appfontlgt.ttf");
        hint.setTypeface(typefacepublish);

        getData();

//      Interstitial Ads
        initInterstitialAd();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100).setOffset(0);
        queryBuilder.setSortBy("sortId AESC");

        Backendless.Data.of("ProServer").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> items) {

                if (items == null || items.isEmpty()) return;
                for (Map item : items) {
                    ProList myServer = new ProList("location", "publish", "lipk", "imgc", "sortId");
                    myServer.location = ((String) item.get("location"));
                    myServer.publish = ((String) item.get("publish"));
                    myServer.lipk = ((String) item.get("lipk"));
                    myServer.imgc = ((String) item.get("imgc"));
                    myServer.sortId = ((Integer) item.get("sortId"));

                    dataList.add(myServer);
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ProxyActivity.this, "دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();

        BannerRequest++;
        Log.i (REQTAG, "Banner in First Activity sent " + BannerRequest + " Request");

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

    private void initInterstitialAd() {
        if (mInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialRequest++;
            Log.i (REQTAG, "Interstitial in List Activity sent " + InterstitialRequest + " Request");

            InterstitialEvent(0);

            Log.i(TAG, "onAdRequest");
            InterstitialAd.load(this, getResources().getString(R.string.List_Interstitial), adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i(TAG, "on Ad Failed To Load" + loadAdError.getMessage());
                    InterstitialEvent(2);
                    mInterstitialAd = null;
                }
            });
        }
    }

    private void interstitialCallBack(ProList data) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                InterstitialEvent(3);
                action(data);
                initInterstitialAd();
                Log.i(TAG, "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                InterstitialEvent(4);
                action(data);
                Log.i(TAG, "The ad failed to show full screen");

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

    private void showAd(ProList data) {
        if (mInterstitialAd != null) {
            interstitialCallBack(data);
            mInterstitialAd.show(ProxyActivity.this);
        } else {
            InterstitialEvent(1);
            action(data);
        }
    }

    private void sharedPreferencesAds() {
        sharedPreferences = this.getSharedPreferences("InterstitialAd", Context.MODE_PRIVATE);
        int ads = sharedPreferences.getInt("InterstitialAd", 0) + 1;
        sharedPreferences.edit().putInt("InterstitialAd", ads).apply();
    }

    private void OnClickWorker(ProList data) {

        try  {
            sharedPreferencesAds();
            if (sharedPreferences.getInt("InterstitialAd", 0) % 2 == 0) {
                showAd(data);
            } else {
                action(data);
            }
        } catch (ActivityNotFoundException notInstalled) {
            Toast.makeText(this, getResources().getString(R.string.installTel), Toast.LENGTH_SHORT).show();
        }
    }

    private void action(ProList data) {
        Intent sendToTelegram = new Intent("android.intent.action.VIEW");
//      String url = data.getlipk().replace("https://", "tg://");
        String url = data.getlipk();
        sendToTelegram.setData(Uri.parse(url));
        startActivity(Intent.createChooser(sendToTelegram, "Set Proxy")
                .setFlags(FLAG_ACTIVITY_NEW_TASK));
    }

    private void InterstitialEvent(int Status) {
        Map<String, Object> eventParameters = new HashMap<String, Object>();

        switch (Status) {
            case 0:
                eventParameters.put("Interstitial List Button", "Ad Request Sent");
                break;
            case 1:
                eventParameters.put("Interstitial List Button", "Failed");
                break;
            case 2:
                eventParameters.put("Interstitial List Button", "Ad Failed To Load");
                break;
            case 3:
                eventParameters.put("Interstitial List Button", "Ad Show And Dismissed");
                break;
            case 4:
                eventParameters.put("Interstitial List Button", "Ad Failed To Show FullScreen");
                break;
        }
        YandexMetrica.reportEvent("AdMob", eventParameters);
    }

    private void initBackendLess() {
        PreferencesManager pm = PreferencesManager.newInstance(this);
        Backendless.setUrl(pm.getBackendUrl());
        Backendless.initApp(getApplicationContext(),
                "01B43E68-0688-4864-8634-1665D3C29E74",
                "5C92FFE6-A9AD-4CFD-9D5E-2BC3BDCA91DE");
    }

}