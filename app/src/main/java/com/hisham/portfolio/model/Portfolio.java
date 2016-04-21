package com.hisham.portfolio.model;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hisham on 20/4/16.
 */
public class Portfolio extends SugarRecord  {
    String name;

    public Portfolio() {}

    public List<String> getAllPortfolio(List<String> arr){
        List<Portfolio> port = Portfolio.listAll(Portfolio.class);
        //String[] arr = new String[port.size()];
        //ArrayList<String> arr = new ArrayList<>();
        arr.clear();
        for (int i=0;i<port.size();i++){
            arr.add(port.get(i).getName());
        }
        return arr;
    }

    public List<PortModel> getAllStocks(){
        return PortModel.find(PortModel.class,"portfolio = ?", String.valueOf(this.getId()));
    }

    public  PortModel getStockWith(String symb){
        return PortModel.find(PortModel.class,"portfolio = ? and ticker = ?"
                , String.valueOf(this.getId()),symb).get(0);
    }

    public String[] getTickerArray(){
        List<PortModel> p = this.getAllStocks();
        String[] tickr = new String[p.size()];
        for (int i=0;i<p.size();i++){
            tickr[i] = p.get(i).getTicker();
        }
        return tickr;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
