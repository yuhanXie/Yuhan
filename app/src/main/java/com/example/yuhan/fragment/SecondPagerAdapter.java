package com.example.yuhan.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author xiegang
 */
public class SecondPagerAdapter extends FragmentPagerAdapter {


    public SecondPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Test1Fragment();
            case 1:
                return new Test2Fragment();
            case 2:
                return new Test3Fragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "推荐";
            case 1:
                return "关注";
            case 2:
                return "附近";
            default:
                return "";
        }
    }
}
