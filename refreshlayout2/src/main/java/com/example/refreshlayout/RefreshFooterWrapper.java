package com.example.refreshlayout;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

/**
 * Created by JessYuan on 19/07/2017.
 */

public class RefreshFooterWrapper implements RefreshFooter {
    private static final String TAG = "RefreshFooterWrapper";

    private View mView;

    public RefreshFooterWrapper(View view) {
        mView = view;
    }

    @Override
    public void onPullingUp(float scale, int offset) {
        Log.i(TAG, "pulling " + scale + " " + offset);
    }

    @Override
    public void onReleasing(float scale, int offset) {
        Log.i(TAG, "releasing " + scale + " " + offset);

    }

    @Override
    public void loadFinished(boolean success) {

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
