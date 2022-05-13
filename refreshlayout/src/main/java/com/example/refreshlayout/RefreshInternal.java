package com.example.refreshlayout;

import android.view.View;

import androidx.annotation.NonNull;

public interface RefreshInternal extends OnStateChangedListener {

    @NonNull
    View getView();
}
