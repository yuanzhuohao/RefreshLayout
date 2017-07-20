package com.example.jess.refreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.refreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private RefreshLayout mRefreshLayout;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshlayout);

        List<String> data = new ArrayList<>();
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");
        data.add("hello world!");

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, data);
        mListView.setAdapter(mAdapter);

        mRefreshLayout.setRefreshListener(new RefreshLayout.PullToRefreshListener() {
            @Override
            public void refresh() {
                mRefreshLayout.setRefreshed(true, true);
            }
        });

        mRefreshLayout.setLoadMoreListener(new RefreshLayout.LoadMoreListener() {
            @Override
            public void loadmore() {
                mRefreshLayout.setLoaded(true, true);
            }
        });


    }
}
