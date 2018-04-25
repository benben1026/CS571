package com.example.weizhou.cs571;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.transform.Result;

public class ResultAdapter extends ArrayAdapter<ResultItem> {
    private Context ctx;
    private ArrayList<ResultItem> data;
    private int startIndex;
    ImageLoader mImageLoader;

    ResultAdapter(Context ctx, ArrayList<ResultItem> data){
        super(ctx, 0, data);
        this.data = data;
        this.ctx = ctx;
        mImageLoader = new ImageLoader(((MyApplication)(((Activity)ctx).getApplication())).getRequestQueue(), new BitmapCache());
    }

    public void setStartIndex(int index){
        this.startIndex = index >= data.size() ? data.size() - 1 : index;
    }

    @Override
    public int getCount() {
        return data.size() - this.startIndex > 20 ? 20 : data.size() - this.startIndex;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(this.ctx).inflate(R.layout.result_item, parent, false);
        } else {
            view = convertView;
        }

        final ResultItem item = this.data.get(position + this.startIndex);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailActivity.class);
                intent.putExtra("placeId", item.placeId);
                intent.putExtra("name", item.name);
                intent.putExtra("icon", item.icon);
                intent.putExtra("vicinity", item.vicinity);
                ctx.startActivity(intent);
            }
        });

        NetworkImageView image = view.findViewById(R.id.result_img);
        image.setImageUrl(item.icon, mImageLoader);

        TextView name = view.findViewById(R.id.result_name);
        name.setText(item.name);

        TextView vicinity = view.findViewById(R.id.result_vicinity);
        vicinity.setText(item.vicinity);

        final FavoriteManager fm = ((MyApplication)((Activity)ctx).getApplication()).getFavoriteManager();
        final ResultAdapter ra = this;
        ImageView favorite = view.findViewById(R.id.result_favorite);
        if (fm.contain(item.placeId)){
            favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        favorite.setTag(item);

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.onFavoriteClick((ResultItem)v.getTag());
                ra.notifyDataSetChanged();
                fm.display();
            }
        });

        return view;
    }
}