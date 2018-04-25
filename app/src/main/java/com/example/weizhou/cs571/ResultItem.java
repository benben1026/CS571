package com.example.weizhou.cs571;

import java.io.Serializable;

public class ResultItem implements Serializable {
    public String placeId;
    public String icon;
    public String name;
    public String vicinity;

    ResultItem(String placeId, String icon, String name, String vicinity){
        this.placeId = placeId;
        this.icon = icon;
        this.name = name;
        this.vicinity = vicinity;
    }

}
