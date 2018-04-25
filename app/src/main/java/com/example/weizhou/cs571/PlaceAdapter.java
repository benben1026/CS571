package com.example.weizhou.cs571;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends ArrayAdapter<String> {
    private GeoDataClient geoDataClient;
    private AutoCompleteTextView autoCompleteTextView;
    long lastUpdate;
    private List<String> ids;

    public PlaceAdapter(@NonNull Context context, int resource, GeoDataClient client, AutoCompleteTextView textView) {
        super(context, resource);
        geoDataClient = client;
        autoCompleteTextView = textView;
        lastUpdate = -1;
        this.ids = new ArrayList<>();
    }

    public void getPredictions(CharSequence location) {
        if (System.currentTimeMillis() - lastUpdate < 500) return;
        lastUpdate = System.currentTimeMillis();
        Log.i("Google Place", "Executing autocomplete query for: " + location);
        Task<AutocompletePredictionBufferResponse> task = geoDataClient.getAutocompletePredictions(location.toString(), null, null);

        task.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>(){
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> completeTask) {
                if (!completeTask.isSuccessful()) {
                    Log.i("Google Place", "Task Failed");
                }
                clear();
                ids.clear();
                AutocompletePredictionBufferResponse response = completeTask.getResult();
                for (AutocompletePrediction prediction : response) {
                    ids.add(prediction.getPlaceId());
                    add(prediction.getFullText(null).toString());
                }
                response.release();

                getFilter().filter(autoCompleteTextView.getText());
            }
        });
    }

    public List<String> getIds() {
        return ids;
    }
}
