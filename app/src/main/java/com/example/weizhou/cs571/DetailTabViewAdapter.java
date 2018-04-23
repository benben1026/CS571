package com.example.weizhou.cs571;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

class DetailTabViewAdapter extends FragmentPagerAdapter {
    DetailTabViewAdapter(FragmentManager fragmentManager){ super(fragmentManager); }

    private PhotoFragment photos = new PhotoFragment();
    private InfoFragment info = new InfoFragment();
    private MapFragment map = new MapFragment();
    private ReviewFragment review = new ReviewFragment();

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return info;
            case 1:
                return photos;
            case 2:
                return map;
            case 3:
                return review;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
