package com.example.weizhou.cs571;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean hasPermission = false;
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        }
        if (!hasPermission) {
            Log.i("Location", "Not has permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        if (hasPermission) {
            Log.i("Location", "Has permission");
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Objects.requireNonNull(locationManager).requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 100, ((MyApplication)getApplication()).getLocationListener());
        }
        Button button = findViewById(R.id.query_form_search);
        button.setOnClickListener(this);
        button = findViewById(R.id.query_form_reset);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_form_search:
                search((View) v.getParent());
                break;
            case R.id.query_form_reset:
                reset((View) v.getParent());
                break;
        }
    }

    public void search(View v){
        TextView keywordInput = v.findViewById(R.id.keyword_input);
        String keyword = keywordInput.getText().toString();

        Spinner categoryInput = v.findViewById(R.id.category_spinner);
        String category = categoryInput.getSelectedItem().toString();

        EditText distanceInput = v.findViewById(R.id.distance_input);

        int distance;
        try {
            distance = Integer.parseInt(distanceInput.getText().toString());
        } catch (NumberFormatException e) {
            distance = 10;
        }

        RadioGroup radioGroup = v.findViewById(R.id.from_group);
        String from = radioGroup.getCheckedRadioButtonId() == R.id.from_option_current ? "current" : "location";

        EditText locationInput = v.findViewById(R.id.from_location_input);
        String location = locationInput.getText().toString();

        SearchParam param = new SearchParam();
        param.setKeyword(keyword);
        param.setCategory(category);
        param.setDistance(distance);
        param.setFrom(from);
        param.setLocation(location);
        Location coordinate = ((MyApplication)this.getApplication()).getCurLocation();
        param.setCoordinate(coordinate.getLatitude(), coordinate.getLongitude());

        boolean passCheck = true;

        if (!param.checkKeyword()) {
            v.findViewById(R.id.keyword_warning).setVisibility(View.VISIBLE);
            passCheck = false;
        }
        if (!param.checkLocation()) {
            v.findViewById(R.id.from_location_warning).setVisibility(View.VISIBLE);
            passCheck = false;
        }

        if(!passCheck) return;

        progressDialog = ProgressDialog.show(this, null, "Fetching results", true);
        System.out.println(param.getUri(this));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, param.getUri(this), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("success");
                try {
                    JSONArray result = response.getJSONArray("results");

                } catch (JSONException e) {
                    JSONArray result = new JSONArray();

                }
                try {
                    String nextToken = response.getString("next_page_token");
                } catch (JSONException e) {
                    String nextToken = null;
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                System.out.println("Network Error");
                System.out.println(error);
                progressDialog.dismiss();
            }
        });
        MyApplication application = (MyApplication) this.getApplication();
        application.getRequestQueue().add(request);
    }

    public void reset(View v){
        TextView keywordInput = v.findViewById(R.id.keyword_input);
        keywordInput.setText("");

        EditText distanceInput = v.findViewById(R.id.distance_input);
        distanceInput.setText("");

        Spinner categoryInput = v.findViewById(R.id.category_spinner);
        categoryInput.setSelection(0);

        RadioGroup radioGroup = v.findViewById(R.id.from_group);
        radioGroup.check(R.id.from_option_current);

        EditText locationInput = v.findViewById(R.id.from_location_input);
        locationInput.setText("");
    }
}
