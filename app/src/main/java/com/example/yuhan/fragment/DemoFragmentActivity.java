package com.example.yuhan.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.yuhan.R;

/**
 * @author xiegang
 */
public class DemoFragmentActivity extends FragmentActivity implements View.OnClickListener {


    private RelativeLayout mRlyMid;
    private TextView mTvFirst;
    private TextView mTvSecond;
    private RelativeLayout mTvThird;
    private RelativeLayout mTvForth;

    private FirstFragment mFirstFragment;
    private SecondFragment mSecondFragment;
    private ThirdFragment mThirdFragment;
    private FourthFragment mFourthFragment;


    //当前选中的fragment
    private Fragment mSelectedFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_fragment);
        mRlyMid = findViewById(R.id.rly_mid);
        mTvFirst = findViewById(R.id.tv_bottom_first);
        mTvSecond = findViewById(R.id.tv_bottom_second);
        mTvThird = findViewById(R.id.rly_bottom_message);
        mTvForth = findViewById(R.id.rly_bottom_mine);

        mTvFirst.setOnClickListener(this);
        mTvSecond.setOnClickListener(this);
        mTvThird.setOnClickListener(this);
        mTvForth.setOnClickListener(this);

        if (null == mFirstFragment) {
            mFirstFragment = new FirstFragment();
        }
        switchFragment(mSelectedFragment, mFirstFragment, "first");
    }

    /**
     * fragment 切换
     *
     * @param from from
     * @param to   to
     * @param tag  tag
     */
    private synchronized void switchFragment(Fragment from, Fragment to, String tag) {
        if (from == to) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (from == null || !from.isAdded()) {
            if (!to.isAdded() && null == manager.findFragmentByTag(tag)) {
                transaction.add(R.id.rly_mid, to, tag).commitAllowingStateLoss();
            } else {
                transaction.show(to).commitAllowingStateLoss();
            }
        } else {
            if (!to.isAdded() && null == manager.findFragmentByTag(tag)) {
                transaction.hide(from).add(R.id.rly_mid, to, tag).commitAllowingStateLoss();
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss();
            }
        }
        mSelectedFragment = to;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bottom_first:
                if (null == mFirstFragment) {
                    mFirstFragment = new FirstFragment();
                }
                switchFragment(mSelectedFragment, mFirstFragment, "first");
                break;
            case R.id.tv_bottom_second:
                if (null == mSecondFragment) {
                    mSecondFragment = new SecondFragment();
                }
                switchFragment(mSelectedFragment, mSecondFragment, "second");
                break;
            case R.id.rly_bottom_message:
                if (null == mThirdFragment) {
                    mThirdFragment = new ThirdFragment();
                }
                switchFragment(mSelectedFragment, mThirdFragment, "third");
                break;
            case R.id.rly_bottom_mine:
                if (null == mFourthFragment) {
                    mFourthFragment = new FourthFragment();
                }
                switchFragment(mSelectedFragment, mFourthFragment, "fourth");
                break;
            default:
                break;
        }
    }
}
