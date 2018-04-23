package com.example.weizhou.cs571;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class FavoriteAdapter extends ArrayAdapter<ResultItem> {
    Context ctx;
    ArrayList<ResultItem> data;
    ImageLoader mImageLoader;

    FavoriteAdapter(Context ctx, ArrayList<ResultItem> data){
        super(ctx, 0, data);
        this.data = data;
        this.ctx = ctx;
        mImageLoader = new ImageLoader(((MyApplication)(((Activity)ctx).getApplication())).getRequestQueue(), new BitmapCache());
    }

    @Override
    public int getCount() {
        return this.data.size();
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

        final ResultItem item = this.data.get(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailActivity.class);
                intent.putExtra("placeId", item.placeId);
                ctx.startActivity(intent);
            }
        });


        NetworkImageView image = view.findViewById(R.id.result_img);
        image.setImageUrl(item.icon, mImageLoader);

        TextView name = view.findViewById(R.id.result_name);
        name.setText(item.name);

        TextView vicinity = view.findViewById(R.id.result_vicinity);
        vicinity.setText(item.vicinity);

        ImageView favorite = view.findViewById(R.id.result_favorite);
        favorite.setImageResource(R.drawable.ic_favorite_red_24dp);
        favorite.setTag(item);

        final FavoriteManager fm = ((MyApplication)((Activity)this.ctx).getApplication()).getFavoriteManager();
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.onFavoriteClick((ResultItem)v.getTag());
                fm.updateMsg();
            }
        });

        return view;
    }
}
