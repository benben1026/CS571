package com.example.weizhou.cs571;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class PhotoAdapter extends ArrayAdapter<Bitmap> {
    Context ctx;
    ArrayList<Bitmap> photos;

    PhotoAdapter(Context ctx, ArrayList<Bitmap> data){
        super(ctx, 0, data);
        this.ctx = ctx;
        this.photos = data;
    }

    @Override
    public int getCount() {
        return this.photos.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(this.ctx).inflate(R.layout.photo_item, parent, false);
        } else {
            view = convertView;
        }

        final Bitmap photo = this.photos.get(position);

        ImageView img = view.findViewById(R.id.detail_img);
        img.setImageBitmap(photo);

        return view;
    }
}
