package com.example.weizhou.cs571;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    private SearchResultManager searchResultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Search results");
        }

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        this.searchResultManager = new SearchResultManager(this, (ListView)findViewById(R.id.result_list), (TextView)findViewById(R.id.result_info),
                (Button) findViewById(R.id.result_previous), (Button) findViewById(R.id.result_next));
        this.searchResultManager.parseData(getIntent().getStringExtra("results"));
        this.searchResultManager.display(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchResultManager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
