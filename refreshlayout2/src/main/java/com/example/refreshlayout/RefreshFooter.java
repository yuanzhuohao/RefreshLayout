package com.example.refreshlayout;

/**
 * Created by JessYuan on 18/07/2017.
 */

public interface RefreshFooter extends RefreshInternal {

    void onPullingUp(float scale, int offset);

    void onReleasing(float scale, int offset);

    void loadFinished(boolean success);
}
