package com.hisham.portfolio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hisham.portfolio.R;
import com.hisham.portfolio.model.PortModel;
import com.hisham.portfolio.model.Portfolio;
import com.hisham.portfolio.utils.ShowToast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class PortfolioDetail extends AppCompatActivity {
    private PortAdapter adapter;
    private List<PortModel> portModels;
    private FloatingActionButton fab;
    private static final int HOME_A= 1001;
    private Portfolio portfolio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio_detail);
        final ListView listView = (ListView) findViewById(R.id.listview);
        portfolio = Portfolio.find(Portfolio.class, "name = ?", getIntent().getStringExtra("NAME")).get(0);
        portModels = new ArrayList<>();
        portModels.addAll(portfolio.getAllStocks());
        adapter = new PortAdapter(this, portModels);
        assert listView != null;
        listView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), AddStockActivity.class);
                in.putExtra("FROM", "PORT")
                    .putExtra("TAG",getIntent().getStringExtra("NAME"));
                //startActivity(in);
                startActivity(in);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("NAME", portModels.get(position).getName())
                        .putExtra("TICKER", portModels.get(position).getTicker());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh && adapter != null) {
            UpdateStockFromYahoo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        UpdateStockFromYahoo();
    }

    private void UpdateStockFromYahoo(){
        final String[] symbols = portfolio.getTickerArray();
        new AsyncTask<Void, Void, Map<String, Stock>>() {
            protected Map<String, Stock> doInBackground(Void... params) {
                try {
                    if(symbols != null && symbols.length > 0) {
                        return YahooFinance.get(symbols); // single request
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute( Map<String, Stock> stocks) {

                for (String symbol : symbols) {
                    //taking data from old place
                    PortModel tmp = portfolio.getStockWith(symbol);
                    PortModel st = new PortModel(stocks.get(symbol).getSymbol(),
                            stocks.get(symbol).getName(),
                            stocks.get(symbol).getCurrency(),
                            stocks.get(symbol).getQuote().getPrice(),
                            stocks.get(symbol).getQuote().getChange(),
                            tmp.getShares(),
                            tmp.getPaid_price(),
                            portfolio);
                    st.save();
                }
                portModels.clear();
                portModels.addAll(portfolio.getAllStocks());
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public class PortAdapter extends BaseAdapter {
        private Activity activity;
        private List<PortModel> data;
        private LayoutInflater inflater;
        private boolean refresh = true;
        int red,green;
        public PortAdapter(Activity activity, List<PortModel> data){
            this.activity = activity;
            this.data = data;
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.red = activity.getResources().getColor(R.color.red);
            this.green = activity.getResources().getColor(R.color.green);
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView ticker;
            TextView share;
            TextView price;
            TextView total_paid;
            TextView daily_total;
            TextView total;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            final PortModel stock = data.get(position);

            final ViewHolder viewHolder;
            if(rowView==null) {
                viewHolder = new ViewHolder();
                rowView = inflater.inflate(R.layout.listview_item_port, parent, false);
                viewHolder.ticker = (TextView) rowView.findViewById(R.id.ticker);
                viewHolder.share = (TextView) rowView.findViewById(R.id.shares);
                viewHolder.price = (TextView) rowView.findViewById(R.id.price);
                viewHolder.total_paid = (TextView) rowView.findViewById(R.id.total_paid);
                viewHolder.daily_total = (TextView) rowView.findViewById(R.id.daily_total);
                viewHolder.total = (TextView) rowView.findViewById(R.id.total);
                rowView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) rowView.getTag();
            }

            if(stock != null){
                viewHolder.ticker.setText(stock.getTicker());
                viewHolder.share.setText(String.valueOf(stock.getShares()));
                viewHolder.price.setText(String.valueOf(stock.getPrice().floatValue()*stock.getShares()));
                viewHolder.total_paid.setText(String.valueOf(stock.getShares()*stock.getPaid_price()));
                Float daily = stock.getChange().floatValue()*stock.getShares();
                if(daily>0){
                    viewHolder.daily_total.setTextColor(green);
                    viewHolder.daily_total.setText("+"+String.valueOf(daily));
                }else{
                    viewHolder.daily_total.setTextColor(red);
                    viewHolder.daily_total.setText(String.valueOf(daily));
                }

                Float gain = (stock.getPrice().floatValue() - stock.getPaid_price())*stock.getShares();
                if(gain > 0){
                    viewHolder.total.setTextColor(green);
                    viewHolder.total.setText("+"+String.valueOf(gain));
                }else{
                    viewHolder.total.setTextColor(red);
                    viewHolder.total.setText(String.valueOf(gain));
                }


            }
            return rowView;
        }
    }
}
