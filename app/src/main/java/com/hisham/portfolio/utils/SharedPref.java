package com.hisham.portfolio.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public static SharedPreferences sharedPreferences;

    public static void setStringValue(String key, String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String getStringValue(String key){
        return sharedPreferences.getString(key, "");
    }

    public static String getStringValue(String key, String dflt){
        return sharedPreferences.getString(key, dflt);
    }

    public static void setIntValue(String key, int value){
        sharedPreferences.edit().putInt(key,value).apply();
    }

    public static int getIntValue(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public static void setBooleanValue(String key, Boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();
    }

    public static Boolean getBooleanValue(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public static void initialize(Context context){
        sharedPreferences =context.getSharedPreferences("PREF", Context.MODE_PRIVATE);
    }

}
