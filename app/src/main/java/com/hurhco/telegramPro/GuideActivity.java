package com.hurhco.telegramPro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;

public class GuideActivity extends LocalizationActivity {

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

        Typeface typeface_Heading1 = Typeface.createFromAsset(heading1.getContext().getAssets(), "font/appfontmed.ttf");
        heading1.setTypeface(typeface_Heading1);

        Typeface typeface_Heading2 = Typeface.createFromAsset(heading2.getContext().getAssets(), "font/appfontmed.ttf");
        heading2.setTypeface(typeface_Heading2);

        Typeface typeface_Heading3 = Typeface.createFromAsset(heading3.getContext().getAssets(), "font/appfontmed.ttf");
        heading3.setTypeface(typeface_Heading3);

        Typeface typeface_describtion1 = Typeface.createFromAsset(describtion2.getContext().getAssets(), "font/appfontlgt.ttf");
        describtion2.setTypeface(typeface_describtion1);

        Typeface typeface_describtion2 = Typeface.createFromAsset(describtion1.getContext().getAssets(), "font/appfontlgt.ttf");
        describtion1.setTypeface(typeface_describtion2);

        Typeface typeface_describtion3 = Typeface.createFromAsset(describtion3.getContext().getAssets(), "font/appfontlgt.ttf");
        describtion3.setTypeface(typeface_describtion3);
    }
}
