package com.example.yuhan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yuhan.layoutmanager.LayoutManagerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvLayoutManager = findViewById(R.id.tv_activity_layout_manager);
        tvLayoutManager.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_activity_layout_manager:
                Intent intent = new Intent(MainActivity.this, LayoutManagerActivity.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
