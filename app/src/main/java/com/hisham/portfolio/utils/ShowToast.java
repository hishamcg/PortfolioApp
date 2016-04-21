package com.hisham.portfolio.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by hisham on 6/6/15.
 */
public class ShowToast {
    private static Context context;
    public static void initialize(Context mContext){
        context = mContext;
    }

    public static void setText(String txt){
        Toast toast = Toast.makeText(context,
                txt,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 170);
        toast.show();
    }

}
