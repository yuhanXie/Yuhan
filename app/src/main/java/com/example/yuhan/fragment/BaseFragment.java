package com.example.yuhan.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yuhan.utils.LogUtils;

/**
 * @author xiegang
 */
public abstract class BaseFragment extends Fragment {


    protected String TAG = "";

    private boolean mIsHidden = false;
    private boolean mIsUserVisibleHint = true;

    //viewPager内第一次加载fragment，会执行setUserVisibleHint和onResume，字段防止onVisible（）执行两遍
    private boolean mIsExecuteOnVisible = false;

    //viewPager 预加载默认会执行一次setUserVisibleHint，初始第一次都为false，避免执行onHidden方法
    private boolean mIsFirstUserVisibleHint = true;

    /**
     * 目前只有在底部tab切换到viewPager内fragment时， onVisible（）不会执行，其余时机皆可
     */
    protected void onVisible(){
        if (TextUtils.isEmpty(TAG)) {
            setTag();
        }
        LogUtils.log("======="+TAG+"===onVisible====");
    }

    protected void onHidden() {
        if (TextUtils.isEmpty(TAG)) {
            setTag();
        }
        LogUtils.log("======="+TAG+"===onHidden====");
    }

    @Override
    public void onResume() {
        if (getUserVisibleHint() && !mIsHidden && !mIsExecuteOnVisible) {
            onVisible();
        }
        if (mIsExecuteOnVisible) {
            mIsExecuteOnVisible = false;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mIsHidden && mIsUserVisibleHint) {
            onHidden();
        }
        mIsExecuteOnVisible = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onHidden();
        } else {
            onVisible();
        }
        mIsHidden = hidden;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsUserVisibleHint = isVisibleToUser;
        if (TextUtils.isEmpty(TAG)) {
            setTag();
        }
        if (isVisibleToUser) {
            if (!mIsExecuteOnVisible) {
                mIsExecuteOnVisible = true;
            }
            onVisible();
        } else {
            if (!mIsFirstUserVisibleHint) {
                onHidden();
            }
            mIsFirstUserVisibleHint = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    protected void setTag() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTag();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
