package com.example.weizhou.cs571;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.util.Objects;

public class SearchFragment extends Fragment implements View.OnClickListener{
    ProgressDialog progressDialog;
    private PlaceAdapter adapter;
    protected GeoDataClient geoDataClient;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        geoDataClient = Places.getGeoDataClient(Objects.requireNonNull(getActivity()));

        AutoCompleteTextView textView = view.findViewById(R.id.from_location_input);
        adapter = new PlaceAdapter(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, geoDataClient, textView);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RadioGroup radioGroup = view.findViewById(R.id.from_group);
                if (radioGroup.getCheckedRadioButtonId() != R.id.from_option_location) return;
                adapter.getPredictions(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setThreshold(1);
        textView.setAdapter(adapter);

        view.findViewById(R.id.form_search).setOnClickListener(this);
        view.findViewById(R.id.form_reset).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.form_search:
                search((View) v.getParent());
                break;
            case R.id.form_reset:
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
        String from = radioGroup.getCheckedRadioButtonId() == R.id.from_option_current ? "here" : "customized";

        EditText locationInput = v.findViewById(R.id.from_location_input);
        String location = locationInput.getText().toString();

        final SearchParam param = new SearchParam();
        param.setKeyword(keyword);
        param.setCategory(category);
        param.setDistance(distance);
        param.setFrom(from);
        param.setLocation(location);
        Location coordinate = ((MyApplication)getActivity().getApplication()).getCurLocation();
        param.setCoordinate(coordinate.getLatitude(), coordinate.getLongitude());
        param.setApi("search");

        boolean passCheck = true;

        if (!param.checkKeyword()) {
            v.findViewById(R.id.keyword_warning).setVisibility(View.VISIBLE);
            passCheck = false;
        }
        if (!param.checkLocation()) {
            v.findViewById(R.id.from_location_warning).setVisibility(View.VISIBLE);
            passCheck = false;
        }

        if(!passCheck) {
            Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), null, "Fetching results", true);
        System.out.println(param.getUri(getActivity()));
        final Intent searchToResultIntent = new Intent(getActivity(), ResultActivity.class);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, param.getUri(getActivity()), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                searchToResultIntent.putExtra("results", response.toString());
                startActivity(searchToResultIntent);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                System.out.println(error);
                Toast.makeText(getActivity(), "Network error. Please try later.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        MyApplication application = (MyApplication) getActivity().getApplication();
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

        v.findViewById(R.id.keyword_warning).setVisibility(View.GONE);
        v.findViewById(R.id.from_location_warning).setVisibility(View.GONE);
    }

}
