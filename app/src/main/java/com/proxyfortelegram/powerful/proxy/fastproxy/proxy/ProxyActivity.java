package com.proxyfortelegram.powerful.proxy.fastproxy.proxy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.proxyfortelegram.powerful.proxy.fastproxy.R;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.AdmobInterstitial;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.AppodealInterstitial;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.listener.IDealAdListener;
import com.proxyfortelegram.powerful.proxy.fastproxy.adManager.listener.IMobAdListener;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.IClickListner;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.PreferencesManager;
import com.proxyfortelegram.powerful.proxy.fastproxy.helpers.RemoteConfig;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProxyActivity extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private List<ListModel> dataList;
    private RecyclerView.Adapter adapter;
    private RecyclerView mList;
    private DividerItemDecoration dividerItemDecoration;
    private FrameLayout adContainerView;
    private AdView adView;
    private String TAG = "ListAdChecker";
    private String REQTAG = "CheckRequest";
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    public AdmobInterstitial admobManager;
    public AppodealInterstitial appodealManager;
    private IMobAdListener iMobAdListener;
    private IDealAdListener iDealAdListener;
    private ProgressDialog mainLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");

        initBackendLess();
        adMobBanner();

        admobManager = new AdmobInterstitial(this, iMobAdListener);
        appodealManager = new AppodealInterstitial(this, iDealAdListener);

        mainLoadingDialog = new ProgressDialog(ProxyActivity.this);
        mainLoadingDialog.setMessage("Please Wait...");
        mainLoadingDialog.setCancelable(false);

        mList = findViewById(R.id.main_list);
        dataList = new ArrayList<>();
        adapter = new ProxyAdapter(this, dataList, new IClickListner() {
            @Override
            public void Onclick(ListModel data) {
                OnClickWorker(data);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());
        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(adapter);

        getData();

//      Interstitial Ads
    }

    private void adMobBanner() {
        adContainerView = findViewById(R.id.ad_container);
        adView = new AdView(this);
        adView.setAdUnitId(RemoteConfig.getInstance().bannerUnitId());
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        loadBanner();
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
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

    private void getData() {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100).setOffset(0);
        queryBuilder.setSortBy("sortId AESC");

        Backendless.Data.of("ProServer").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> items) {

                if (items == null || items.isEmpty()) return;
                for (Map item : items) {
                    ListModel myServer = new ListModel("location", "publish", "link", "sortId");
                    myServer.location = ((String) item.get("location"));
                    myServer.publish = ((String) item.get("publish"));
                    myServer.link = ((String) item.get("link"));
                    myServer.sortId = ((Integer) item.get("sortId"));

                    dataList.add(myServer);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ProxyActivity.this, "دوباره تلاش کنید", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void OnClickWorker(ListModel data) {
        action(data);

        /*try {
            sharedPreferencesAds();
            if (sharedPreferences.getInt("InterstitialAd", 0) % 2 == 0) {
                showAd(data);
            } else {
            }
        } catch (ActivityNotFoundException notInstalled) {
            Toast.makeText(this, getResources().getString(R.string.installTel), Toast.LENGTH_SHORT).show();
        }*/
    }

    private void action(ListModel data) {
        Intent sendToTelegram = new Intent("android.intent.action.VIEW");
//      String url = data.getlink().replace("https://", "tg://");
        String url = data.getlink();
        sendToTelegram.setData(Uri.parse(url));
        startActivity(Intent.createChooser(sendToTelegram, "Set Proxy")
                .setFlags(FLAG_ACTIVITY_NEW_TASK));
    }

    private void initBackendLess() {
        PreferencesManager pm = PreferencesManager.newInstance(this);
        Backendless.setUrl(pm.getBackendUrl());
        Backendless.initApp(getApplicationContext(),
                "01B43E68-0688-4864-8634-1665D3C29E74",
                "5C92FFE6-A9AD-4CFD-9D5E-2BC3BDCA91DE");
    }

    /*private void showAd(ListModel data) {
        admobManager.requestInterstitialAd(RemoteConfig.getInstance().buttonInterstitialUnitId(), new IMobAdListener() {

            @Override
            public void onAdCloseAdMob() {
                action(data);
            }

            @Override
            public void onFailedAdMob() {
                showDealAd(data);
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
                action(data);
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
    }*/

    /*private void showDealAd(ListModel data) {
        appodealManager.requestInterstitialAd(new IDealAdListener() {
            @Override
            public void onAdCloseAppodeal() {
                action(data);
            }

            @Override
            public void onFailedAppodeal() {
                action(data);
            }

            @Override
            public void onAdImpressionAppodeal() {

            }

            @Override
            public void onAdClickedAppodeal() {

            }

            @Override
            public void onTimeOutAppodeal() {
                action(data);
            }

            @Override
            public void onDeviceRootedAppodeal() {
                action(data);
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
    }*/

    /*private void sharedPreferencesAds() {
        sharedPreferences = this.getSharedPreferences("InterstitialAd", Context.MODE_PRIVATE);
        int ads = sharedPreferences.getInt("InterstitialAd", 0) + 1;
        sharedPreferences.edit().putInt("InterstitialAd", ads).apply();
    }*/

}