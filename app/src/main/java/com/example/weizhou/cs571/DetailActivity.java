package com.example.weizhou.cs571;

import android.content.ClipData;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    public ItemDetailManager itemDetailManager;
    public FavoriteManager favoriteManager;

    private ResultItem resultItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final TabLayout tabLayout = findViewById(R.id.detail_tabs);
        final ViewPager viewPager = findViewById(R.id.detail_viewpager);
        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(getDrawable(R.drawable.tab_divider));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        viewPager.setAdapter(new DetailTabViewAdapter(getSupportFragmentManager()));

        this.resultItem = new ResultItem(getIntent().getStringExtra("placeId"),
                getIntent().getStringExtra("icon"),
                getIntent().getStringExtra("name"),
                getIntent().getStringExtra("vicinity"));

        itemDetailManager = new ItemDetailManager(this, this.resultItem.placeId);

        favoriteManager = ((MyApplication)getApplication()).getFavoriteManager();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(this.resultItem.name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);

        if (this.favoriteManager.contain(this.resultItem.placeId)){
            item.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (favoriteManager.contain(resultItem.placeId)){
                    favoriteManager.removeFavorite(resultItem.placeId);
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                } else {
                    favoriteManager.addFavorite(resultItem);
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                }
                ((MyApplication)getApplication()).searchResultManager.getAdapter().notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return false;
            case R.id.action_favorite:

                return false;
            case R.id.action_share:

                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
