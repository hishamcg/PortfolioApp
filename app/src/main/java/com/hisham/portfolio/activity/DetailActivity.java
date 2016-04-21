package com.hisham.portfolio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hisham.portfolio.R;
import com.hisham.portfolio.model.SubDetailModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class DetailActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    TextView ticker_detail,price,currency,change;
    LinearLayout detail_container;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(intent.getStringExtra("NAME"));

        ticker_detail = (TextView) findViewById(R.id.ticker_detail);
        price = (TextView) findViewById(R.id.price);
        currency = (TextView) findViewById(R.id.currency);
        change = (TextView) findViewById(R.id.change);
        detail_container = (LinearLayout)findViewById(R.id.detail_container);

        mChart = (LineChart) findViewById(R.id.chart1);
        assert mChart != null;
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("");
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.TRANSPARENT);
        mChart.getAxisRight().setDrawLabels(false);

        //get data
        new asyncTask().execute(intent.getStringExtra("TICKER"));

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        mChart.centerViewToAnimated(e.getXIndex(), e.getVal(), mChart.getData().getDataSetByIndex(dataSetIndex).getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }

    private class asyncTask extends AsyncTask<String, Void, Stock>{
        protected Stock doInBackground(String... ticker) {
            try {
                return YahooFinance.get(ticker[0],true);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        protected void onPostExecute(Stock stock) {
            if (stock != null) {
                ticker_detail.setText(stock.getStockExchange()+": "+stock.getSymbol());
                price.setText(String.valueOf(stock.getQuote().getPrice()));
                currency.setText(stock.getCurrency());
                if(stock.getQuote().getChange().compareTo(BigDecimal.ZERO) == 1){
                    change.setTextColor(getApplicationContext().getResources().getColor(R.color.green));
                }else if(stock.getQuote().getChange().compareTo(BigDecimal.ZERO) == -1){
                    change.setTextColor(getApplicationContext().getResources().getColor(R.color.red));
                }

                change.setText(String.valueOf(stock.getQuote().getChange()+" " +
                        "("+stock.getQuote().getChangeInPercent()+"%)"));

                addDetails(stock);

                mChart.setVisibility(View.VISIBLE);
                updateGraph(stock);

            }
        }
    }

    private void addDetails(Stock stock){
        ArrayList<SubDetailModel> sub_data = new ArrayList<>();
        sub_data.add(new SubDetailModel().setKey1("High")
                .setVal1(String.valueOf(stock.getQuote().getDayHigh()))
                .setKey2("Low")
                .setVal2(String.valueOf(stock.getQuote().getDayLow())));

        sub_data.add(new SubDetailModel().setKey1("Open")
                .setVal1(String.valueOf(stock.getQuote().getOpen()))
                .setKey2("Mkt Cap")
                .setVal2(String.valueOf(stock.getStats().getMarketCap())));

        sub_data.add(new SubDetailModel().setKey1("P/E ratio")
                .setVal1(String.valueOf(stock.getStats().getEps()))
                .setKey2("Div yield")
                .setVal2(String.valueOf(stock.getDividend().getAnnualYield())));

        sub_data.add(new SubDetailModel().setKey1("Ask Size")
                .setVal1(String.valueOf(stock.getQuote().getAskSize()))
                .setKey2("Bid Size")
                .setVal2(String.valueOf(stock.getQuote().getBidSize())));

        LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(SubDetailModel sub:sub_data){
            View inf = inflater.inflate(R.layout.content_sub_detail,null,false);
            final TextView k1 = (TextView) inf.findViewById(R.id.key1);
            final TextView v1 = (TextView) inf.findViewById(R.id.val1);
            final TextView k2 = (TextView) inf.findViewById(R.id.key2);
            final TextView v2 = (TextView) inf.findViewById(R.id.val2);

            k1.setText(sub.getKey1());
            v1.setText(checkEmpty(sub.getVal1()));
            k2.setText(sub.getKey2());
            v2.setText(checkEmpty(sub.getVal2()));

            detail_container.addView(inf);
        }

    }

    private String checkEmpty(String t){
        return (t!=null&&!t.isEmpty()&&!t.equals("null"))?t:"-";
    }

    private void updateGraph(Stock stock){

        try {
            ArrayList<String> xVals = new ArrayList<>();
            ArrayList<Entry> yVals = new ArrayList<>();

            List<HistoricalQuote> quotes = stock.getHistory();
            Collections.reverse(quotes);
            int count = 0;
            SimpleDateFormat dateF = new SimpleDateFormat("dd/MMM/yy");
            for (HistoricalQuote quo:quotes){
                xVals.add(String.valueOf(dateF.format(quo.getDate().getTime())));
                //xVals.add((count) + "");
                yVals.add(new Entry(quo.getClose().floatValue(), count));
                count++;
            }

            LineDataSet set = new LineDataSet(yVals, stock.getSymbol());
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.getHoloBlue());
            set.setCircleColor(Color.WHITE);
            set.setLineWidth(2f);
            set.setCircleRadius(3f);
            set.setFillAlpha(85);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);
            set.setDrawFilled(true);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set);

            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            mChart.setData(data);
            mChart.animateX(1000);
            configure();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void configure(){
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(14f);
        l.setTextColor(Color.BLACK);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
    }
}
