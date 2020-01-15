package com.mumuwoyou.mycount.app.dbmodel;

import org.litepal.crud.LitePalSupport;
public class PlaceModel extends LitePalSupport {

    private int id;

    private String place;

    private String isdefault;

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

    public String getIsdefault() { return  isdefault; }

    public void  setIsdefault(String isdefault) { this.isdefault = isdefault; }

}
