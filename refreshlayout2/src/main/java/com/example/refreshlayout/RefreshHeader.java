package com.example.refreshlayout;

/**
 * Created by JessYuan on 18/07/2017.
 */

public interface RefreshHeader extends RefreshInternal {
    void onPullingDown(float scale, int offset);

    void onReleasing(float scale, int offset);

    void RefreshFinished(boolean success);
}
