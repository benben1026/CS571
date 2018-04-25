package com.example.weizhou.cs571;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ItemDetailManager {
    public ItemDetail item;
    public Activity activity;

    private View infoView;
    private ListView photoListView;
    private TextView photoTextView;
    private View reviewFragmentView;

    private PhotoAdapter photoAdapter;
    private ReviewAdapter reviewAdapter;
    private Uri.Builder yelpRequest;

    private int photoIndex;
    private GeoDataClient mGeoDataClient;

    ItemDetailManager(Activity activity, final String placeId){
        this.item = new ItemDetail(placeId);
        this.infoView = null;
        this.activity = activity;
        this.yelpRequest = new Uri.Builder();
        this.yelpRequest.scheme("http").authority(activity.getResources().getString(R.string.server_domain)).appendPath("getyelpreviews");
        final Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(activity.getResources().getString(R.string.server_domain)).appendPath("placedetail");
        builder.appendQueryParameter("id", placeId);
        final ProgressDialog progressDialog = ProgressDialog.show(activity, null, "Fetching results", true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, builder.build().toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseDate(response);
                getYelpReview();
                displayInfo();
                progressDialog.dismiss();
                reviewAdapter.notifyDataSetChanged();
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
        this.photoAdapter = new PhotoAdapter(activity, this.item.getPhotos());

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

        this.reviewAdapter = new ReviewAdapter(activity, this.item.getGoogleReviews());
    }

    private void photoLoadingCallback(final PlacePhotoMetadataBuffer photoMetadataBuffer){
        PlacePhotoMetadata photoMetadata;
        try{
            photoMetadata = photoMetadataBuffer.get(this.photoIndex);
        } catch(Exception e){
            photoMetadataBuffer.release();
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
                photoTextView.setVisibility(View.INVISIBLE);
                photoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getYelpReview(){
        System.out.println(this.yelpRequest.build().toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, this.yelpRequest.build().toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equalsIgnoreCase("OK")){
                        JSONArray reviews = response.getJSONObject("data").getJSONArray("reviews");
                        for (int i = 0; i < reviews.length(); i++){
                            Review r = new Review();
                            r.authorName = (((JSONObject)reviews.get(i)).getJSONObject("user")).getString("name");
                            r.authorImg = (((JSONObject)reviews.get(i)).getJSONObject("user")).getString("image_url");
                            r.rating = (float)((JSONObject)reviews.get(i)).getDouble("rating");
                            r.url = ((JSONObject)reviews.get(i)).getString("url");
                            r.comment = ((JSONObject)reviews.get(i)).getString("text");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date date = sdf.parse(((JSONObject)reviews.get(i)).getString("time_created"));
                            r.dateTime = date.getTime();
                            item.addYelpReviews(r);
                        }
                    }
                    System.out.println("got yelp reviews");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                System.out.println(error);
            }
        });
        MyApplication application = (MyApplication) activity.getApplication();
        application.getRequestQueue().add(request);
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
                if (result.has("reviews")){
                    JSONArray reviews = result.getJSONArray("reviews");
                    for (int i = 0; i < reviews.length(); i++){
                        JSONObject reviewObject = (JSONObject) reviews.get(i);
                        Review r = new Review();
                        r.authorName = reviewObject.getString("author_name");
                        r.authorImg = reviewObject.getString("profile_photo_url");
                        r.rating = (float)reviewObject.getDouble("rating");
                        r.comment = reviewObject.getString("text");
                        r.dateTime = reviewObject.getLong("time") * 1000;
                        r.url = reviewObject.getString("author_url");
                        this.item.addGoogleReviews(r);
                    }
                }
                item.lat = result.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                item.lng = result.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                JSONArray addressComponents = result.getJSONArray("address_components");
                String streetNumber = "", route = "";
                for (int i = 0; i < addressComponents.length(); i++){
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("country")){
                        yelpRequest.appendQueryParameter("country", ((JSONObject)addressComponents.get(i)).getString("short_name"));
                    }
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("administrative_area_level_1")){
                        yelpRequest.appendQueryParameter("state", ((JSONObject)addressComponents.get(i)).getString("short_name"));
                    }
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("administrative_area_level_2")){
                        yelpRequest.appendQueryParameter("city", ((JSONObject)addressComponents.get(i)).getString("short_name"));
                    }
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("postal_code\"")){
                        yelpRequest.appendQueryParameter("postal", ((JSONObject)addressComponents.get(i)).getString("short_name"));
                    }
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("street_number")){
                        streetNumber = ((JSONObject)addressComponents.get(i)).getString("short_name");
                    }
                    if (((JSONObject)addressComponents.get(i)).getJSONArray("types").toString().contains("route")){
                        route = ((JSONObject)addressComponents.get(i)).getString("short_name");
                    }
                }
                yelpRequest.appendQueryParameter("name", this.item.getName());
                yelpRequest.appendQueryParameter("address", streetNumber + route);
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
            ((RatingBar)this.infoView.findViewById(R.id.rating_star)).setRating(this.item.getRating());
        }

        TextView googlePageTextView = this.infoView.findViewById(R.id.google_page_value);
        googlePageTextView.setText(String.valueOf(this.item.getGooglePage()));
        Linkify.addLinks(googlePageTextView, Linkify.WEB_URLS);

        TextView websiteTextView = this.infoView.findViewById(R.id.website_value);
        websiteTextView.setText(String.valueOf(this.item.getWebsite()));
        Linkify.addLinks(websiteTextView, Linkify.WEB_URLS);

    }

    public void setPhotoView(ListView photoListView, TextView photoTextView){
        this.photoListView = photoListView;
        this.photoListView.setAdapter(this.photoAdapter);
        this.photoTextView = photoTextView;
    }

    public void setReviewView(View view){
        this.reviewFragmentView = view;
        ((ListView)view.findViewById(R.id.review_list)).setAdapter(this.reviewAdapter);
    }

    public void displayReview(String origin, String sorting){
        System.out.println("Display reviews from " + origin + ", in " + sorting);
        TextView tv = this.reviewFragmentView.findViewById(R.id.review_info);
        if (origin.equalsIgnoreCase("google reviews")){
            if (this.item.getGoogleReviews().size() == 0){
                tv.setText("No reviews from google");
                tv.setVisibility(View.VISIBLE);
                this.reviewAdapter.setData(this.item.getGoogleReviews());
                return;
            }
            tv.setVisibility(View.GONE);
            ArrayList<Review> copyOfReviews = (ArrayList<Review>) this.item.getGoogleReviews().clone();
            switch (sorting){
                case "Default order":
                    this.reviewAdapter.setData(this.item.getGoogleReviews());
                    break;
                case "Highest rating":
                    Collections.sort(copyOfReviews, Review.highestRating);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Lowest rating":
                    Collections.sort(copyOfReviews, Review.lowestRating);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Most recent":
                    Collections.sort(copyOfReviews, Review.mostRecent);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Least recent":
                    Collections.sort(copyOfReviews, Review.leastRecent);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
            }
        } else if(origin.equalsIgnoreCase("yelp reviews")){
            if (this.item.getGoogleReviews().size() == 0){
                tv.setText("No reviews from yelp");
                tv.setVisibility(View.VISIBLE);
                this.reviewAdapter.setData(this.item.getYelpReviews());
                return;
            }
            tv.setVisibility(View.GONE);
            ArrayList<Review> copyOfReviews = (ArrayList<Review>) this.item.getYelpReviews().clone();
            switch (sorting){
                case "Default order":
                    this.reviewAdapter.setData(this.item.getYelpReviews());
                    break;
                case "Highest rating":
                    Collections.sort(copyOfReviews, Review.highestRating);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Lowest rating":
                    Collections.sort(copyOfReviews, Review.lowestRating);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Most recent":
                    Collections.sort(copyOfReviews, Review.mostRecent);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
                case "Least recent":
                    Collections.sort(copyOfReviews, Review.leastRecent);
                    this.reviewAdapter.setData(copyOfReviews);
                    break;
            }
        }
    }

}
