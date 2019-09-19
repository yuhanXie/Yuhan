package com.example.yuhan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yuhan.MainActivity;
import com.example.yuhan.R;
import com.example.yuhan.utils.LogUtils;

/**
 * @author xiegang
 */
public class Test1Fragment extends BaseFragment {


    private TextView mTvTest1;

    @Override
    protected void setTag() {
        super.setTag();
        TAG = "Test1Fragment";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.log("=======TestFragment1===onCreateView====");
        return inflater.inflate(R.layout.fragment_test1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTest1 = view.findViewById(R.id.tv_test_1);

        mTvTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
