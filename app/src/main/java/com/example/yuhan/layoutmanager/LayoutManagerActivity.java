package com.example.yuhan.layoutmanager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yuhan.R;

import java.util.ArrayList;

/**
 * @author xiegang
 */
public class LayoutManagerActivity extends AppCompatActivity {


    private SimpleAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_manager);
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        CustomV2LayoutManager customLayoutManager = new CustomV2LayoutManager();
        recyclerView.setLayoutManager(customLayoutManager);
        mAdapter = new SimpleAdapter();
        recyclerView.setAdapter(mAdapter);
        customLayoutManager.setLeftItemCount(8);
        initData();
    }

    private void initData() {
        ArrayList<String> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i % 2 == 0) {
                dataList.add("item       " + i + "\n\n");
            } else {
                dataList.add("item       " + i + "\n");
            }
        }
        mAdapter.setData(dataList);
        mAdapter.notifyDataSetChanged();
    }
}
