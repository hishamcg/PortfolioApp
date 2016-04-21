package com.hisham.portfolio.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisham.portfolio.Config;
import com.hisham.portfolio.R;
import com.hisham.portfolio.model.StockModel;
import com.hisham.portfolio.utils.SharedPref;
import com.hisham.portfolio.utils.ShowToast;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class MainActivity extends FragmentActivity {

    private ViewPager mPager;
    private int[] mResources = {
            R.drawable.i1,
            R.drawable.i2,
            R.drawable.i3
    };
    private String[] titles = {
            "Explore",
            "Invest",
            "Repeat"
    };

    private String[] contents = {
            "Understand and evaluate different portfolio",
            "Invest along our expertise",
            "Lets get the flow going"
    };

    private int[] mBack_image = {
            R.drawable.p3,
            R.drawable.p1,
            R.drawable.p2
    };

    private int[] overlay_color = {
            0x70000000,
            0x70000000,
            0x60000000
    };

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String authToken = SharedPref.getStringValue("AUTH_TOKEN");
        String name = SharedPref.getStringValue("NAME");
        //TODO remove this not
        if (authToken.isEmpty()) {
            CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setOffscreenPageLimit(3);
            mPager.setPageTransformer(true, new FadeTransform());
            mPager.setAdapter(mCustomPagerAdapter);
            CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(mPager);
            Button button = (Button)findViewById(R.id.button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Authentication.start(MainActivity.this, Config.BASE_URL);
                    startActivity(new Intent(getApplicationContext(),Navigate.class));
                    finish();
                }
            });
            UpdateStockFromYahoo();
        }else{
            startActivity(new Intent(getApplicationContext(),Navigate.class));
            finish();
        }
    }
//
//    private void startHome(Class cls, Boolean firstTime) {
//        //GCMRegisterUtils.registerGCM(MainActivity.this, Config.GOOGLE_PROJECT_ID, Config.GCM_URL,
//        //                                              SharedPref.getStringValue("AUTH_TOKEN",""));
//        Intent in = new Intent(MainActivity.this, cls);
//        in.putExtra("firstTime",firstTime);
//        startActivity(in);
//        finish();
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(Authentication.getAuthResult(requestCode, resultCode, data)){
//            startHome(Navigate.class,true);
//        }else{
//            ShowToast.setText("authentication failed");
//        }
//    }

    private void UpdateStockFromYahoo(){

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    String[] symbols = new String[]{"GOOG","YHOO","AAPL"};
                    Map<String, Stock> stocks = YahooFinance.get(symbols); // single request

                    for (String symbol : symbols) {
                        StockModel st = new StockModel(stocks.get(symbol).getSymbol(),
                                stocks.get(symbol).getName(),
                                stocks.get(symbol).getCurrency(),
                                stocks.get(symbol).getQuote().getPrice(),
                                stocks.get(symbol).getQuote().getChange());
                        st.save();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.intro_layout, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            ImageView imageBg = (ImageView) itemView.findViewById(R.id.image_bg);
            View view_screen = itemView.findViewById(R.id.view_screen);
            TextView content = (TextView)itemView.findViewById(R.id.content);
            TextView title = (TextView)itemView.findViewById(R.id.title);
            view_screen.setBackgroundColor(overlay_color[position]);
            imageView.setImageResource(mResources[position]);
            content.setText(contents[position]);
            title.setText(titles[position]);
            imageBg.setImageResource(mBack_image[position]);
            view_screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int item = mPager.getCurrentItem()!=2?mPager.getCurrentItem()+1:2;
                    mPager.setCurrentItem(item);
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    public class FadeTransform implements ViewPager.PageTransformer {

        public void transformPage(View view, float position) {
            View imageBg = view.findViewById(R.id.image_bg);
            View title = view.findViewById(R.id.title);
            View imageView = view.findViewById(R.id.imageView);
            float mov = view.getWidth() * -position;

            if(imageBg != null)
            if (position > -1 && position <= 0) { // [-1,0]
                // This page is moving out to the left
                imageBg.setTranslationX(mov * 2/3);
                title.setTranslationX(mov * -8/5);
                imageView.setTranslationX(mov);
            } else if (position > 0 && position <= 1) { // (0,1]
                // This page is moving in from the right
                imageBg.setTranslationX(mov * 2/3);
                title.setTranslationX(mov * -8/5);
                imageView.setTranslationX(mov);
            }
        }
    }

}