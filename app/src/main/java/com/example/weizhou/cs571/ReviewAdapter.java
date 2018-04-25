package com.example.weizhou.cs571;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewAdapter extends ArrayAdapter<Review> {
    Context ctx;
    ArrayList<Review> data;
    ImageLoader mImageLoader;

    ReviewAdapter(Context ctx, ArrayList<Review> data){
        super(ctx, 0, data);
        this.ctx = ctx;
        this.data = data;
        mImageLoader = new ImageLoader(((MyApplication)(((Activity)ctx).getApplication())).getRequestQueue(), new BitmapCache());
    }

    public void setData(ArrayList<Review> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(this.ctx).inflate(R.layout.review_item, parent, false);
        } else {
            view = convertView;
        }

        final Review review = this.data.get(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.url));
                ctx.startActivity(browserIntent);
            }
        });


        NetworkImageView profile = view.findViewById(R.id.reviewer_img);
        profile.setImageUrl(review.authorImg, mImageLoader);

        TextView name = view.findViewById(R.id.reviewer_name);
        name.setText(review.authorName);

        RatingBar rating = view.findViewById(R.id.reviewer_rating);
        rating.setRating(review.rating);

        TextView time = view.findViewById(R.id.review_time);
        Date date = new Date(review.dateTime);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        time.setText(df.format("yyyy-MM-dd hh:mm:ss", date));

        TextView content = view.findViewById(R.id.review_content);
        content.setText(review.comment);


        return view;
    }
}
