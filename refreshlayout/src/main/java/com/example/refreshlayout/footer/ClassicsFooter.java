package com.example.refreshlayout.footer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.refreshlayout.RefreshFooter;
import com.example.refreshlayout.RefreshLayout;
import com.example.refreshlayout.RefreshState;
import com.example.refreshlayout.SpinnerStyle;
import com.example.refreshlayout.internal.ProgressDrawable;
import com.example.refreshlayout.utils.DensityUtils;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.refreshlayout.RefreshState.None;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 经典上拉底部组件
 * Created by SCWANG on 2017/5/28.
 */

public class ClassicsFooter extends LinearLayout implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_FOOTER_LOADING = "正在加载...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_ALLLOADED = "全部加载完成";

    private TextView mBottomText;
    private ImageView mProgressView;
    private ProgressDrawable mProgressDrawable;
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    private boolean mLoadmoreFinished = false;

    //<editor-fold desc="LinearLayout">
    public ClassicsFooter(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        setGravity(Gravity.CENTER);
        setMinimumHeight(DensityUtils.dp2px(60));

        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff666666);
        mProgressView = new ImageView(context);
        mProgressView.setImageDrawable(mProgressDrawable);
        LayoutParams lpPathView = new LayoutParams(DensityUtils.dp2px(16), DensityUtils.dp2px(16));
        lpPathView.rightMargin = DensityUtils.dp2px(10);
        addView(mProgressView, lpPathView);

        mBottomText = new AppCompatTextView(context, attrs, defStyleAttr);
        mBottomText.setTextColor(0xff666666);
        mBottomText.setTextSize(16);
        mBottomText.setText(REFRESH_FOOTER_PULLUP);

        addView(mBottomText, WRAP_CONTENT, WRAP_CONTENT);

        if (!isInEditMode()) {
            mProgressView.setVisibility(GONE);
        }
    }
    
    

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    public boolean setLoadmoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (finished) {
                mBottomText.setText(REFRESH_FOOTER_ALLLOADED);
            } else {
                mBottomText.setText(REFRESH_FOOTER_PULLUP);
            }
            mProgressDrawable.stop();
            mProgressView.setVisibility(GONE);
        }
        return true;
    }

    @NonNull
    public View getView() {
        return this;
    }

    private Runnable restoreRunable;
    private void restoreRefreshLayoutBackground() {
        if (restoreRunable != null) {
            restoreRunable.run();
            restoreRunable = null;
        }
    }

    public ClassicsFooter setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }


    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
                    restoreRefreshLayoutBackground();
                case PullToUpLoad:
                    mBottomText.setText(REFRESH_FOOTER_PULLUP);
                    break;
                case Loading:
                    mBottomText.setText(REFRESH_FOOTER_LOADING);
                    break;
                case ReleaseToLoad:
                    mBottomText.setText(REFRESH_FOOTER_RELEASE);
                    mProgressView.setVisibility(VISIBLE);
                    mProgressDrawable.start();
                    break;
            }
        }
    }

    @Override
    public void onPullingUp(float scale, int offset) {

    }

    @Override
    public void onReleasing(float scale, int offset) {

    }

    @Override
    public void loadFinished(boolean success) {
        mProgressDrawable.stop();
        mProgressView.setVisibility(GONE);
        mBottomText.setText(REFRESH_FOOTER_FINISH);
    }


}
