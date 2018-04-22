package com.example.weizhou.cs571;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ResultAdapter extends ArrayAdapter<ResultItem> {
    private Context ctx;
    private ArrayList<ResultItem> data;
    private int startIndex;

    ResultAdapter(Context ctx, ArrayList<ResultItem> data){
        super(ctx, 0, data);
        this.data = data;
        this.ctx = ctx;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(this.ctx).inflate(R.layout.result_item, parent, false);
        }

        ResultItem item = this.data.get(position + this.startIndex);

        //TODO: add detail event listener

        ImageView img = convertView.findViewById(R.id.result_img);
        new DownloadImageTask(img).execute(item.icon);

        TextView name = convertView.findViewById(R.id.result_name);
        name.setText(item.name);

        TextView vicinity = convertView.findViewById(R.id.result_vicinity);
        vicinity.setText(item.vicinity);

        //TODO: check favorite list
        ImageView favorite = convertView.findViewById(R.id.result_favorite);
        favorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        favorite.setTag(item);

        //TODO: add favorite click event listener

        return convertView;
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}