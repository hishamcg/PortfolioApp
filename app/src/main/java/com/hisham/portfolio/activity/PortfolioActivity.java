package com.hisham.portfolio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hisham.portfolio.R;
import com.hisham.portfolio.model.Portfolio;

import java.util.ArrayList;
import java.util.List;

public class PortfolioActivity extends AppCompatActivity {
    private PortAdapter adapter;
    private List<String> data = new ArrayList<>();
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        ListView listView = (ListView) findViewById(R.id.listview);

        data = new Portfolio().getAllPortfolio(data);

        assert listView != null;
        //adapter = new ArrayAdapter<String>(this, R.layout.simple_list, R.id.simple_text, data);
        adapter = new PortAdapter(this,data);
        listView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertBox();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PortfolioDetail.class);
                intent.putExtra("NAME", data.get(position));
                startActivity(intent);
            }
        });
    }

    private void showAlertBox(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PortfolioActivity.this);
        alertDialog.setTitle("Portfolio");
        alertDialog.setMessage("Enter Name");

        final EditText input = new EditText(PortfolioActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setGravity(Gravity.CENTER);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Portfolio p = new Portfolio();
                        String n = input.getText().toString();
                        if(!n.isEmpty()) {
                            p.setName(n);
                            p.save();
                            data = new Portfolio().getAllPortfolio(data);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public class PortAdapter extends BaseAdapter {
        private List<String> str;
        private LayoutInflater inflater;
        public PortAdapter(Activity activity, List<String> str){
            this.str = str;
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return str.size();
        }

        @Override
        public Object getItem(int position) {
            return str.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView name;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {

            final ViewHolder viewHolder;
            if(rowView==null) {
                viewHolder = new ViewHolder();
                rowView = inflater.inflate(R.layout.simple_list,parent, false);
                viewHolder.name = (TextView) rowView.findViewById(R.id.simple_text);
                rowView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) rowView.getTag();
            }

            if(str.get(position) != null){
                viewHolder.name.setText(str.get(position));
            }
            return rowView;
        }
    }
}
