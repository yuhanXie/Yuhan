package com.example.yuhan.layoutmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuhan.R;
import com.example.yuhan.utils.LogUtils;

import java.util.ArrayList;

/**
 * @author xiegang
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {

    private ArrayList<String> mDataList;

    public void setData(ArrayList<String> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public SimpleAdapter.SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_simple, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleAdapter.SimpleViewHolder holder, int position) {
        LogUtils.log("===========onBindViewHolder" +  position);
//        if (position % 2 == 0) {
            holder.mTvTest.setText(mDataList.get(position));
//        } else {
//            holder.mTvTest.setText("item" + position);
//        }

    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTest;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTest = itemView.findViewById(R.id.tv_adapter_simple);
        }
    }
}
