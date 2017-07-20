package com.example.refreshlayout;

import android.support.annotation.NonNull;
import android.view.View;

public interface RefreshInternal extends OnStateChangedListener {

    @NonNull
    View getView();
}
