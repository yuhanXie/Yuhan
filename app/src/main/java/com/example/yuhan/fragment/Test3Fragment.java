package com.example.yuhan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yuhan.R;
import com.example.yuhan.utils.LogUtils;

/**
 * @author xiegang
 */
public class Test3Fragment extends BaseFragment {

    @Override
    protected void setTag() {
        super.setTag();
        TAG = "Test3Fragment";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.log("=======TestFragment3===onCreateView====");
        return inflater.inflate(R.layout.fragment_test3, container, false);
    }

}
