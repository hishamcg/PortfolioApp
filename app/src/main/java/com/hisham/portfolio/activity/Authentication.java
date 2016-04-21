package com.hisham.portfolio.activity;

import android.app.Activity;
import android.content.Intent;

import com.hisham.portfolio.rest.RestClient;
import com.hisham.portfolio.utils.SharedPref;
import com.hisham.portfolio.utils.ShowToast;

public class Authentication {
    public static final int REQUEST_CODE = 18709;
    public static final int RESULT_CODE = 99876;

    public static boolean getAuthResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == RESULT_CODE && data.getStringExtra("AUTH_TOKEN") != null){

            RestClient.setAuth_token(data.getStringExtra("AUTH_TOKEN"));

            SharedPref.setStringValue("AUTH_TOKEN", data.getStringExtra("AUTH_TOKEN"));
            SharedPref.setStringValue("EMAIL", data.getStringExtra("EMAIL"));
            SharedPref.setStringValue("USER_ID", data.getStringExtra("USER_ID"));
            SharedPref.setStringValue("PHOTO_URL", data.getStringExtra("PHOTO_URL"));

            return true;
        }else{
            return false;
        }
    }

    public static void start(Activity activity, String baseUrl){
        Intent in = new Intent(activity, Navigate.class);
        in.putExtra("baseUrl",baseUrl);
        activity.startActivityForResult(in, REQUEST_CODE);
    }
}
