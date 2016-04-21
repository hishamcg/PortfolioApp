package com.hisham.portfolio.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;


import com.hisham.portfolio.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkStatus {
	private static Context context;

    public static void initialize(Context mContext){
        context = mContext;
    }
	public static boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

    public static boolean isGpsAvailable(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void showGPSSettingsAlert(final Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.show();
    }

    public static Dialog getGPSSettingsAlert(final Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        alertDialog.setIcon(R.mipmap.ic_launcher);
        return alertDialog.create();
    }

    public static void showWifiSettingsAlert(final Activity activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Internet Settings");
        alert.setMessage("Hi this application requires\ninternet connection");
        alert.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        // Set the Icon for the Dialog
        alert.setIcon(R.mipmap.ic_launcher);
        alert.show();
    }

    public static Dialog getWifiSettingsAlert(final Activity activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Internet Settings");
        alert.setMessage("Hi this application requires\ninternet connection");
        alert.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        // Set the Icon for the Dialog
        alert.setIcon(R.mipmap.ic_launcher);
        return alert.create();
    }

    public void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }
}
