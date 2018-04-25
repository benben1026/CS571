package com.example.weizhou.cs571;

import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MyApplication extends Application {
    RequestQueue requestQueue;
    Location curLocation;
    LocationListener locationListener;
    FavoriteManager favoriteManager;
    public SearchResultManager searchResultManager;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public Location getCurLocation() {
        return curLocation;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setCurLocation(Location location) {
        curLocation = location;
    }

    public FavoriteManager getFavoriteManager() { return favoriteManager; }

    @Override
    public void onCreate() {
        super.onCreate();
        favoriteManager = new FavoriteManager();
        requestQueue =  Volley.newRequestQueue(this);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLocation = location;
                Log.i("Location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
}
