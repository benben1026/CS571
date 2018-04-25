package com.example.weizhou.cs571;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {


    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ItemDetailManager im = ((DetailActivity)getActivity()).itemDetailManager;
        im.setInfoView(view);
        im.displayInfo();
        // Inflate the layout for this fragment
        return view;
    }

}
