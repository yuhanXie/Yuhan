package com.example.yuhan.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.example.yuhan.R;


/**
 * 滑动TabLayout,对于ViewPager的依赖性强
 */
public class AdvancedTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mTabItem;
    private float mCurrentPositionOffset;
    private int mTabIndex;
    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private Rect mTabRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    private float mTabPadding;
    private boolean mTabModeFixed;

    /**
     * indicator
     */
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;

    private float mTextSize;
    private int mTextSelectedColor;
    private int mTextColor;
    private boolean mTextBold;
    private boolean mTextAllCaps;
    private int mLastScrollX;


    private SparseArray<Float> mLeavedPercents = new SparseArray<Float>();

    private int mCurrentIndex;
    private int mLastIndex;
    private float mLastPositionOffsetSum;
    private int mScrollState;
    private float mMaxTextScale = 1.0f;


    private int[] colors = new int[]{0xFFFFEE31, 0xFFFFD228};


    public AdvancedTabLayout(Context context) {
        this(context, null, 0);
    }

    public AdvancedTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvancedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);//设置滚动视图是否可以伸缩其内容以填充视口
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabItem = new LinearLayout(context);
        addView(mTabItem);

        obtainAttributes(context, attrs);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AdvancedTabLayout);

        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorHeight, 0);
        mIndicatorWidth = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorWidth, 0);
        mIndicatorCornerRadius = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorCornerRadius, 0);
        mIndicatorMarginLeft = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorMarginLeft, 0);
        mIndicatorMarginTop = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorMarginTop, 10);
        mIndicatorMarginRight = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorMarginRight, 0);
        mIndicatorMarginBottom = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabIndicatorMarginBottom, 0);

        mTextSize = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabTextSize, 0);
        mTextSelectedColor = ta.getColor(R.styleable.AdvancedTabLayout_advancedTabTextColor, Color.parseColor("#ffffff"));
        mTextColor = ta.getColor(R.styleable.AdvancedTabLayout_advancedTabSelectedTextColor, Color.parseColor("#AAffffff"));
        mTextBold = ta.getBoolean(R.styleable.AdvancedTabLayout_advancedTabTextBold, false);
        mTextAllCaps = ta.getBoolean(R.styleable.AdvancedTabLayout_advancedTabTextAllCaps, false);

        mTabModeFixed = ta.getBoolean(R.styleable.AdvancedTabLayout_advancedTabModeFixed, false);
        mTabPadding = ta.getDimensionPixelSize(R.styleable.AdvancedTabLayout_advancedTabPadding, mTabModeFixed ? 0 : 10);
        mMaxTextScale = ta.getFloat(R.styleable.AdvancedTabLayout_advancedTabTextMaxScale, 1.0f);

        ta.recycle();
    }

    /**
     * 关联ViewPager
     */
    public void setupWithViewPager(ViewPager viewPager) {
        if (viewPager == null || viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }

        this.mViewPager = viewPager;

        this.mViewPager.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mTabItem.removeAllViews();
        mTabCount = mViewPager.getAdapter().getCount();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = View.inflate(mContext, R.layout.foundation_tab_layout_tab_item, null);
            addTab(i, mViewPager.getAdapter().getPageTitle(i).toString(), tabView);
        }
        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, String title, View tabView) {
        TextView tvTitle = (TextView) tabView.findViewById(R.id.tv_tab_title);
        if (tvTitle != null) {
            if (title != null) {
                tvTitle.setText(title);
            }
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mTabItem.indexOfChild(v);
                if (position != -1) {
                    if (mViewPager.getCurrentItem() != position) {
                        mViewPager.setCurrentItem(position);
                        if (mListener != null) {
                            mListener.onTabSelected(position);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onTabReselected(position);
                        }
                    }
                }
            }
        });

        /*
         * 每一个Tab的布局参数
         */
        LinearLayout.LayoutParams lp = mTabModeFixed ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        mTabItem.addView(tabView, position, lp);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View view = mTabItem.getChildAt(i);
            view.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);
            if (tvTitle != null) {
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                if (mTextAllCaps) {
                    tvTitle.setText(tvTitle.getText().toString().toUpperCase());
                }

                tvTitle.getPaint().setFakeBoldText(mTextBold);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /*
         * position:当前View的位置
         * mCurrentPositionOffset:当前View的偏移量比例.[0,1)
         */

        this.mTabIndex = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();

        float currentPositionOffsetSum = position + positionOffset;
        boolean leftToRight = false;
        if (mLastPositionOffsetSum <= currentPositionOffsetSum) {
            leftToRight = true;
        }
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE) {
            if (currentPositionOffsetSum == mLastPositionOffsetSum) {
                return;
            }
            int nextPosition = position + 1;
            boolean normalDispatch = true;
            if (positionOffset == 0.0f) {
                if (leftToRight) {
                    nextPosition = position - 1;
                    normalDispatch = false;
                }
            }
            for (int i = 0; i < mTabCount; i++) {
                if (i == position || i == nextPosition) {
                    continue;
                }
                Float leavedPercent = mLeavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight, true);
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, true, false);
                    dispatchOnEnter(nextPosition, positionOffset, true, false);
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, false, false);
                    dispatchOnEnter(position, 1.0f - positionOffset, false, false);
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, true, false);
                dispatchOnEnter(position, 1.0f - positionOffset, true, false);
            }
        } else {
            for (int i = 0; i < mTabCount; i++) {
                if (i == mCurrentIndex) {
                    continue;
                }
                Float leavedPercent = mLeavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, false, true);
                }
            }
            dispatchOnEnter(mCurrentIndex, 1.0f, false, true);
        }
        mLastPositionOffsetSum = currentPositionOffsetSum;
    }

    @Override
    public void onPageSelected(int position) {
        updateTabSelection(position);

        mLastIndex = mCurrentIndex;
        mCurrentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
    }


    private void dispatchOnEnter(int index, float enterPercent, boolean leftToRight, boolean force) {
        if (index == mCurrentIndex || mScrollState == ViewPager.SCROLL_STATE_DRAGGING || force) {
            onEnter(index, enterPercent);
            mLeavedPercents.put(index, 1.0f - enterPercent);
        }
    }

    private void dispatchOnLeave(int index, float leavePercent, boolean leftToRight, boolean force) {
        if (index == mLastIndex || mScrollState == ViewPager.SCROLL_STATE_DRAGGING || ((index == mCurrentIndex - 1
                || index == mCurrentIndex + 1) && mLeavedPercents.get(index, 0.0f) != 1.0f) || force) {
            onLeave(index, leavePercent);
        }
        mLeavedPercents.put(index, leavePercent);
    }

    public void onLeave(int index, float leavePercent) {
        View view = mTabItem.getChildAt(index);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int color = (int) evaluator.evaluate(leavePercent, mTextColor, mTextSelectedColor);
        tvTitle.setTextColor(color);

        if (mMaxTextScale > 1f) {
            view.setPivotX(view.getMeasuredWidth() / 2);
            view.setPivotY(view.getMeasuredHeight() - 10);
            view.setScaleX(mMaxTextScale + (1f - mMaxTextScale) * leavePercent);
            view.setScaleY(mMaxTextScale + (1f - mMaxTextScale) * leavePercent);
        }
    }

    public void onEnter(int index, float enterPercent) {
        View view = mTabItem.getChildAt(index);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int color = (int) evaluator.evaluate(enterPercent, mTextSelectedColor, mTextColor);
        tvTitle.setTextColor(color);

        //Log.e("onEnter", view.getMeasuredHeight() + "----->" + view.getMeasuredWidth());
        if (mMaxTextScale > 1f) {
            view.setPivotX(view.getMeasuredWidth() / 2);
            view.setPivotY(view.getMeasuredHeight() - 10);
            view.setScaleX(1f + (mMaxTextScale - 1f) * enterPercent);
            view.setScaleY(1f + (mMaxTextScale - 1f) * enterPercent);
        }
    }


    /**
     * HorizontalScrollView滚到当前tab,并且居中显示
     */
    private void scrollToCurrentTab() {
        if (mTabCount <= 0) {
            return;
        }

        int offset = (int) (mCurrentPositionOffset * mTabItem.getChildAt(mTabIndex).getWidth());
        /*当前Tab的left+当前Tab的Width乘以positionOffset*/
        int newScrollX = mTabItem.getChildAt(mTabIndex).getLeft() + offset;

        if (mTabIndex > 0 || offset > 0) {
            /*HorizontalScrollView移动到当前tab,并居中*/
            newScrollX -= getWidth() / 2 - getPaddingLeft();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            /* scrollTo（int x,int y）:x,y代表的不是坐标点,而是偏移量
             *  x:表示离起始位置的x水平方向的偏移量
             *  y:表示离起始位置的y垂直方向的偏移量
             */
            scrollTo(newScrollX, 0);
        }
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabItem.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tvTitle = (TextView) tabView.findViewById(R.id.tv_tab_title);

            if (tvTitle != null) {
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                tvTitle.getPaint().setFakeBoldText(mTextBold);
            }
        }
    }

    private void calcIndicatorRect() {
        View currentTabView = mTabItem.getChildAt(this.mTabIndex);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        if (this.mTabIndex < mTabCount - 1) {
            View nextTabView = mTabItem.getChildAt(this.mTabIndex + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            left = left + mCurrentPositionOffset * (nextTabLeft - left);
            right = right + mCurrentPositionOffset * (nextTabRight - right);
        }

        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;

        if (this.mTabIndex < mTabCount - 1) {
            View nextTab = mTabItem.getChildAt(this.mTabIndex + 1);
            indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
        }

        mIndicatorRect.left = (int) indicatorLeft;
        mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);

        mIndicatorRect.top = currentTabView.getBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int paddingLeft = getPaddingLeft();

        calcIndicatorRect();

        if (mIndicatorHeight > 0) {
            mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                    (int) (getPaddingTop() + mIndicatorMarginTop + mIndicatorRect.top),
                    paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                    (int) (getPaddingTop() + mIndicatorMarginTop + mIndicatorRect.top + mIndicatorHeight));
            mIndicatorDrawable.setColors(colors);
            mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
            mIndicatorDrawable.draw(canvas);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        //所有子view加起来总的Measured Dimension高度和宽度  
        int measuredHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != GONE) {
                //measureChild(view, widthMeasureSpec, heightMeasureSpec);
                measuredHeight += view.getMeasuredHeight();
            }
        }

        measuredHeight += getPaddingTop() + getPaddingBottom() + mIndicatorHeight;
        setMeasuredDimension(specWidthSize, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        if (mTabItem == null) {
            return;
        }
//        ImageView ivDot = (ImageView) mTabItem.getChildAt(position).findViewById(R.id.iv_tab_dot);
//        ivDot.setVisibility(VISIBLE);
    }

    /**
     * 隐藏未读红点
     *
     * @param position 显示tab位置
     */
    public void hideDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        if (mTabItem == null) {
            return;
        }
//        ImageView ivDot = (ImageView) mTabItem.getChildAt(position).findViewById(R.id.iv_tab_dot);
//        ivDot.setVisibility(GONE);
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mTabIndex", mTabIndex);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mTabIndex = bundle.getInt("mTabIndex");
            state = bundle.getParcelable("instanceState");
            if (mTabIndex != 0 && mTabItem.getChildCount() > 0) {
                updateTabSelection(mTabIndex);
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
    }

}
