package com.example.weizhou.cs571;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {


    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ((DetailActivity)getActivity()).itemDetailManager.setPhotoView((ListView)view.findViewById(R.id.photo_list), (TextView)view.findViewById(R.id.photo_info));
        return view;
    }

}
