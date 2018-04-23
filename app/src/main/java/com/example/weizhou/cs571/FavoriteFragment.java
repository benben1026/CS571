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
        FavoriteManager fm = ((MyApplication)this.getActivity().getApplication()).getFavoriteManager();
        fm.initManager(this.getActivity(),
                (ListView)view.findViewById(R.id.favorite_list), (TextView)view.findViewById(R.id.favorite_info));
        fm.display();
        // Inflate the layout for this fragment
        return view;
    }

}
