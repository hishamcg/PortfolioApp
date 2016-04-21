package com.hisham.portfolio;

import com.hisham.portfolio.rest.RestClient;
import com.hisham.portfolio.utils.FontsOverride;
import com.hisham.portfolio.utils.NetworkStatus;
import com.hisham.portfolio.utils.SharedPref;
import com.hisham.portfolio.utils.ShowToast;
import com.orm.SugarApp;

/**
 * Created by hisham on 18/4/16.
 */
public class PortfolioApp extends SugarApp {
    @Override
    public void onCreate(){
        super.onCreate();

        SharedPref.initialize(this);
        NetworkStatus.initialize(this);
        ShowToast.initialize(this);
        RestClient.init(SharedPref.getStringValue("AUTH_TOKEN"));
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/robotoslab_light.ttf");
        //LibRestClient.init("kM21kxymCjtGXVZxTxzG", Config.LIB_URL);
    }


}
