package com.hisham.portfolio.model;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by hisham on 20/4/16.
 */
public class PortModel extends SugarRecord{
    @Unique
    String ticker;
    String name,currency;
    BigDecimal price,change;
    Integer shares,paid_price;
    Portfolio portfolio;

    public PortModel(){
    }

    public PortModel(String ticker,String name,String currency,BigDecimal price,BigDecimal change,
            Integer shares,Integer paid_price,Portfolio portfolio) {
        this.ticker = ticker;
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.change = change;
        this.shares = shares;
        this.paid_price = paid_price;
        this.portfolio = portfolio;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public Integer getPaid_price() {
        return paid_price;
    }

    public void setPaid_price(Integer paid_price) {
        this.paid_price = paid_price;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
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
