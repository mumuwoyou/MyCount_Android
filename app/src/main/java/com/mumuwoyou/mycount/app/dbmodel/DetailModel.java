package com.mumuwoyou.mycount.app.dbmodel;

import org.litepal.crud.LitePalSupport;

public class DetailModel extends LitePalSupport {

    private int id;

    private  String place;

    private  int count;

    private  StockModel stockmodel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public StockModel getStockmodel() {
        return stockmodel;
    }

    public void setStockmodel(StockModel stockmodel) {
        this.stockmodel = stockmodel;
    }




}
