package com.hurhco.telegramPro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;

public class Splash extends LocalizationActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, FirstActivity.class);
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }
        }, 2000);
    }
}