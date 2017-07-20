package com.example.refreshlayout;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.example.refreshlayout.footer.ClassicsFooter;
import com.example.refreshlayout.header.ClassicsHeader;
import com.example.refreshlayout.utils.DensityUtils;
import com.example.refreshlayout.utils.ScrollBoundaryUtil;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by JessYuan on 06/07/2017.
 */
public class RefreshLayout extends ViewGroup {

    private static final String TAG = "RefreshLayout";

    private Context mContext;

    private View header;
    private View content;
    private View footer;

    private int mTouchSlop;
    private int mScrollY = 0;

    private boolean mIsBeingDraggedUp = false;
    private boolean mIsBeingDraggedDown = false;
    private boolean mRefreshing = false;
    private boolean mLoading = false;
    private boolean mDownReleasing = false;
    private boolean mUpReleasing = false;
    private boolean mLoadFinished = false;
    private boolean mRefreshFinished = false;
    private boolean mEnableRefresh = true;
    private boolean mEnableLoad = true;

    private int mActivePointerId = -1;

    private float mInitialDownY = 0;
    private float mInitialMotionY = 0;

    private int mHeaderHeight = 0;
    private int mHeaderMaxHeight = 0;
    private int mFooterHeight = 0;
    private int mFooterMaxHeight = 0;
    private int mMaxHeight = 0;
    private int mHeight = 0;

    private LoadMoreListener mLoadMoreListener;
    private PullToRefreshListener mRefreshListener;

    private RefreshFooter mRefreshFooter;
    private RefreshHeader mRefreshHeader;

    private RefreshState mState = RefreshState.None;

    @Override
    public void focusableViewAvailable(View v) {
        super.focusableViewAvailable(v);
    }

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        mHeaderHeight = DensityUtils.dp2px(100);
        mFooterHeight = DensityUtils.dp2px(100);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (header == null) {
//            header = LayoutInflater.from(mContext).inflate(R.layout.header, null);
            header = new ClassicsHeader(mContext);
            if (header instanceof RefreshHeader) {
                mRefreshHeader = (RefreshHeader) header;
            } else {
                mRefreshHeader = new RefreshHeaderWrapper(header);
            }
        }
        addView(mRefreshHeader.getView());

        if (footer == null) {
            footer = new ClassicsFooter(mContext);
            if (footer instanceof RefreshFooter) {
                mRefreshFooter = (RefreshFooter) footer;
            } else {
                mRefreshFooter = new RefreshFooterWrapper(footer);
            }
        }
        addView(mRefreshFooter.getView());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (content == null) {
                View view = getChildAt(i);
                if (view instanceof AbsListView
                    || view instanceof WebView
                    || view instanceof ScrollView
                    || view instanceof ScrollingView
                    || view instanceof NestedScrollingChild
                    || view instanceof NestedScrollingParent
                    || view instanceof ViewPager) {
                    content = view;
                }
            } else if (header == null && i == 0) {
                header = getChildAt(i);
                if (header instanceof RefreshHeader) {
                    mRefreshHeader = (RefreshHeader) header;
                } else {
                    mRefreshHeader = new RefreshHeaderWrapper(header);
                }
            } else if (footer == null && i == 2) {
                footer = getChildAt(i);
                if (footer instanceof RefreshFooter) {
                    mRefreshFooter = (RefreshFooter) footer;
                } else {
                    mRefreshFooter = new RefreshFooterWrapper(footer);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (header != null) {
            LayoutParams layoutParams = (LayoutParams) header.getLayoutParams();
            int widthSpec = getChildMeasureSpec(widthMeasureSpec, layoutParams.leftMargin + layoutParams.rightMargin,
                layoutParams.width);
            int heightSpec = heightMeasureSpec;

            // 设置header高度
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightSpec = makeMeasureSpec(mHeaderHeight, EXACTLY);
                header.measure(widthSpec, heightSpec);
            } else if (layoutParams.height > 0) {
                heightSpec = makeMeasureSpec(layoutParams.height, EXACTLY);
                header.measure(widthSpec, heightSpec);
            } else if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - layoutParams.bottomMargin, 0), AT_MOST);
                header.measure(widthSpec, heightSpec);
                int measuredHeight = header.getMeasuredHeight();
                if (mHeaderHeight <= 0) {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - layoutParams.bottomMargin, 0), EXACTLY);
                    header.measure(widthSpec, heightSpec);
                }
            }

            mHeaderHeight = header.getMeasuredHeight();
            mHeaderMaxHeight = (int) (header.getMeasuredHeight() * 1.5f);
        }

        if (footer != null) {
            LayoutParams layoutParams = (LayoutParams) footer.getLayoutParams();
            int widthSpec = getChildMeasureSpec(widthMeasureSpec, layoutParams.leftMargin + layoutParams.rightMargin,
                layoutParams.width);
            int heightSpec = heightMeasureSpec;

            // 设置header高度
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightSpec = makeMeasureSpec(mFooterHeight, EXACTLY);
                footer.measure(widthSpec, heightSpec);
            } else if (layoutParams.height > 0) {
                heightSpec = makeMeasureSpec(layoutParams.height, EXACTLY);
                footer.measure(widthSpec, heightSpec);
            } else if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - layoutParams.bottomMargin, 0), AT_MOST);
                footer.measure(widthSpec, heightSpec);
                int measuredHeight = footer.getMeasuredHeight();
                if (mFooterHeight <= 0) {
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - layoutParams.bottomMargin, 0), EXACTLY);
                    footer.measure(widthSpec, heightSpec);
                }
            }

            mFooterHeight = footer.getMeasuredHeight();
            mFooterMaxHeight = (int) (footer.getMeasuredHeight() * 1.5f);
        }

        if (content != null) {
            // 设置中间内容的高度
            LayoutParams layoutParams = (LayoutParams) content.getLayoutParams();
            int widthSpec = getChildMeasureSpec(widthMeasureSpec, layoutParams.leftMargin + layoutParams.rightMargin,
                layoutParams.width);
            int heightSpec = getChildMeasureSpec(heightMeasureSpec, layoutParams.topMargin + layoutParams.bottomMargin,
                layoutParams.height);
            content.measure(widthSpec, heightSpec);
        }

        setMeasuredDimension(getSize(widthMeasureSpec),
            getSize(heightMeasureSpec));

        Log.i(TAG, "onMeasure: \n"
            + "header: " + header.getMeasuredWidth() + " " + header.getMeasuredHeight() + "\n"
            + "content: " + content.getMeasuredWidth() + " " + content.getMeasuredHeight() + "\n"
            + "footer: " + footer.getMeasuredWidth() + " " + footer.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (content != null) {
            LayoutParams lp = (LayoutParams) content.getLayoutParams();
            int l = getPaddingLeft() + lp.leftMargin;
            int t = getPaddingTop() + lp.topMargin;
            int r = l + content.getMeasuredWidth();
            int b = t + content.getMeasuredHeight();
            content.layout(l, t, r, b);
            content.bringToFront();
        }

        if (header != null) {
            LayoutParams lp = (LayoutParams) header.getLayoutParams();
            int l = getPaddingLeft() + lp.leftMargin;
            int t = getPaddingTop() + lp.topMargin - header.getMeasuredHeight();
            int r = l + header.getMeasuredWidth();
            int b = t + header.getMeasuredHeight();

            header.layout(l, t, r, b);
        }

        if (footer != null) {
            LayoutParams lp = (LayoutParams) footer.getLayoutParams();
            int posLeft = getPaddingLeft() + lp.leftMargin;
            int posTop = getPaddingTop() + lp.topMargin + getMeasuredHeight();
            int posRight = posLeft + footer.getMeasuredWidth();
            int posBottom = posTop + footer.getMeasuredHeight();

            footer.layout(posLeft, posTop, posRight, posBottom);
        }

        Log.i(TAG, "onLayout: \n"
            + "header: " + header.getTop() + " " + header.getBottom() + "\n"
            + "content: " + content.getTop() + " " + content.getBottom() + "\n"
            + "footer: " + footer.getTop() + " " + footer.getBottom());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onInterceptTouchEvent");

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;

        if ((ScrollBoundaryUtil.canScrollUp(content, ev) && ScrollBoundaryUtil.canScrollDown(content, ev))) {
            Log.i(TAG, "onInterceptTouchEvent不拦截");
            return false;
        }

        Log.i(TAG, "onInterceptTouchEvent拦截");

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDraggedUp = false;
                mIsBeingDraggedDown = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == -1) {
                    Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                final float dy = y - mInitialDownY;
                if (Math.abs(dy) > mTouchSlop) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    if (dy > 0 && !ScrollBoundaryUtil.canScrollUp(content, ev) && mEnableRefresh ) {
                        mIsBeingDraggedDown = true;
                    } else if (dy < 0 && !ScrollBoundaryUtil.canScrollDown(content, ev) && mEnableLoad) {
                        mIsBeingDraggedUp = true;
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDraggedDown = false;
                mIsBeingDraggedUp = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDraggedDown || mIsBeingDraggedUp;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onTouchEvent");

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;

        if (ScrollBoundaryUtil.canScrollUp(content, ev) && ScrollBoundaryUtil.canScrollDown(content, ev)) {
            Log.i(TAG, "onTouchEvent没消费");
            return false;
        }

        Log.i(TAG, "onTouchEvent消费");

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDraggedUp = false;
                mIsBeingDraggedDown = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                final float dy = y - mInitialDownY;
                if (Math.abs(dy) > mTouchSlop) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    if (dy > 0 && !ScrollBoundaryUtil.canScrollUp(content, ev) && mEnableRefresh) {
                        mIsBeingDraggedDown = true;
                    } else if (dy < 0 && !ScrollBoundaryUtil.canScrollDown(content, ev) && mEnableLoad) {
                        mIsBeingDraggedUp = true;
                    }
                }

                if (mIsBeingDraggedUp || mIsBeingDraggedDown) {
                    final float overscrollTop = (y - mInitialMotionY) * 0.5f;
                    if (overscrollTop > 0 && overscrollTop <= mHeaderMaxHeight) {
                        if (overscrollTop > mHeaderHeight) {
                            notifyStateChanged(RefreshState.ReleaseToRefresh);
                        } else {
                            notifyStateChanged(RefreshState.PullDownToRefresh);
                        }
                        moveContent((int) overscrollTop);
                    } else if (overscrollTop < 0 && Math.abs(overscrollTop) <= mFooterMaxHeight) {
                        if (Math.abs(overscrollTop) >= mFooterHeight) {
                            notifyStateChanged(RefreshState.ReleaseToLoad);
                        } else {
                            notifyStateChanged(RefreshState.PullDownToRefresh);
                        }
                        moveContent((int) overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(TAG,
                        "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDraggedUp || mIsBeingDraggedDown) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * 0.5f;
                    release((int) overscrollTop);
                    mIsBeingDraggedUp = false;
                    mIsBeingDraggedDown = false;
                }
                mActivePointerId = INVALID_POINTER;
                return false;
        }

        return true;
    }

    /* 移动中间的内容（包括了头部和尾部） */
    private void moveContent(int scrollY) {
        if (mScrollY == scrollY) {
            return;
        }
        mScrollY = scrollY;

        content.setTranslationY(scrollY);
        header.setTranslationY(scrollY);
        footer.setTranslationY(scrollY);

        if (mIsBeingDraggedDown && !mIsBeingDraggedUp) { // 下拉更新时
            float scale = (float) Math.abs(scrollY) / header.getMeasuredHeight();

            mRefreshHeader.onPullingDown(scale, Math.abs(scrollY));
        } else if (mIsBeingDraggedUp && !mIsBeingDraggedDown) { // 上拉加载更多
            float scale = (float) Math.abs(scrollY) / footer.getMeasuredHeight();
            mRefreshFooter.onPullingUp(scale, Math.abs(scrollY));
        }
    }

    /* 手释放时 */
    private void release(int scrollY) {
        if (mIsBeingDraggedDown && !mIsBeingDraggedUp) { // 下拉更新时
            mMaxHeight = mHeaderMaxHeight;
            mHeight = header.getMeasuredHeight();
            mDownReleasing = true;

            if (mState == RefreshState.PullDownToRefresh && scrollY >= mHeaderHeight) {
                mRefreshing = true;
                notifyStateChanged(RefreshState.ReleaseToRefresh);
            }
        } else if (mIsBeingDraggedUp && !mIsBeingDraggedDown) { // 上拉加载更多
            mMaxHeight = -mFooterMaxHeight;
            mHeight = -footer.getMeasuredHeight();
            mUpReleasing = true;

            if (mState == RefreshState.PullToUpLoad && scrollY <= -mFooterHeight) {
                mLoading = true;
                notifyStateChanged(RefreshState.ReleaseToLoad);
            }
        }

        animator(scrollY, mMaxHeight, mHeight);

        if (mState == RefreshState.ReleaseToRefresh) {
            notifyStateChanged(RefreshState.Refreshing);
            if (mRefreshListener != null) {
                mRefreshListener.refresh();
            }
        } else if (mState == RefreshState.ReleaseToLoad) {
            notifyStateChanged(RefreshState.Loading);
            if (mLoadMoreListener != null) {
                mLoadMoreListener.loadmore();
            }
        }
    }

    private void animator(int scroll, int maxHeight, int height) {
        if (Math.abs(scroll) >= Math.abs(maxHeight)) {
            generateAnimator(300, maxHeight, height).start();
        } else if (Math.abs(scroll) >= Math.abs(height)) {
            generateAnimator(300, scroll, height).start();
        } else {
            generateAnimator(800, scroll, 0).start();
        }
    }

    /* 在加载完成或刷新完成后的那步伸缩回去的动画 */
    private void finished() {
        generateAnimator(800, mHeight, 0).start();
        mMaxHeight = 0;
        mHeight = 0;
    }

    private Animator generateAnimator(int duration, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (mDownReleasing) {
                    float scale = value / (float) header.getMeasuredHeight();
                    mRefreshHeader.onReleasing(scale, value);
                } else if (mUpReleasing) {
                    float scale = value / (float) footer.getMeasuredHeight();
                    mRefreshFooter.onReleasing(Math.abs(scale), value);
                }

                if (value == 0 && (mDownReleasing || mUpReleasing)) {
                    mDownReleasing = false;
                    mUpReleasing = false;
                    notifyStateChanged(RefreshState.None);
                }
                moveContent((int) animation.getAnimatedValue());
            }
        });

        return animator;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /* 改变刷新或加载时的状态 */
    private void notifyStateChanged(RefreshState state) {
        final RefreshState oldState = mState;
        if (oldState != state) {
            mState = state;
            if (mRefreshFooter != null) {
                mRefreshFooter.onStateChanged(this, oldState, state);
            }
            if (mRefreshHeader != null) {
                mRefreshHeader.onStateChanged(this, oldState, state);
            }
        }
    }

    /* 设置是否可以刷新 */
    public void setEnableRefresh(boolean enable) {
        mEnableRefresh = enable;
    }

    /* 设置是否可以加载 */
    public void setEnableLoad(boolean enable) {
        mEnableLoad = enable;
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setRefreshListener(PullToRefreshListener listener) {
        mRefreshListener = listener;
    }

    /**
     * 设置是否加载完成
     * @param loaded 是否加载完成
     * @param success 是否加载成功
     */
    public void setLoaded(boolean loaded, boolean success) {
        mLoading = !loaded;
        mLoadFinished = loaded;
        notifyStateChanged(RefreshState.LoadingFinish);
        mRefreshFooter.loadFinished(success);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                finished();
            }
        },1000);
    }


    /**
     * 设置是否刷新完成
     * @param refreshed 是否刷新完成
     * @param success 是否刷新成功
     */
    public void setRefreshed(boolean refreshed,boolean success) {
        mRefreshing = !refreshed;
        mRefreshFinished = refreshed;
        notifyStateChanged(RefreshState.RefreshFinish);
        mRefreshHeader.RefreshFinished(success);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                finished();
            }
        },1000);
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RefreshLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface PullToRefreshListener {
        void refresh();
    }

    public interface LoadMoreListener {
        void loadmore();
    }
}