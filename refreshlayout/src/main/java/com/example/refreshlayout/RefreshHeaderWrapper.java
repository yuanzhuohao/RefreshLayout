package com.example.refreshlayout;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by JessYuan on 19/07/2017.
 */

public class RefreshHeaderWrapper implements RefreshHeader {

    private static final String TAG = "RefreshHeaderWrapper";

    private View mView;

    public RefreshHeaderWrapper(View view) {
        mView = view;
    }

    @Override
    public void onPullingDown(float scale, int offset) {

    }

    @Override
    public void onReleasing(float scale, int offset) {

    }

    @Override
    public void RefreshFinished(boolean success) {

    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        Log.i(TAG, "onStateChanged: " + oldState + " " + newState);
    }

    @NonNull
    @Override
    public View getView() {
        return mView;
    }
}
