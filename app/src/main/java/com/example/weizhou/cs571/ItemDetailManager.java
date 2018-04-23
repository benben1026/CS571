package com.example.weizhou.cs571;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemDetailManager {
    public ItemDetail item;
    public Activity activity;
    public View infoView;

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
        ((TextView)this.infoView.findViewById(R.id.rating_value)).setText(String.valueOf(this.item.getRating()));

        TextView googlePageTextView = this.infoView.findViewById(R.id.google_page_value);
        googlePageTextView.setText(String.valueOf(this.item.getGooglePage()));
        Linkify.addLinks(googlePageTextView, Linkify.WEB_URLS);

        TextView websiteTextView = this.infoView.findViewById(R.id.website_value);
        websiteTextView.setText(String.valueOf(this.item.getWebsite()));
        Linkify.addLinks(websiteTextView, Linkify.WEB_URLS);

    }
}
