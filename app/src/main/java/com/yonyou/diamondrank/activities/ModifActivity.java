package com.yonyou.diamondrank.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.callbacks.CommonCallback;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.apache.http.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by libo on 2017/2/11.
 */

public class ModifActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.odl_key)
    TextView odlKey;
    @Bind(R.id.odl_value)
    TextView odlValue;
    @Bind(R.id.new_key)
    TextView newKey;
    @Bind(R.id.new_value)
    EditText newValue;
    @Bind(R.id.commit_btn)
    Button commitBtn;
    private int position;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        position = (int) getIntent().getIntExtra("position",0);

        user = (User) getIntent().getSerializableExtra("user");
        if (position == 0){
            toolbarTitle.setText("修改登录名");
            odlKey.setText("旧登录名");
            newKey.setText("新登录名");
            odlValue.setText(user.getLoginName());
        }else if (position == 1){
            toolbarTitle.setText("修改用户名");
            odlKey.setText("旧用户名");
            newKey.setText("新用户名");
            odlValue.setText(user.getName());
        }else if (position == 5){
            toolbarTitle.setText("修改手机号");
            odlKey.setText("旧手机名");
            newKey.setText("新手机名");
            odlValue.setText(user.getPhone());
        }else if (position == 6){
            toolbarTitle.setText("更换邮箱");
            odlKey.setText("旧邮箱");
            newKey.setText("新邮箱");
            odlValue.setText(user.getEmail());
        }else if (position == 8){
            toolbarTitle.setText("修改登录密码");
            odlKey.setText("旧密码");
            newKey.setText("新密码");
            newValue.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,String> params = new HashMap<String, String>();
                final String userId = user.getId().substring(0,user.getId().length()-2);
                params.put("id",userId);
                if (TextUtils.isEmpty(newValue.getText().toString())){
                    ToastUtils.showShort(ModifActivity.this,"请输入内容");
                    return;
                }

                if (position == 0){
                    if (newValue.getText().length() <6){
                        ToastUtils.showShort(ModifActivity.this,"账号不能小于6位");
                        return;
                    }else {
                        params.put("loginName",newValue.getText().toString().trim());
                    }
                }else if (position == 1){
                    params.put("name",newValue.getText().toString().trim());

                }else if (position == 5){
                    if (!Utils.isMobileNO(newValue.getText().toString())){
                        ToastUtils.showShort(ModifActivity.this,"手机号不合法");
                        return;
                    }else {
                        params.put("phone",newValue.getText().toString().trim());
                    }
                }else if (position == 6){
                    if (!Utils.isEmail(newValue.getText().toString())){
                        ToastUtils.showShort(ModifActivity.this,"邮箱账号号不合法");
                        return;
                    }else {

                        params.put("email",newValue.getText().toString().trim());
                    }
                }else if (position == 8){
                    if (newValue.getText().length()<6){
                        ToastUtils.showShort(ModifActivity.this,"密码不能小于6位");
                        return;
                    }else {
                        params.put("plainPassword",newValue.getText().toString().trim());
                    }
                }

                OkHttpUtils.post()
                        .url(Utils.getUrl(ModifActivity.this,AppConstants.MODIF_INFO))
                        .params(params)
                        .build().execute(new CommonCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {
                       if (stringObjectMap.get("code").equals("0")){
                           Map<String,Object> value = (Map<String, Object>) stringObjectMap.get("data");

                           user.setLoginName((String) value.get("loginName"));
                           user.setName((String) value.get("name"));
                           user.setEmail((String) value.get("email"));
                           user.setPhone((String)value.get("phone"));

                           ToastUtils.showShort(ModifActivity.this,"修改成功");
                           SpUtils.clearData(ModifActivity.this,AppConstants.USER);
                           SpUtils.saveObject(ModifActivity.this,AppConstants.USER,user);
                           finish();
                       }
                    }
                });
            }
        });

    }
}
