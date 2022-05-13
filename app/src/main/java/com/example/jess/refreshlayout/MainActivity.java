package com.example.jess.refreshlayout;

import android.os.Handler;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.refreshlayout.RefreshLayout;
import com.example.refreshlayout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader mHeader;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshlayout);
        mHeader = (ClassicsHeader) findViewById(R.id.header);

        final List<String> data = new ArrayList<>();
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

        mRefreshLayout.setEnableLoad(false);
        Date date = new Date();
        date.setDate(6);
        mHeader.setLastUpdateTime(date);
        mHeader.setTimeFormat(new SimpleDateFormat("上次更新 M-d HH:mm", Locale.CHINA));

        final Handler handler = new Handler(getMainLooper());
        mRefreshLayout.setRefreshListener(new RefreshLayout.PullToRefreshListener() {
            @Override
            public void refresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
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
                        mAdapter.notifyDataSetChanged();

                        mRefreshLayout.setRefreshed(true, true);
                    }
                }, 2000);
            }
        });

        mRefreshLayout.setLoadMoreListener(new RefreshLayout.LoadMoreListener() {
            @Override
            public void loadmore() {
                mRefreshLayout.setLoaded(true, true);
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mRefreshLayout.refresh();
    }
}
