package com.hisham.portfolio.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by hisham on 19/4/16.
 */
public class StockModel extends SugarRecord {
    @Unique
    String ticker;
    String name,currency;
    BigDecimal price,change;

    public StockModel(){
    }

    public StockModel(String ticker,String name,String currency,BigDecimal price,BigDecimal change) {
        this.ticker = ticker;
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.change = change;
    }

    public String[] getTickerArray(){
        ArrayList<StockModel> stockModels = new ArrayList<>();
        stockModels.addAll(StockModel.listAll(StockModel.class));
        String[] tickr = new String[stockModels.size()];
        for (int i=0;i<stockModels.size();i++){
            tickr[i] = stockModels.get(i).getTicker();
        }
        return tickr;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }
}
