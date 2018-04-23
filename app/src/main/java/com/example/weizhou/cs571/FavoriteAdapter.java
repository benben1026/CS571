package com.example.weizhou.cs571;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteAdapter extends ArrayAdapter<ResultItem> {
    Context ctx;
    ArrayList<ResultItem> data;

    FavoriteAdapter(Context ctx, ArrayList<ResultItem> data){
        super(ctx, 0, data);
        this.data = data;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.ctx).inflate(R.layout.result_item, parent, false);
        }

        ResultItem item = this.data.get(position);

        //TODO: add detail event listener

        ImageView img = convertView.findViewById(R.id.result_img);
        new DownloadImageTask(img).execute(item.icon);

        TextView name = convertView.findViewById(R.id.result_name);
        name.setText(item.name);

        TextView vicinity = convertView.findViewById(R.id.result_vicinity);
        vicinity.setText(item.vicinity);

        ImageView favorite = convertView.findViewById(R.id.result_favorite);
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

        return convertView;
    }
}
