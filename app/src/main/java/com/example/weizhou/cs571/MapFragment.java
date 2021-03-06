package com.example.weizhou.cs571;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private PlaceAdapter adapter;
    private GeoDataClient mGeoDataClient;
    private GoogleMap map;

    private ItemDetailManager itemDetailManager;

    private String fromId;
    private String fromName;

    public MapFragment() {
        // Required empty public constructor
        this.fromId = null;
        this.fromName = null;
    }

    private void updateRoute(){
        if (this.fromId == null || this.fromName == null){
            return;
        }

        Task<PlaceBufferResponse> getPlaceTask = mGeoDataClient.getPlaceById(this.fromId);

        ItemDetail itemDetail = this.itemDetailManager.item;
        final LatLng destination = new LatLng(itemDetail.lat, itemDetail.lng);

        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions().position(destination).title(itemDetail.getName())).showInfoWindow();
            //map.addMarker(new MarkerOptions().position(destination).title(itemDetail.getName()));
        }

        getPlaceTask.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getActivity(), "Fail to get this location.", Toast.LENGTH_SHORT).show();
                    return;
                }
                PlaceBufferResponse response = task.getResult();
                final Place place = response.get(0);
                final LatLng from = place.getLatLng();
                final String placeName = place.getName().toString();
                response.release();

                Spinner modeSpinner = getActivity().findViewById(R.id.map_mode_spinner);
                String mode = modeSpinner.getSelectedItem().toString().toLowerCase();

                GoogleDirection.withServerKey("AIzaSyDa0TpN4WJI79_50iyLR--gmWJhbu45MiA")
                        .from(from).to(destination).transportMode(mode).execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (!direction.isOK()) {
                            Toast.makeText(getActivity(), "Fail to get direction.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (map == null) return;

                        List<Route> routes = direction.getRouteList();
                        if (routes.isEmpty()) {
                            Toast.makeText(getActivity(), "No route found.", Toast.LENGTH_SHORT).show();
                        }
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(from);
                        builder.include(destination);
                        LatLngBounds bounds = builder.build();

                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                        map.addPolyline(new PolylineOptions().addAll(routes.get(0).getOverviewPolyline().getPointList()).color(Color.BLUE));
                        map.addMarker(new MarkerOptions().position(from).title(placeName)).showInfoWindow();

                    }
                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getActivity(), "Fail to get direction.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        this.itemDetailManager = ((DetailActivity)getActivity()).itemDetailManager;

        mGeoDataClient = Places.getGeoDataClient(Objects.requireNonNull(getActivity()));
        AutoCompleteTextView textView = view.findViewById(R.id.map_from_input);
        adapter = new PlaceAdapter(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, mGeoDataClient, textView);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getPredictions(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textView.setThreshold(1);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                fromId = adapter.getIds().get(position);
                fromName = adapter.getItem(position);
                updateRoute();
            }
        });

        Spinner modeSpinner = view.findViewById(R.id.map_mode_spinner);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRoute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        MapView mapView = view.findViewById(R.id.map_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng placeLatLng = new LatLng(itemDetailManager.item.lat, itemDetailManager.item.lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15));
                googleMap.addMarker(new MarkerOptions().position(placeLatLng).title(itemDetailManager.item.getName())).showInfoWindow();

                map = googleMap;
            }
        });

        return view;
    }

}
