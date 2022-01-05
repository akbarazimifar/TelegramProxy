package com.proxyfortelegram.powerful.proxy.fastproxy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class TutorialActivity extends AppCompatActivity {

    private TextView heading1;
    private TextView heading2;
    private TextView heading3;
    private TextView describtion1;
    private TextView describtion2;
    private TextView describtion3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        heading1 = findViewById(R.id.slide_heading1);
        heading2 = findViewById(R.id.slide_heading2);
        heading3 = findViewById(R.id.slide_heading3);
        describtion1 = findViewById(R.id.slide_desc1);
        describtion2 = findViewById(R.id.slide_desc2);
        describtion3 = findViewById(R.id.slide_desc3);

    }
}
