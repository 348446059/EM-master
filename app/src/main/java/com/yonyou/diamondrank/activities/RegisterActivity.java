package com.yonyou.diamondrank.activities;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baoyz.actionsheet.ActionSheet;
import com.bigkoo.pickerview.OptionsPickerView;

import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.bean.DepartmentBean;
import com.yonyou.diamondrank.bean.PositionBean;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;
import com.yonyou.diamondrank.views.FixedEditText;
import com.yonyou.diamondrank.widget.RoundAngleImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by libo on 17/1/22.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath()
                    + "/AppName/camera/";// 拍照路径
    String cameraPath;
    @Bind(R.id.et_login_name)
    FixedEditText etLoginName;
    @Bind(R.id.et_user_name)
    FixedEditText etUserName;
    @Bind(R.id.et_user_position)
    FixedEditText etUserPosition;
    @Bind(R.id.select_btn)
    Button selectBtn;
    @Bind(R.id.et_user_email)
    FixedEditText etUserEmail;
    @Bind(R.id.et_user_phone)
    FixedEditText etUserPhone;
    @Bind(R.id.et_user_password)
    FixedEditText etUserPassword;
    @Bind(R.id.et_user_again)
    FixedEditText etUserAgain;

    OptionsPickerView pickerView;
    @Bind(R.id.btn_close)
    Button btnClose;
    @Bind(R.id.register_btn)
    Button registerBtn;

    public static LoginActivity login_activity;
    @Bind(R.id.user_avator)
    RoundAngleImageView userAvator;

    private ArrayList<DepartmentBean> arrayList;
    private ArrayList<String> option1 = new ArrayList<String>();
    private ArrayList<ArrayList<String>> option2 = new ArrayList<ArrayList<String>>();

    private int departmentID, positionID;//部门及职位ID
    private boolean isUploadImage = false;
    private File imgFile;
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                login_activity.finish();
                startActivity(new Intent(RegisterActivity.this, MainActivitys.class));
                RegisterActivity.this.finish();
                ToastUtils.showShort(RegisterActivity.this,"注册成功");

            }else if (msg.what == 2){
                ToastUtils.showShort(RegisterActivity.this,"上传图片过大");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
        setTheme(R.style.ActionSheetStyleiOS7);

        if (!Utils.isConfigUrl(this)){
            ToastUtils.makeShortText("您还没有配置主机域名,请先配置!",this);
            finish();
            return;
        }

        Utils.getPositionInfo(this,new Utils.MyCallBack() {
            @Override
            public void success(ArrayList<DepartmentBean> listBean) {
                for (int i = 0; i < listBean.size(); i++) {
                    arrayList = listBean;
                    DepartmentBean departmentBean = arrayList.get(i);
                    option1.add(departmentBean.getDepartmentName());
                    ArrayList<String> opt = new ArrayList<String>();
                    for (int j = 0; j < departmentBean.getPositionBeen().size(); j++) {
                        PositionBean positionBean = departmentBean.getPositionBeen().get(j);
                        opt.add(positionBean.getPositionName());
                    }
                    option2.add(opt);
                }
                initView();
            }

            @Override
            public void failed(Exception e) {
                   ToastUtils.showShort(RegisterActivity.this,"没有部门信息");
            }
        });

    }

    private void initView() {
        btnClose.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        userAvator.setOnClickListener(this);
        pickerView = new OptionsPickerView(this);
        pickerView.setTitle("选择部门/职位");
        pickerView.setPicker(option1, option2, true);
       pickerView.setCyclic(false, false, false);
       pickerView.setSelectOptions(0, 0);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                getDepartIDAndPositionID(options1, option2);
            }
        });
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerView.show();
            }
        });


    }


    /**
     * 获取部门及职位ID
     *
     * @param arg0
     * @param arg1
     */
    private void getDepartIDAndPositionID(int arg0, int arg1) {
        DepartmentBean departmentBean = arrayList.get(arg0);
        departmentID = departmentBean.getId();
        PositionBean positionBean = arrayList.get(arg0).getPositionBeen().get(arg1);
        positionID = positionBean.getId();

        etUserPosition.setText(departmentBean.getDepartmentName() + "/" + positionBean.getPositionName());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.finish();
                break;
            case R.id.user_avator:
                showActionSheet();
                break;
            case R.id.add_img:
                showActionSheet();
                break;
            case R.id.register_btn:
                registerMethod();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                try {
                    //获取输入流
                    FileInputStream is = new FileInputStream(cameraPath);
                    //把流解析成bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    userAvator.setImageBitmap(bitmap);
                    userAvator.setScaleType(ImageView.ScaleType.FIT_XY);
                }catch (FileNotFoundException e){

                }
                isUploadImage = true;
                imgFile = new File(cameraPath);
            }else if (requestCode == ALBUM_REQUEST_CODE){
                isUploadImage = true;

                Picasso.with(this).load(data.getData()).fit().into(userAvator);
                imgFile = new File(data.getData().getPath());


                if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M){
                    imgFile = new File(getRealPathFromURI(data.getData()));
                }else {
                    imgFile = new File(data.getData().getPath());
                }

            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }

        cursor.close();
        return res;
    }


    public void startCamera(){
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            cameraPath = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis() + ".png";
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String out_file_path = SAVED_IMAGE_DIR_PATH;
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
//                  intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                   intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
           // }
           // int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            // 把文件地址转换成Uri格式
            Uri uri = Uri.fromFile(new File(cameraPath));
            // 设置系统相机拍摄照片完成后图片文件的存放地址


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             Uri imageUri = FileProvider.getUriForFile(RegisterActivity.this, "com.em.fileprovider",imgFile);
//             intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//             intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//         } else {
//             intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
//         }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
          ToastUtils.showShort(this,"请确认已经插入SD卡");
        }

    }

     private void showPhotoAlbum(){
         //跳转到系统相册
        // Intent intent = new Intent(Intent.ACTION_PICK, null);

         Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

         intent.setAction(Intent.ACTION_GET_CONTENT);



//         if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
//
//         }
//


         startActivityForResult(intent, ALBUM_REQUEST_CODE);
     }

    private void registerMethod() {

        if (TextUtils.isEmpty(etLoginName.getText())
                ||TextUtils.isEmpty(etUserName.getText().toString())
                ||TextUtils.isEmpty(etUserPhone.getText().toString())
                ||TextUtils.isEmpty(etUserEmail.getText().toString())
                ||TextUtils.isEmpty(etUserPosition.getText().toString())
                ||TextUtils.isEmpty(etUserPassword.getText().toString())
                ||TextUtils.isEmpty(etUserAgain.getText().toString())){

            ToastUtils.showShort(this,"信息不完整");
            return;
        }

        if (!Utils.isEmail(etUserEmail.getText().toString())){
            ToastUtils.showShort(this,"邮箱格式不正确");
            return;
        }

        if (!Utils.isMobileNO(etUserPhone.getText().toString())){
              ToastUtils.showShort(this,"手机格式不正确");
            return;
        }

        if (!etUserPassword.getText().toString().equals(etUserAgain.getText().toString())){
              ToastUtils.showShort(this,"两次输入的密码不一致");
            return;
        }


       if (!isUploadImage){
           ToastUtils.showShort(this,"请上传您的头像");
           return;
       }

       Map<String,Object> params = new HashMap<>();
        params.put("loginName",etLoginName.getText().toString().trim());
        params.put("plainPassword",etUserPassword.getText().toString().trim());
        params.put("name",etUserName.getText().toString().trim());
        params.put("deptId",departmentID);
        params.put("positionId",positionID);
        params.put("email",etUserEmail.getText().toString().trim());
        params.put("phone",etUserPhone.getText().toString().trim());


        post_image(Utils.getUrl(this,AppConstants.REGISTER_URL),params,imgFile);

    }

    /**
     * 注册请求接口
     * @param url
     * @param map
     * @param file
     */
    protected void post_image(final String url,final Map<String,Object> map,File file){
        OkHttpClient client = new OkHttpClient();
        //form表单形式上传
        final MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (file != null){
            //MediaType.parse()里面是上传的文件类型
           RequestBody body = RequestBody.create(MediaType.parse("image/*"),file);
            String filename = file.getName();
            //参数分别为 请求key
            requestBody.addFormDataPart("file","file.png",body);

        }

        if (map != null){
            //map里面是请求中所需要的key和value
            for(Map.Entry entry : map.entrySet()){
                requestBody.addFormDataPart(entry.getKey().toString(),entry.getValue().toString());
            }
        }
        Request request = new Request.Builder().url(url).addHeader("Content-Type","multipart/form-data").post(requestBody.build()).tag(this).build();

        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Message message = new Message();
                message.what = 2;
                myHandler.sendMessage(message);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Map<String,Object> map = new Gson().fromJson(str,Map.class);

                    if (map.get("code").equals("0")){
                        Gson g = new Gson();

                        User u =  g.fromJson(g.toJson(map.get("data")),User.class);
                        //保存用户对象
                        SpUtils.saveObject(RegisterActivity.this, AppConstants.USER,u);

                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);

                    }

                } else {
                    Log.i("lfq" ,response.message() + " error : body " + response.body().string());
                }
            }
        });

    }


    private void showActionSheet(){

      ActionSheet.createBuilder(this,getSupportFragmentManager())
              .setCancelButtonTitle("取消")
              .setOtherButtonTitles("相册","相机")
              .setCancelableOnTouchOutside(true)
              .setListener(this).show();
    }
    /**
     * actionSheet
     * @param actionSheet
     * @param isCancel
     */
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

        if (index == 0) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_REQUEST_CODE);

            }else {
                showPhotoAlbum();
            }



        } else if (index == 1) {

            if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_REQUEST_CODE);

            } else {
                //有权限，直接拍照
                startCamera();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
