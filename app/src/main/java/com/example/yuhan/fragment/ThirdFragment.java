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

/**
 * @author xiegang
 */
public class ThirdFragment extends BaseFragment {

    private TextView mTvThird;


    @Override
    protected void setTag() {
        super.setTag();
        TAG = "ThirdFragment";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvThird = view.findViewById(R.id.tv_third);
        mTvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
