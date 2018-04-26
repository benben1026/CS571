package com.example.weizhou.cs571;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class FavoriteFragment extends Fragment {

    FavoriteManager favoriteManager;

    public FavoriteFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        this.favoriteManager = ((MyApplication)this.getActivity().getApplication()).getFavoriteManager();
        this.favoriteManager.initManager(this.getActivity(),
                (ListView)view.findViewById(R.id.favorite_list), (TextView)view.findViewById(R.id.favorite_info));
        this.favoriteManager.display();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.favoriteManager.getAdapter().notifyDataSetChanged();
    }
}
