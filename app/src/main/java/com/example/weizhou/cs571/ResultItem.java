package com.example.weizhou.cs571;

public class ResultItem {
    public String placeId;
    public String icon;
    public String name;
    public String vicinity;

    ResultItem(String placeId, String icon, String name, String vicnity){
        this.placeId = placeId;
        this.icon = icon;
        this.name = name;
        this.vicinity = vicnity;
    }
}
