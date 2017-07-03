package com.yonyou.diamondrank.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.callbacks.CommonCallback;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;
import com.yonyou.diamondrank.views.CleanEditText;
import com.google.gson.Gson;
import com.umeng.message.UmengRegistrar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

import okhttp3.Call;

/**
 * @desc 登录界面
 * Created by lanzhm on 16/7/18.
 */
public class LoginActivity extends Activity{

    private static final String TAG = "loginActivity";
    private static final int REQUEST_CODE_TO_REGISTER = 0x001;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0x102;

    // 界面控件
    private CleanEditText urlEdit;
    private CleanEditText accountEdit;
    private CleanEditText passwordEdit;
    private Button mBtnLogin,register_btn;
    private CheckBox rememberPwd;
    private ImageButton setip_btn;
    // 第三方平台获取的访问token，有效时间，uid
    private String accessToken;
    private String expires_in;
    private String uid;
    private String sns;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLogin();
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        accountEdit = (CleanEditText) this.findViewById(R.id.et_username);
        urlEdit = (CleanEditText) this.findViewById(R.id.et_domain);
        accountEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        accountEdit.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        register_btn = (Button) findViewById(R.id.register_btn);
        passwordEdit = (CleanEditText) this.findViewById(R.id.et_password);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordEdit.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    clickLogin();
                }
                return false;
            }
        });
        setip_btn = (ImageButton)findViewById(R.id.set_btn);

        mBtnLogin=(Button) findViewById(R.id.btn_login);
        rememberPwd= (CheckBox) findViewById(R.id.remember_pwd);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                RegisterActivity.login_activity = LoginActivity.this;
                startActivity(intent);
            }
        });

        setip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SetIPActivity.class);
                startActivity(intent);
            }
        });
    }




    /**
     * 点击登录
     */
    private void clickLogin() {

        if (!Utils.isConfigUrl(this)){
            ToastUtils.makeShortText("您还没有配置主机域名,请先配置!",this);
            return;
        }


        final String account =accountEdit.getText().toString();
        final String password = passwordEdit.getText().toString();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ToastUtils.makeShortText("你有相关权限！",this);
            } else {

                // No explanation needed, we can request the permission.
                ToastUtils.makeShortText("你没有相关权限，请手动授予权限！",this);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
//        final String token = DeviceUtil.getDeviceId(this);
        final String device_token = UmengRegistrar.getRegistrationId(this);
//        final String device_token = "123765";
        if (checkInput(account, password,device_token)) {
            // TODO: 请求服务器登录账号
            OkHttpUtils
                    .post()//
                    .url(Utils.getUrl(this,AppConstants.LOGIN_URL))
                    .addParams("account", account)//
                    .addParams("password", password)//
                    .addParams("token", device_token)
                    .build()//
                    .execute(new CommonCallback()
                    {
                         @Override
                        public void onError(Call call, Exception e)
                        {
                            ToastUtils.showShort(LoginActivity.this,"登录失败");
                        }

                         @Override
                        public void onResponse(Map<String, Object> stringObjectMap)
                        {
                            Log.d(TAG, "------*------onResponse：complete="+stringObjectMap.toString());
                            if (stringObjectMap.get("code").equals("0")){
                                Gson g = new Gson();

                                User u =  g.fromJson(g.toJson(stringObjectMap.get("data")),User.class);
                                if (u.getRoles().equals("2")){
                                    SpUtils.putString(LoginActivity.this, AppConstants.PASSWORD, password);
                                }
                                ToastUtils.showShort(LoginActivity.this,"登录成功");
                                 //保存用户对象
                                SpUtils.saveObject(LoginActivity.this, AppConstants.USER,u);
                                SpUtils.putBoolean(LoginActivity.this, AppConstants.REMEMBER, rememberPwd.isChecked());
                                SpUtils.putBoolean(LoginActivity.this, AppConstants.FIRST_OPEN, true);
                                startActivity(new Intent(LoginActivity.this, MainActivitys.class));
                                LoginActivity.this.finish();
                            }
                        }
                    });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    ToastUtils.makeShortText("授权成功！",this);

                } else {

                    ToastUtils.makeShortText("授权失败！",this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    /**
     * 检查输入
     *
     * @param account
     * @param password
     * @param token
     * @return
     */
    public boolean checkInput(String account, String password,String token) {
        //是否选择记住密码
        if (SpUtils.getBoolean(LoginActivity.this,AppConstants.REMEMBER)){
            accountEdit.setText(SpUtils.getString(LoginActivity.this,AppConstants.ACCOUNT));
            passwordEdit.setText(SpUtils.getString(LoginActivity.this,AppConstants.PASSWORD));
        }else{
            accountEdit.setText("");
            passwordEdit.setText("");
        }
        // 账号为空时提示
        if (account == null || account.trim().equals("")) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_LONG)
                    .show();
        } else {
           if (password == null || password.trim().equals("")) {
                Toast.makeText(this, "密码不能为空",
                        Toast.LENGTH_LONG).show();
            } else {
               if (rememberPwd.isChecked()){
                   SpUtils.putBoolean(LoginActivity.this, AppConstants.REMEMBER, true);
                   SpUtils.putString(LoginActivity.this, AppConstants.ACCOUNT, account);
                   SpUtils.putString(LoginActivity.this, AppConstants.PASSWORD, password);
                   SpUtils.putString(LoginActivity.this, AppConstants.TOKEN, token);
               }else {
                   SpUtils.putBoolean(LoginActivity.this, AppConstants.REMEMBER, false);
                   SpUtils.putString(LoginActivity.this, AppConstants.ACCOUNT, "");
                   SpUtils.putString(LoginActivity.this, AppConstants.PASSWORD, "");
                   SpUtils.putString(LoginActivity.this, AppConstants.TOKEN, "");
               }
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

}
