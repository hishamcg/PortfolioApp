package com.hisham.portfolio.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.google.gson.Gson;
import com.hisham.portfolio.R;
import com.hisham.portfolio.adapter.SearchAdapter;
import com.hisham.portfolio.model.PortModel;
import com.hisham.portfolio.model.Portfolio;
import com.hisham.portfolio.model.SearchArray;
import com.hisham.portfolio.model.SearchModel;
import com.hisham.portfolio.model.StockModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class AddStockActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private FormEditText paid,shares;//trade_date;
    private int year, month, day;
    private boolean isFromHome = true;
    private static final int Add_A = 1001;
    private SearchModel searchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);
        SearchArray array = new Gson().fromJson(loadJSONFromAsset(), SearchArray.class);

        final SearchAdapter adapter = new SearchAdapter(this,R.layout.content_search,array.getData());
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.search);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        paid = (FormEditText)findViewById(R.id.paid);
        shares = (FormEditText)findViewById(R.id.shares);
        //trade_date = (FormEditText)findViewById(R.id.trade_date);

        isFromHome = getIntent().getStringExtra("FROM").equals("HOME");
        if(isFromHome){
            findViewById(R.id.container).setVisibility(View.GONE);
        }
//        else{
//            calendar = Calendar.getInstance();
//            year = calendar.get(Calendar.YEAR);
//
//            month = calendar.get(Calendar.MONTH);
//            day = calendar.get(Calendar.DAY_OF_MONTH);
//            showDate(year, month+1, day);
//        }

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                searchModel = (SearchModel) adapter.getItem(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_stock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add && (isFromHome || paid.testValidity() && shares.testValidity()
                && Integer.parseInt(paid.getText().toString())>0 && Integer.parseInt(shares.getText().toString())>0)) {
            new AsyncTask<String, Void, Stock>() {
                protected Stock doInBackground(String... params) {
                    try {
                        return YahooFinance.get(params[0]);
                    } catch (IOException|ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                protected void onPostExecute(Stock stocks) {
                    if (isFromHome) { // single request
                        StockModel st = new StockModel(stocks.getSymbol(),
                                stocks.getName(),
                                stocks.getCurrency(),
                                stocks.getQuote().getPrice(),
                                stocks.getQuote().getChange());
                        st.save();
                    } else {
                        Portfolio portfolio = Portfolio.find(Portfolio.class, "name = ?", getIntent().getStringExtra("TAG")).get(0);
                        PortModel st = new PortModel(stocks.getSymbol(),
                                stocks.getName(),
                                stocks.getCurrency(),
                                stocks.getQuote().getPrice(),
                                stocks.getQuote().getChange(),
                                Integer.parseInt(shares.getText().toString()),
                                Integer.parseInt(paid.getText().toString()),
                                portfolio);
                        st.save();
                    }
                    finish();
                }
            }.execute(searchModel.getTicker());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("stock.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

//    @SuppressWarnings("deprecation")
//    public void setDate(View view) {
//        showDialog(999);
//        //Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        // TODO Auto-generated method stub
//        if (id == 999) {
//            return new DatePickerDialog(this, myDateListener, year, month, day);
//        }
//        return null;
//    }
//
//    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
//            // TODO Auto-generated method stub
//            // arg1 = year
//            // arg2 = month
//            // arg3 = day
//            showDate(arg1, arg2+1, arg3);
//        }
//    };

//    private void showDate(int year, int month, int day) {
//        trade_date.setText(new StringBuilder().append(day).append("/")
//                .append(month).append("/").append(year));
//    }
}
