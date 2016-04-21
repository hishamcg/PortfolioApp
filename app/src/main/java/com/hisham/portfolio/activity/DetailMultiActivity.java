package com.hisham.portfolio.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.hisham.portfolio.model.StockModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class DetailMultiActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private LineChart mChart;
    private String[] choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_multi);
        TextView selected = (TextView) findViewById(R.id.selected);

        choice = getIntent().getStringArrayExtra("TICKER");
        selected.setText(ConvertStringArrayToString(getIntent().getStringArrayExtra("NAME")));

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

        new asyncTaskArray().execute(choice);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private class asyncTaskArray extends AsyncTask<String[], Void, Map<String, Stock>> {
        protected Map<String, Stock> doInBackground(String[]... ticker) {
            try {
                return YahooFinance.get(ticker[0],true);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        protected void onPostExecute(Map<String, Stock> stock) {
            if (stock != null) {
                mChart.setVisibility(View.VISIBLE);
                updateGraph(stock);

            }
        }
    }

    private void updateGraph(Map<String, Stock> stocks){

        try {
            ArrayList<String> xVals = new ArrayList<String>();
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            int max_arr_size = 0;
            //just incase
            String max_choice = choice[0];
            for (String symbol : choice) {
                if(stocks.get(symbol).getHistory().size() > max_arr_size){
                    max_arr_size = stocks.get(symbol).getHistory().size();
                    max_choice = symbol;
                }
                //Math.max(stocks.get(symbol).getHistory().size(),max_arr_size);
            }

            List<HistoricalQuote> qs = stocks.get(max_choice).getHistory();
            Collections.reverse(qs);
            SimpleDateFormat dateF = new SimpleDateFormat("dd/MMM/yy");
            for (HistoricalQuote quo:qs){
                xVals.add(String.valueOf(dateF.format(quo.getDate().getTime())));
            }

            // just make sure all data have the same size
            // fix on the max value and fill zero if no value

            for (String symbol : choice) {
                List<HistoricalQuote> quotes = stocks.get(symbol).getHistory();
                Collections.reverse(quotes);
                ArrayList<Entry> yVals = new ArrayList<Entry>();
                for(int i=0;i<max_arr_size;i++){
                    //setting zero for out of bound value
                    yVals.add(new Entry(quotes.get(i)!=null?quotes.get(i).getClose().floatValue():0, i));
                }

                Random rnd = new Random();
                int randColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                LineDataSet set = new LineDataSet(yVals, symbol);
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(randColor);
                set.setCircleColor(Color.WHITE);
                set.setLineWidth(2f);
                set.setCircleRadius(3f);
                set.setFillAlpha(85);
                set.setFillColor(randColor);
                set.setHighLightColor(Color.rgb(244, 117, 117));
                set.setDrawCircleHole(false);
                set.setDrawFilled(true);

                dataSets.add(set);
            }


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

    static String ConvertStringArrayToString(String[] array)
    {
        //
        // Concatenate all the elements into a StringBuilder.
        //
        StringBuilder builder = new StringBuilder();
        for (String value:array){
            builder.append(value);
            builder.append("\n");
        }
        return builder.toString();
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
