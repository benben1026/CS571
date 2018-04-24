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
public class ReviewFragment extends Fragment {


    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        ItemDetailManager im = ((DetailActivity)getActivity()).itemDetailManager;
        im.setReviewView((ListView)view.findViewById(R.id.review_list), (TextView)view.findViewById(R.id.review_info));


        return view;
    }

}
