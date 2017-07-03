package com.yonyou.diamondrank.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.effects.SwipeBackController;

public class TestActivity extends AppCompatActivity {
    private SwipeBackController swipeBackController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        swipeBackController = new SwipeBackController(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (swipeBackController.processEvent(ev)) {
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }
}
