package com.example.yuhan.layoutmanager;

import android.graphics.Rect;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yuhan.utils.DisplayUtils;
import com.example.yuhan.utils.LogUtils;

/**
 * 正反方布局
 *
 * @author xiegang
 */
public class CustomV2LayoutManager extends RecyclerView.LayoutManager {

    /**
     * 记录item的位置
     */
    private SparseArray<Rect> mItemRectList = null;

    /**
     * 记录item高度
     */
    private SparseIntArray mItemHeightList = null;

    /**
     * 记录item在左右的位置,true为左
     */
    private SparseBooleanArray mItemPositionList = null;

    private int mTotalHeight = -1;//总的itemView的总高度
    private int mScrollOffset = 0;
    /**
     * 左边布局数量
     */
    private int mLeftItemCount;


    public void setLeftItemCount(int leftItemCount) {
        mLeftItemCount = leftItemCount;
        requestLayout();
    }

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
        detachAndScrapAttachedViews(recycler);
        initStepHeight(recycler);
//        mVisibleCount = initVisibleCount(mItemHeight);
        initItemRectSparse();
        layoutChild(recycler);
    }


    /**
     * 记录所有子View的高度
     *
     * @param recycler recycler
     */
    private void initStepHeight(RecyclerView.Recycler recycler) {
        mItemHeightList = new SparseIntArray();
        for (int i = 0; i < getItemCount(); i++) {
            //从缓存中获取childView
            View childView = recycler.getViewForPosition(i);
            addView(childView);
            //计算view的宽高
            measureChildWithMargins(childView, 0, 0);
            int height = getDecoratedMeasuredHeight(childView);
            mItemHeightList.put(i, height);
        }

        //将所有item移到缓存池
        removeAndRecycleAllViews(recycler);
    }

    /**
     * 计算总高度，记录每个item的位置
     */
    private void initItemRectSparse() {
        mItemRectList = new SparseArray<>();
        mItemPositionList = new SparseBooleanArray();
        mTotalHeight = getPaddingTop();

        //注意recycleView不要设置左右padding
        if (mLeftItemCount == 0) {
            //布局全部在右边
            for (int i = 0; i < getItemCount(); i++) {
                int height = mItemHeightList.get(i);
                mItemRectList.put(i, new Rect(DisplayUtils.getScreenWidth() / 2, mTotalHeight,
                        DisplayUtils.getScreenWidth(), mTotalHeight + height));
                mTotalHeight += height;
            }
        } else if (mLeftItemCount == getItemCount()) {
            //布局全部在左边
            for (int i = 0; i < getItemCount(); i++) {
                int height = mItemHeightList.get(i);
                mItemRectList.put(i, new Rect(0, mTotalHeight,
                        DisplayUtils.getScreenWidth() / 2, mTotalHeight + height));
                mTotalHeight += height;
            }
        } else {
            mLeftHeight = getPaddingTop();
            mRightHeight = getPaddingTop();
            for (int i = 0; i < getItemCount(); i++) {
                int height = mItemHeightList.get(i);
                if (i % 2 == 0) {
                    if (i <= (mLeftItemCount - 1) * 2) {
                        //左边
                        mItemRectList.put(i, new Rect(0, mLeftHeight,
                                DisplayUtils.getScreenWidth() / 2, mLeftHeight + height));
                        mLeftHeight += height;
                        mItemPositionList.put(i, true);
                    } else {
                        //右边
                        mItemRectList.put(i, new Rect(DisplayUtils.getScreenWidth() / 2, mRightHeight,
                                DisplayUtils.getScreenWidth(), mRightHeight + height));
                        mRightHeight += height;
                        mItemPositionList.put(i, false);
                    }
                } else {
                    if (i < (getItemCount() - mLeftItemCount) * 2) {
                        //右边
                        mItemRectList.put(i, new Rect(DisplayUtils.getScreenWidth() / 2, mRightHeight,
                                DisplayUtils.getScreenWidth(), mRightHeight + height));
                        mRightHeight += height;
                        mItemPositionList.put(i, false);
                    } else {
                        //左边
                        mItemRectList.put(i, new Rect(0, mLeftHeight,
                                DisplayUtils.getScreenWidth() / 2, mLeftHeight + height));
                        mLeftHeight += height;
                        mItemPositionList.put(i, true);
                    }
                }
            }
            mTotalHeight = mLeftHeight > mRightHeight ? mLeftHeight : mRightHeight;
        }
    }


    private int mLeftHeight = 0;
    private int mRightHeight = 0;
    private int mLeftTop = 0;
    private int mLeftBottom = -1;
    private int mRightTop = 0;
    private int mRightBottom = -1;

    private void layoutChild(RecyclerView.Recycler recycler) {
        //先缓存
        detachAndScrapAttachedViews(recycler);
        int i = 0;
        int tempLeftHeight = 0;
        int tempRightHeight = 0;
        boolean tempLeftIsFull = false;
        boolean tempRightIsFull = false;
        while (i < getItemCount() && !(tempLeftIsFull && tempRightIsFull)) {
            if (mItemPositionList.get(i) && !tempLeftIsFull) {
                //左边
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                //布局
                layoutDecorated(childView, mItemRectList.get(i).left, mItemRectList.get(i).top,
                        mItemRectList.get(i).right, mItemRectList.get(i).bottom);
                tempLeftHeight += mItemHeightList.get(i);
                if (tempLeftHeight >= getVerticalVisibleHeight()) {
                    tempLeftIsFull = true;
                }
                mLeftBottom = i;
            } else if (!tempRightIsFull) {
                //右边
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                //布局
                layoutDecorated(childView, mItemRectList.get(i).left, mItemRectList.get(i).top,
                        mItemRectList.get(i).right, mItemRectList.get(i).bottom);
                tempRightHeight += mItemHeightList.get(i);
                if (tempRightHeight >= getVerticalVisibleHeight()) {
                    tempRightIsFull = true;
                }
                mRightBottom = i;
            }
            i++;
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
        handleSpanRecycle(recycler);
    }

    private void handleSpanRecycle(RecyclerView.Recycler recycler) {
        int mCurLeftTop = -1;
        int mCurRightTop = -1;
        int mCurLeftBottom = -1;
        int mCurRightBottom = -1;

        int curLeftHeight = 0;
        int curRightHeight = 0;
        for (int i = 0; i < getItemCount(); i++) {
            int height = mItemHeightList.get(i);
            if (mItemPositionList.get(i)) {
                //左边
                curLeftHeight += height;
                if (mScrollOffset <= curLeftHeight && mScrollOffset >= curLeftHeight - height) {
                    mCurLeftTop = i;
                }
                if (mScrollOffset + DisplayUtils.getScreenHeight() <= curLeftHeight &&
                        mScrollOffset + DisplayUtils.getScreenHeight() >= curLeftHeight - height) {
                    mCurLeftBottom = i;
                }
            } else {
                curRightHeight += height;
                if (mScrollOffset <= curRightHeight && mScrollOffset >= curRightHeight - height) {
                    mCurRightTop = i;
                }
                if (mScrollOffset + DisplayUtils.getScreenHeight() <= curRightHeight &&
                        mScrollOffset + DisplayUtils.getScreenHeight() >= curRightHeight - height) {
                    mCurRightBottom = i;
                }
            }
        }

        if (mCurLeftBottom == -1) {
            //未执行，说明已展示完
            mCurLeftBottom = mLeftBottom;
        }

        if (mCurRightBottom == -1) {
            mCurRightBottom = mRightBottom;
        }

        LogUtils.log(mLeftTop + "======" + mLeftBottom);
        LogUtils.log(mCurLeftTop + "====" + mCurLeftBottom + "=====" + mCurRightTop + "=====" + mCurRightBottom);

        handle(recycler, mCurLeftTop, mCurLeftBottom, mLeftTop, mLeftBottom, true);
        handle(recycler, mCurRightTop, mCurRightBottom, mRightTop, mRightBottom, false);
        mLeftTop = mCurLeftTop;
        mLeftBottom = mCurLeftBottom;
        mRightTop = mCurRightTop;
        mRightBottom = mCurRightBottom;
    }


    private void handle(RecyclerView.Recycler recycler, int currentTop, int currentBottom, int lastTop, int lastBottom, boolean isLeft) {
        if (lastTop < currentTop) {
            //上滑
            for (int i = lastTop; i < currentTop; i++) {
                if (mItemPositionList.get(i) == isLeft) {
                    View childView = recycler.getViewForPosition(i);
                    removeAndRecycleView(childView, recycler);
                    LogUtils.log("=====remove=====" + i);
                }
            }
        } else if (lastTop > currentTop){
            //下滑
            for (int i = lastTop - 1; i >= currentTop; i--) {
                if (mItemPositionList.get(i) == isLeft) {
                    View childView = recycler.getViewForPosition(i);
                    Rect rect = mItemRectList.get(i);
                    addView(childView);
                    measureChildWithMargins(childView, 0, 0);
                    layoutDecorated(childView, rect.left, rect.top - mScrollOffset, rect.right,
                            rect.bottom - mScrollOffset);
                    LogUtils.log("=====add=====" + i);
                }
            }
        }

        if (lastBottom < currentBottom) {
            for (int i = lastBottom + 1; i <= currentBottom; i++) {
                if (mItemPositionList.get(i) == isLeft) {
                    View childView = recycler.getViewForPosition(i);
                    Rect rect = mItemRectList.get(i);
                    addView(childView);
                    measureChildWithMargins(childView, 0, 0);
                    layoutDecorated(childView, rect.left, rect.top - mScrollOffset, rect.right,
                            rect.bottom - mScrollOffset);
                    LogUtils.log("=====add=====" + i);
                }
            }
        } else if (lastBottom > currentBottom) {
            for (int i = lastBottom; i > currentBottom; i--) {
                if (mItemPositionList.get(i) == isLeft) {
                    View childView = recycler.getViewForPosition(i);
                    removeAndRecycleView(childView, recycler);
                    LogUtils.log("=====remove=====" + i);
                }
            }
        }
    }
}
