package com.yonyou.diamondrank.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.SpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by libo on 2017/2/12.
 */

public class AddDepartmentActivity extends AppCompatActivity {
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("添加部门");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String url = "http://mobiledev.yonyou.com/em/login";
        String params = "username="+getIntent().getStringExtra("loginName")+"&"+"password="+SpUtils.getString(this,AppConstants.PASSWORD);
        byte[] bytes = params.getBytes();
        webView.postUrl(url,bytes);


    }

}
