package com.hurhco.telegramPro;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String BACKEND_URL = "BACKEND_URL" ;

    private static SharedPreferences sharedPref;

    public static PreferencesManager newInstance(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager();
        if (sharedPref == null) {
            sharedPref = context.getSharedPreferences("APP", Context.MODE_PRIVATE);;
        }
        return preferencesManager;
    }

    public String getBackendUrl(){
        return sharedPref.getString(BACKEND_URL, "https://broad-unit-4e5d.khorshidco.workers.dev");
    }

    public void setBackendUrl(String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BACKEND_URL, value);
        editor.apply();
    }

}

