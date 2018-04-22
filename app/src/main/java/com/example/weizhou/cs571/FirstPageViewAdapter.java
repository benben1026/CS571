package com.example.weizhou.cs571;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

class FirstPageViewAdapter extends FragmentPagerAdapter {
    private Fragment search = new SearchFragment();
    private Fragment favorite = new FavoriteFragment();

    public FirstPageViewAdapter(FragmentManager fm) {super(fm);}

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = search;
        }
        else if (position == 1)
        {
            fragment = favorite;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
