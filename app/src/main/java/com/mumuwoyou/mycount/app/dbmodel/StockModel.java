package com.mumuwoyou.mycount.app.dbmodel;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class StockModel extends LitePalSupport {

    private int id;

    private String code;

    private String name;

    private String barcode;

    private  int count;

    private  int sum;

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDetailList(List<DetailModel> detailList) {
        this.detailList = detailList;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getCount() {
        return count;
    }

    public List<DetailModel> getDetailList() {
        return detailList;
    }

    private List<DetailModel> detailList = new ArrayList<DetailModel>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
