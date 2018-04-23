package com.example.weizhou.cs571;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.util.Linkify;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetailManager {
    public ItemDetail item;
    public Activity activity;
    public View infoView;

    private int photoIndex;
    private GeoDataClient mGeoDataClient;

    ItemDetailManager(Activity activity, final String placeId){
        this.item = new ItemDetail(placeId);
        this.infoView = null;
        this.activity = activity;
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(activity.getResources().getString(R.string.server_domain)).appendPath("placedetail");
        builder.appendQueryParameter("id", placeId);
        final ProgressDialog progressDialog = ProgressDialog.show(activity, null, "Fetching results", true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseDate(response);
                displayInfo();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                System.out.println(error);
                item = null;
                progressDialog.dismiss();
            }
        });
        MyApplication application = (MyApplication) activity.getApplication();
        application.getRequestQueue().add(request);


        this.photoIndex = 0;

        mGeoDataClient = Places.getGeoDataClient(activity, null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                photoLoadingCallback(photoMetadataBuffer);
            }
        });
    }

    private void photoLoadingCallback(final PlacePhotoMetadataBuffer photoMetadataBuffer){
        PlacePhotoMetadata photoMetadata;
        try{
            photoMetadata = photoMetadataBuffer.get(this.photoIndex);
        } catch(Exception e){
            return;
        }
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                item.addPhoto(bitmap);
                photoIndex ++;
                photoLoadingCallback(photoMetadataBuffer);
            }
        });
    }

    public void parseDate(JSONObject data){
        try {
            if(data.getString("status").equalsIgnoreCase("OK")){
                JSONObject result = data.getJSONObject("result");
                this.item.setName(result.getString("name"));
                if (result.has("formatted_address"))
                    this.item.setAddress(result.getString("formatted_address"));
                if (result.has("formatted_phone_number"))
                    this.item.setPhoneNumber(result.getString("formatted_phone_number"));
                if (result.has("price_level"))
                    this.item.setPriceLevel(result.getInt("price_level"));
                if (result.has("rating"))
                    this.item.setRating((float)result.getDouble("rating"));
                if (result.has("url"))
                    this.item.setGooglePage(result.getString("url"));
                if (result.has("website"))
                    this.item.setWebsite(result.getString("website"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO
        }
    }

    public void setInfoView(View view){
        this.infoView = view;
    }

    public void displayInfo(){
        if (this.infoView == null){
            return;
        }
        ((TextView)this.infoView.findViewById(R.id.address_value)).setText(this.item.getAddress());

        TextView phoneTextView = this.infoView.findViewById(R.id.phone_value);
        phoneTextView.setText(this.item.getPhoneNumber());
        Linkify.addLinks(phoneTextView, Linkify.PHONE_NUMBERS);

        ((TextView)this.infoView.findViewById(R.id.price_level_value)).setText(String.valueOf(this.item.getPriceLevel()));
        //((TextView)this.infoView.findViewById(R.id.rating_value)).setText(String.valueOf(this.item.getRating()));
        //Display rating star, if rate == -1, set the whole layout invisible
        if(this.item.getRating() == -1) {
            this.infoView.findViewById(R.id.linear_rating_name).setVisibility(View.GONE);
        } else {
            ((RatingBar)this.infoView.findViewById(R.id.raing_star)).setRating(this.item.getRating());
        }

        TextView googlePageTextView = this.infoView.findViewById(R.id.google_page_value);
        googlePageTextView.setText(String.valueOf(this.item.getGooglePage()));
        Linkify.addLinks(googlePageTextView, Linkify.WEB_URLS);

        TextView websiteTextView = this.infoView.findViewById(R.id.website_value);
        websiteTextView.setText(String.valueOf(this.item.getWebsite()));
        Linkify.addLinks(websiteTextView, Linkify.WEB_URLS);

    }
}
