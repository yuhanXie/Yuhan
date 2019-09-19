package com.example.yuhan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.yuhan.R;
import com.example.yuhan.widget.AdvancedTabLayout;

/**
 * @author xiegang
 */
public class SecondFragment extends BaseFragment {

    private AdvancedTabLayout mTabLayout;

    private ViewPager mViewPage;

    private SecondPagerAdapter pagerAdapter;
    @Override
    protected void setTag() {
        super.setTag();
        TAG = "SecondFragment";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPage = view.findViewById(R.id.view_pager);
        mViewPage.setOffscreenPageLimit(2);
        pagerAdapter = new SecondPagerAdapter(getFragmentManager());
        mViewPage.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPage);
    }

    private boolean isFirstLoad = true;

//    @Override
//    protected void onVisible() {
//        super.onVisible();
//        Fragment fragment = pagerAdapter.getItem(mViewPage.getCurrentItem());
//        if (fragment instanceof BaseFragment) {
//            if (!isFirstLoad) {
//                //第一次不加载
//                ((BaseFragment) fragment).onVisible();
//                isFirstLoad = false;
//            }
//        }
//    }
//
//    @Override
//    protected void onHidden() {
//        super.onHidden();
//        Fragment fragment = pagerAdapter.getItem(mViewPage.getCurrentItem());
//        if (fragment instanceof BaseFragment) {
//            ((BaseFragment) fragment).onHidden();
//        }
//    }
}
