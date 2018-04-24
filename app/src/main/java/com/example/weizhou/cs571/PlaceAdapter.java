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

public class PlaceAdapter extends ArrayAdapter<String> {
    private GeoDataClient geoDataClient;
    private AutoCompleteTextView autoCompleteTextView;
    long lastUpdate;

    public PlaceAdapter(@NonNull Context context, int resource, GeoDataClient client, AutoCompleteTextView textView) {
        super(context, resource);
        geoDataClient = client;
        autoCompleteTextView = textView;
        lastUpdate = -1;
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
                AutocompletePredictionBufferResponse response = completeTask.getResult();
                for (AutocompletePrediction prediction : response) {
                    add(prediction.getFullText(null).toString());
                }
                response.release();

                getFilter().filter(autoCompleteTextView.getText());
            }
        });
    }
}
