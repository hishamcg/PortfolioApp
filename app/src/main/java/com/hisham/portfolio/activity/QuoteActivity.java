package com.hisham.portfolio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hisham.portfolio.R;
import com.hisham.portfolio.model.StockModel;
import com.hisham.portfolio.utils.ShowToast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class QuoteActivity extends AppCompatActivity {
    private HomeAdapter adapter;
    private ArrayList<StockModel> stockModels = new ArrayList<>();
    private FloatingActionButton fab;
    private static final int HOME_A= 1001;
    private SparseBooleanArray mSelectedItemsIds = new SparseBooleanArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        final ListView listView = (ListView) findViewById(R.id.listview);

        stockModels.addAll(StockModel.listAll(StockModel.class));

        adapter = new HomeAdapter(this, stockModels);
        assert listView != null;
        listView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listView.getCheckedItemCount() <= 0) {
                    Intent in = new Intent(getApplicationContext(), AddStockActivity.class);
                    in.putExtra("FROM", "HOME");
                    startActivity(in);
                }else{
                    SparseBooleanArray selected = getSelectedIds();
                    String[] choice = new String[selected.size()];
                    String[] names = new String[selected.size()];
                    short size = (short)selected.size();
                    for (byte I = 0; I<size; I++){
                        if (selected.valueAt(I)) {
                            StockModel selectedItem = stockModels.get(selected.keyAt(I));
                            choice[I] = selectedItem.getTicker();
                            names[I] = selectedItem.getName();
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), DetailMultiActivity.class);
                    intent.putExtra("TICKER", choice);
                    intent.putExtra("NAME",names);
                    startActivity(intent);
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("NAME", stockModels.get(position).getName())
                        .putExtra("TICKER",stockModels.get(position).getTicker());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                listView.setItemChecked(position, true);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                // Capture ListView item click
                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode,
                                                          int position, long id, boolean checked) {
                        mode.setTitle(listView.getCheckedItemCount() + " Selected");
                        toggleSelection(position);
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if (item.getItemId() == R.id.delete){
                            SparseBooleanArray selected = getSelectedIds();
                            short size = (short)selected.size();
                            ArrayList<StockModel> stockDeleteList = new ArrayList<StockModel>();
                            for (byte I = 0; I<size; I++){
                                if (selected.valueAt(I)) {
                                    stockDeleteList.add(stockModels.get(selected.keyAt(I)));
                                    stockModels.get(selected.keyAt(I)).delete();

                                }
                            }

                            stockModels.removeAll(stockDeleteList);
                            adapter.notifyDataSetChanged();
                            // Close CAB (Contextual Action Bar)
                            mode.finish();
                            return true;
                        }
                        return false;
                    }
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_long_press, menu);
                        fab.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                        return true;
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        listView.clearChoices();
                        listView.requestLayout();
                        fab.setImageResource(R.drawable.ic_add_white_36dp);

                    }
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                });
                return true;
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

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    String[] symbols = new StockModel().getTickerArray();
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
            protected void onPostExecute(Void p) {
                stockModels.clear();
                stockModels.addAll(StockModel.listAll(StockModel.class));
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        adapter.notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public class HomeAdapter extends BaseAdapter {
        private ArrayList<StockModel> data;
        private LayoutInflater inflater;
        private boolean refresh = true;
        int red,green;
        public HomeAdapter(Activity activity, ArrayList<StockModel> data){
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
            TextView name;
            TextView price;
            TextView currency;
            TextView change;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            final StockModel stock = data.get(position);

            final ViewHolder viewHolder;
            if(rowView==null) {
                viewHolder = new ViewHolder();
                rowView = inflater.inflate(R.layout.listview_item_quote, parent, false);
                viewHolder.ticker = (TextView) rowView.findViewById(R.id.ticker);
                viewHolder.name = (TextView) rowView.findViewById(R.id.name);
                viewHolder.price = (TextView) rowView.findViewById(R.id.price);
                viewHolder.currency = (TextView) rowView.findViewById(R.id.currency);
                viewHolder.change = (TextView) rowView.findViewById(R.id.change);
                rowView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) rowView.getTag();
            }

            if(stock != null){
                viewHolder.ticker.setText(stock.getTicker());
                viewHolder.name.setText(stock.getName());
                viewHolder.price.setText(String.valueOf(stock.getPrice()));
                viewHolder.currency.setText(stock.getCurrency());
                if(stock.getChange().compareTo(BigDecimal.ZERO) == 1){
                    viewHolder.change.setTextColor(green);
                    viewHolder.change.setText("+"+String.valueOf(stock.getChange()));
                }else{
                    viewHolder.change.setTextColor(red);
                    viewHolder.change.setText(String.valueOf(stock.getChange()));
                }

            }
            return rowView;
        }
    }
}
