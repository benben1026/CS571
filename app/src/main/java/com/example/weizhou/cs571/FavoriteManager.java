package com.example.weizhou.cs571;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FavoriteManager {
    private ArrayList<ResultItem> data;
    private FavoriteAdapter adapter;
    private ListView listView;
    private TextView textView;
    private Activity activity;

    private SharedPreferences sharedPref;

    FavoriteManager(){ }

    public void initManager(Activity a, ListView lv, TextView tv){
        this.listView = lv;
        this.textView = tv;
        this.data = new ArrayList<ResultItem>();
        this.adapter = new FavoriteAdapter(a, this.data);
        this.activity = a;

        this.listView.setAdapter(this.adapter);

        this.sharedPref = a.getPreferences(Context.MODE_PRIVATE);
        String favoriteData = this.sharedPref.getString("favoriteData", "");
        if (!favoriteData.equals("")){
            try {
                this.data = (ArrayList<ResultItem>) ObjectSerializer.deserialize(favoriteData);
                this.adapter.data = data;
                this.display();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateSharedPref(){
        SharedPreferences.Editor editor = this.sharedPref.edit();
        try {
            editor.putString("favoriteData", ObjectSerializer.serialize(this.data));
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFavorite(ResultItem item){
        this.data.add(item);
        Toast.makeText(this.activity, item.name + " was added to favorites", Toast.LENGTH_SHORT).show();
        updateSharedPref();
    }

    public void removeFavorite(String id){
        for (int i = 0; i < this.data.size(); i++){
            if (this.data.get(i).placeId.equals(id)){
                Toast.makeText(this.activity, this.data.get(i).name + " was removed from favorites", Toast.LENGTH_SHORT).show();
                this.data.remove(i);
                updateSharedPref();
                return;
            }
        }
    }

    public boolean contain(String id){
        for (int i = 0; i < this.data.size(); i++){
            if (this.data.get(i).placeId.equals(id)){
                return true;
            }
        }
        return false;
    }

    public void display(){
        if (data.size() > 0){
            this.textView.setVisibility(View.INVISIBLE);
        } else {
            this.textView.setVisibility(View.VISIBLE);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void onFavoriteClick(ResultItem result) {
        if (this.contain(result.placeId)) {
            this.removeFavorite(result.placeId);
        }
        else {
            this.addFavorite(result);
        }
        adapter.notifyDataSetChanged();
    }
}
