package com.example.weizhou.cs571;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultManager {

    private ArrayList<ResultItem> results;
    private String nextToken;
    private int page;

    private ListView resultList;
    private TextView resultInfo;
    private Button nextPageBtn, previousPageBtn;
    private Activity activity;
    private ResultAdapter adapter;

    public SearchResultManager(Activity a, ListView lv, TextView tv, Button pb, Button nb){
        this.activity = a;
        this.resultList = lv;
        this.resultInfo = tv;
        this.nextPageBtn = nb;
        this.previousPageBtn = pb;
        this.page = 1;
        this.nextToken = null;
        this.results = new ArrayList<ResultItem>();
        this.adapter = new ResultAdapter(a, results);

        this.resultList.setAdapter(this.adapter);

        this.previousPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (page <= 1){

                }
                display(page -= 1);
            }
        });

        this.nextPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ((page + 1) * activity.getResources().getInteger(R.integer.data_per_page) <= results.size()){
                    display(page += 1);
                    return;
                }
                SearchParam param = new SearchParam();
                param.setNextToken(nextToken);
                param.setApi("nextpage");
                System.out.println(param.getUri(activity));
                final ProgressDialog progressDialog = ProgressDialog.show(activity, null, "Fetching results", true);
                final Intent searchToResultIntent = new Intent(activity, ResultActivity.class);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, param.getUri(activity), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseData(response.toString());
                        display(page += 1);
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        System.out.println(error);
                        progressDialog.dismiss();
                    }
                });
                MyApplication application = (MyApplication) activity.getApplication();
                application.getRequestQueue().add(request);
            }
        });
    }

    public ResultAdapter getAdapter() {
        return adapter;
    }

    public void parseData(String dataString){
        JSONObject dataObject;
        try {
            dataObject = new JSONObject(dataString);

        } catch (JSONException e) {
            e.printStackTrace();
            this.resultInfo.setText("Data Error");
            this.resultInfo.setVisibility(View.VISIBLE);
            this.previousPageBtn.setEnabled(false);
            this.nextPageBtn.setEnabled(false);
            return;
        }
        try{
            if (dataObject.getString("status").equalsIgnoreCase("OK")){
                JSONArray dataArray = dataObject.getJSONArray("results");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject d = dataArray.getJSONObject(i);
                    this.addItem(new ResultItem(d.getString("place_id"), d.getString("icon"), d.getString("name"), d.getString("vicinity")));
                }
                this.resultInfo.setVisibility(View.INVISIBLE);
            } else if(dataObject.getString("status").equalsIgnoreCase("ZERO_RESULTS")){
                this.resultInfo.setText("No Results found.");
                this.resultInfo.setVisibility(View.VISIBLE);
                this.previousPageBtn.setEnabled(false);
                this.nextPageBtn.setEnabled(false);
                return;
            }
        } catch (JSONException e){
            this.resultInfo.setText("Data Error");
            this.resultInfo.setVisibility(View.VISIBLE);
            this.previousPageBtn.setEnabled(false);
            this.nextPageBtn.setEnabled(false);
            return;
        }
        try {
            this.nextToken = dataObject.getString("next_page_token");
        } catch (JSONException e){
            this.nextToken = null;
        }
    }

    public void display(int page){
        if (this.results.size() == 0) {
            return;
        }
        System.out.println("page=" + page);
        System.out.println("size=" + results.size());
        this.page = page;
        adapter.setStartIndex((page - 1) * this.activity.getResources().getInteger(R.integer.data_per_page));
        if (page == 1){
            this.previousPageBtn.setEnabled(false);
        } else {
            this.previousPageBtn.setEnabled(true);
        }
        if (page * this.activity.getResources().getInteger(R.integer.data_per_page) >= this.results.size() && this.nextToken == null){
            this.nextPageBtn.setEnabled(false);
        } else {
            this.nextPageBtn.setEnabled(true);
        }
        this.adapter.notifyDataSetChanged();
    }



    private void addItem(ResultItem item){
        results.add(item);
    }
}
