package com.example.weizhou.cs571;

import android.app.Activity;
import android.net.Uri;

public class SearchParam {
    String keyword;
    String category;
    int distance;
    String from;
    String location;
    Double lat, lng;
    String nextToken;

    public SearchParam(){
        this.nextToken = null;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setDistance(int distance){
        this.distance = distance * 1609 > 50000 ? 50000 : distance * 1609;
    }

    public  void setFrom(String from){
        this.from = from;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setCoordinate(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public void setNextToken(String nextToken){
        this.nextToken = nextToken;
    }

    boolean checkKeyword() {
        return !this.keyword.isEmpty();
    }

    boolean checkLocation() {
        return this.from.equals("current") || !this.location.isEmpty();
    }

    public String getUri(Activity activity){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(activity.getResources().getString(R.string.server_domain)).appendPath("nearbysearch");
        if (this.nextToken != null){
            builder.appendQueryParameter("pagetoken", this.nextToken);
        } else {
            builder.appendQueryParameter("keyword", this.keyword);
            builder.appendQueryParameter("radius", String.valueOf(this.distance));
            builder.appendQueryParameter("from", this.from);
            builder.appendQueryParameter("lat", String.valueOf(this.lat));
            builder.appendQueryParameter("lng", String.valueOf(this.lng));
            builder.appendQueryParameter("location", this.location);
        }
        return builder.build().toString();
    }

}
