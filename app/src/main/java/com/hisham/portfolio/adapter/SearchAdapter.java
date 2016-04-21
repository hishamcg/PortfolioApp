package com.hisham.portfolio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.hisham.portfolio.R;
import com.hisham.portfolio.model.SearchModel;

import java.util.ArrayList;

/**
 * Created by hisham on 19/4/16.
 */
public class SearchAdapter extends ArrayAdapter<SearchModel> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<SearchModel> items;
    private ArrayList<SearchModel> itemsAll;
    private ArrayList<SearchModel> suggestions;
    private int viewResourceId;

    public SearchAdapter(Context context, int viewResourceId, ArrayList<SearchModel> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<SearchModel>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        SearchModel search = items.get(position);
        if (search != null) {
            TextView ticker = (TextView) v.findViewById(R.id.ticker);
            TextView name = (TextView) v.findViewById(R.id.name);
            if (ticker != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                ticker.setText(search.getTicker());
                name.setText(search.getName());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((SearchModel)(resultValue)).getTicker();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (SearchModel ser : itemsAll) {
                    if(ser.getTicker().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(ser);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<SearchModel> filteredList = (ArrayList<SearchModel>) results.values;
            if(results.count > 0) {
                clear();
                //addAll(filteredList);
                for (SearchModel c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}