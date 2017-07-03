package com.yonyou.diamondrank.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.UmengUtil;
import com.yonyou.diamondrank.views.CleanEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by libo on 2017/6/27.
 */

public class SetIPActivity extends Activity {


    @Bind(R.id.btn_close)
    Button btnClose;
    @Bind(R.id.set_appkey)
    CleanEditText setAppkey;
    @Bind(R.id.set_appscret)
    CleanEditText setAppscret;
    @Bind(R.id.set_domain)
    CleanEditText setDomain;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.auto_btn)
    Button autoBtn;
    @Bind(R.id.set_portal)
    CleanEditText setPortal;
    @Bind(R.id.input_layout)
    LinearLayout inputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetIPActivity.this.finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isVail = false;

                String main_url = setDomain.getText().toString();
                String portal = setPortal.getText().toString();

                if (!portal.isEmpty() && portal.length() >3){
                   main_url += ":" + portal;
                }

                if (main_url.isEmpty()) {
                    showUrlError();
                } else {

                    if (!main_url.startsWith("http")) {
                        main_url = "http://" + main_url;


                    }

                }

                if (Patterns.WEB_URL.matcher(main_url).matches()) {
                    isVail = true;
                } else {
                    isVail = false;
                    showUrlError();
                }


                if (isVail) {
                    SpUtils.putString(SetIPActivity.this, AppConstants.MAIN_URL, main_url);
                } else {
                    showUrlError();
                }
                String url = SpUtils.getString(SetIPActivity.this, AppConstants.MAIN_URL);

                if (setAppkey.getText().toString().length() < 20 || setAppscret.getText().toString().length() < 20) {
                    ToastUtils.makeShortText("推送密匙格式不正确", SetIPActivity.this);
                } else {
                    UmengUtil.initPush(SetIPActivity.this, setAppkey.getText().toString(), setAppscret.getText().toString());
                    ToastUtils.makeShortText("保存成功", SetIPActivity.this);
                    SetIPActivity.this.finish();
                }
            }
        });


        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(SetIPActivity.this);
                intentIntegrator
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                        .setPrompt("将二维码/条码放入框内，即可自动扫描")
                        .setOrientationLocked(false)
                        .setCaptureActivity(CustomScanAct.class)
                        .initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {

            } else {
                // ScanResult 为获取到的字符串
                String scanResult = intentResult.getContents();

                String[] strs = scanResult.split(",");

                if (strs.length == 3){
                    setDomain.setText(strs[0]);
                    setAppkey.setText(strs[1]);
                    setAppscret.setText(strs[2]);
                    return;
                }

                if (strs.length == 4){
                    setDomain.setText(strs[0]);
                    setPortal.setText(strs[1]);
                    setAppkey.setText(strs[2]);
                    setAppscret.setText(strs[3]);
                    return;
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showUrlError() {
        ToastUtils.makeShortText("请输入有效的域名或IP地址:", this);
    }

    /**
     * 验证输入域名地址合法性
     *
     * @return
     */
    private boolean isVailUrl() {
        boolean isVail = false;

        String main_url = setDomain.getText().toString();

        if (main_url.isEmpty()) {
            showUrlError();
        } else {

            if (!main_url.startsWith("http")) {
                main_url = "http://" + main_url;
            }

        }

        if (Patterns.WEB_URL.matcher(main_url).matches()) {
            isVail = true;
        } else {
            isVail = false;
            showUrlError();
        }

        return isVail;
    }
}
