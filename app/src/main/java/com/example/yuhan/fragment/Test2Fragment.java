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
public class Test2Fragment extends BaseFragment {

    private TextView mTvTest2;

    @Override
    protected void setTag() {
        super.setTag();
        TAG = "Test2Fragment";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.log("=======TestFragment2===onCreateView====");
        return inflater.inflate(R.layout.fragment_test2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTest2 = view.findViewById(R.id.tv_test2);

        mTvTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
