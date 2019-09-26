package com.example.yuhan.layoutmanager;

import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yuhan.utils.DisplayUtils;

/**
 * @author xiegang
 */
public class CustomLayoutManager extends RecyclerView.LayoutManager {

    private SparseArray<Rect> mItemRectList = null;
    private int mTotalHeight = -1;//总的itemView的总高度
    private int mScrollOffset = 0;
    private int mVisibleCount;

    //顶部item位置
    private int mTop = -1;
    //底部item位置
    private int mBottom = -1;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    private boolean cut(RecyclerView.State state) {
        return getItemCount() <= 0 || state.isPreLayout();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (cut(state)) {
            return;
        }
        //暂时地将所有子view detach和scrap（回收），view会被回收到回收池，回收池会优先重用scrap的子view
        detachAndScrapAttachedViews(recycler);
        //计算item的高度，此处默认item高度一致
        int mItemHeight = initStepHeight(recycler);
        mVisibleCount = initVisibleCount(mItemHeight);
        initItemRectSparse(mItemHeight);
        layoutChild(recycler, mVisibleCount);
    }


    private int initStepHeight(RecyclerView.Recycler recycler) {
        //从缓存中获取childView，只适用于item高度一致的情况
        View childView = recycler.getViewForPosition(0);
        addView(childView);
        //计算view的宽高
        measureChildWithMargins(childView, 0, 0);
        int height = getDecoratedMeasuredHeight(childView);
        //将所有item移到缓存池
        removeAndRecycleAllViews(recycler);
        return height;
    }

    /**
     * 计算总高度，记录每个item的位置
     *
     * @param stepItemHeight stepItemHeight
     */
    private void initItemRectSparse(int stepItemHeight) {
        mItemRectList = new SparseArray<>();
        int offsetY = getPaddingTop();
        for (int i = 0; i < getItemCount(); i++) {
            mItemRectList.put(i, new Rect(getPaddingLeft(), offsetY,
                    getWidth() + getPaddingRight(), offsetY + stepItemHeight));
            offsetY += stepItemHeight;
            mTotalHeight = offsetY;
        }
    }

    private void layoutChild(RecyclerView.Recycler recycler, int visibleCount) {
        mTop = 0;
        mBottom = visibleCount - 1;
        //先缓存
        detachAndScrapAttachedViews(recycler);
        for (int i = 0; i < visibleCount; i++) {
            View childView = recycler.getViewForPosition(i);
            addView(childView);
            measureChildWithMargins(childView, 0, 0);
            //布局
            layoutDecorated(childView, mItemRectList.get(i).left, mItemRectList.get(i).top,
                    mItemRectList.get(i).right, mItemRectList.get(i).bottom);
        }
    }

    private int initVisibleCount(int itemHeight) {
        double count = (double) getVerticalVisibleHeight() / (double) itemHeight;
        return getItemCount() > Math.ceil(count) ?
                (int) Math.ceil(count) : getItemCount();
    }

    private int getVerticalVisibleHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        dy = handleScroll(dy, recycler, state);
        handleRecycle(recycler, state, dy);
        return dy;
    }

    private int handleScroll(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mTotalHeight < DisplayUtils.getScreenHeight()) {
            return 0;
        }
        //列表向下滚动dy为正，列表向上滚动dy为负
        int theRvVisibleHeight = getVerticalVisibleHeight();
        int theMoreHeight = mTotalHeight - theRvVisibleHeight;
        if (mScrollOffset + dy < 0) {
            //滚动到了顶部
            dy = -mScrollOffset;
        } else if (mScrollOffset + dy > mTotalHeight - getVerticalVisibleHeight()) {
            dy = theMoreHeight - mScrollOffset;
        }

        mScrollOffset += dy;
        offsetChildrenVertical(-dy);
        return dy;
    }

    private void handleRecycle(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        if (cut(state) || dy == 0) {
            return;
        }

        int mCurTop = -1;

        for (int i = 0; i < getItemCount(); i++) {
            Rect rect = mItemRectList.get(i);
            if (mScrollOffset >= rect.top && mScrollOffset <= rect.bottom) {
                //当前top位置
                mCurTop = i;
                break;
            }
        }

        int mCurBottom = getItemCount() - 1 > (mCurTop + mVisibleCount) ? (mCurTop + mVisibleCount) : getItemCount() - 1;

        if (mTop == mCurTop) {
            return;
        }

        if (mTop < mCurTop) {
            //上滑
            for (int i = mTop; i < mCurTop; i++) {
                View childView = recycler.getViewForPosition(i);
                removeAndRecycleView(childView, recycler);
            }

            for (int i = mBottom + 1; i <= mCurBottom; i++) {
                View childView = recycler.getViewForPosition(i);
                Rect rect = mItemRectList.get(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                layoutDecorated(childView, rect.left, rect.top - mScrollOffset, rect.right,
                        rect.bottom - mScrollOffset);
            }
        } else {
            //下滑
            for (int i = mTop; i >= mCurTop; i--) {
                View childView = recycler.getViewForPosition(i);
                Rect rect = mItemRectList.get(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                layoutDecorated(childView, rect.left, rect.top - mScrollOffset, rect.right,
                        rect.bottom - mScrollOffset);
            }

            for (int i = mBottom; i > mCurBottom; i--) {
                View childView = recycler.getViewForPosition(i);
                removeAndRecycleView(childView, recycler);

            }
        }

        mTop = mCurTop;
        mBottom = mCurBottom;
    }
}
