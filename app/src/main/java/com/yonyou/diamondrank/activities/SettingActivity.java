package com.yonyou.diamondrank.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.bigkoo.pickerview.OptionsPickerView;

import com.yonyou.diamondrank.R;
import com.yonyou.diamondrank.adapters.RecyclerViewAdapter;
import com.yonyou.diamondrank.bean.DepartmentBean;
import com.yonyou.diamondrank.bean.PositionBean;
import com.yonyou.diamondrank.callbacks.CommonCallback;
import com.yonyou.diamondrank.global.AppConstants;
import com.yonyou.diamondrank.utils.DividerItemDecoration;
import com.yonyou.diamondrank.utils.ImageLoadProxy;
import com.yonyou.diamondrank.utils.PictureUtil;
import com.yonyou.diamondrank.utils.SpUtils;
import com.yonyou.diamondrank.utils.ToastUtils;
import com.yonyou.diamondrank.utils.User;
import com.yonyou.diamondrank.utils.Utils;
import com.yonyou.diamondrank.widget.RoundAngleImageView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * Created by libo on 2017/2/10.
 */

public class SettingActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener{
    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath()
                    + "/AppName/camera/";// 拍照路径

    private ArrayList<DepartmentBean> arrayList;
    private ArrayList<String> option1 = new ArrayList<String>();
    private ArrayList<ArrayList<String>> option2 = new ArrayList<ArrayList<String>>();

    private int departmentID, positionID;//部门及职位ID
    private File imgFile;
    private String cameraPath;

    OptionsPickerView pickerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.user_avator)
    RoundAngleImageView userAvator;
    @Bind(R.id.my_recyclerview)
    RecyclerView myRecyclerview;
    private User user;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> mData;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String info = (String) msg.obj;
            ToastUtils.showShort(SettingActivity.this,info);
            if (msg.what == 1){
                ImageLoadProxy.displayHeadIcon(user.getImgPath(),userAvator);

                //删除临时文件
                File file = new File(compressImgPath);
                file.delete();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        setTheme(R.style.ActionSheetStyleiOS7);
        user = (User) getIntent().getSerializableExtra("user");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = (User) SpUtils.readObject(this, AppConstants.USER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpUtils.clearData(this,AppConstants.USER);
        SpUtils.saveObject(this,AppConstants.USER,user);
    }

    private void initView() {
        setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("设置");
        ImageLoadProxy.displayHeadIcon(Utils.getUrl(this,user.getImgPath()), userAvator);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pickerView = new OptionsPickerView(this);
        pickerView.setTitle("选择部门/职位");

        // setlayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerview.setLayoutManager(mLayoutManager);
        //setAdapter
        mData = new ArrayList<>(100);

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
                pickerView.setPicker(option1, option2, true);
                pickerView.setCyclic(false, false, false);
                pickerView.setSelectOptions(0, 0);
                pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        getDepartIDAndPositionID(options1, option2);
                    }
                });

            }

            @Override
            public void failed(Exception e) {

            }
        });


        mAdapter = new RecyclerViewAdapter(this,mData,user);

        myRecyclerview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL_LIST));
        myRecyclerview.setAdapter(mAdapter);

        userAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionSheet();
            }
        });


        mAdapter.setmOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                   if (position == 3){
                       pickerView.show();
                       modifView = view;
                   }else if (position == 9){
                       Intent intent = new Intent(SettingActivity.this,AddDepartmentActivity.class);
                       intent.putExtra("loginName",user.getLoginName());
                       startActivity(intent);

                   }else {
                       Intent intent = new Intent(SettingActivity.this,ModifActivity.class);
                       intent.putExtra("position",position);

                        intent.putExtra("user",user);
                       startActivity(intent);

                   }
            }
        });

    }
  private View modifView;
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

        //用户ID保留有效数字（后台bug）
       String s = user.getId().substring(0,user.getId().length()-2);

        OkHttpUtils.post()
                .url(Utils.getUrl(this,AppConstants.MODIF_INFO))
                .addParams("id",s)
                .addParams("deptId",String.valueOf(departmentID))
                .addParams("positionId",String.valueOf(positionID))
                .build()
                .execute(new CommonCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtils.showShort(SettingActivity.this,"修改失败");
                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {
                        if (stringObjectMap.get("code").equals("0")) {
                            Gson g = new Gson();
                             Map<String,String> map =  g.fromJson(g.toJson(stringObjectMap.get("data")),Map.class);
                               user.setDepartmentName(map.get("departmentName"));
                               user.setDeptId(map.get("deptId"));
                               user.setPosition(map.get("positionName"));
                               user.setPositionId(map.get("positionId"));
                            ToastUtils.showShort(SettingActivity.this,"修改成功");
                           TextView tv =  (TextView) modifView.findViewById(R.id.value);
                            tv.setText(user.getDepartmentName()+" - "+user.getPosition());
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

            // 把文件地址转换成Uri格式
            Uri uri = Uri.fromFile(new File(cameraPath));
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            ToastUtils.showShort(this,"请确认已经插入SD卡");
        }

    }

    private void showPhotoAlbum(){
        //跳转到系统相册
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0){
            showPhotoAlbum();
        }else if (index == 1){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    private  String compressImgPath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_REQUEST_CODE) {


                compressImgPath = PictureUtil.compressImage(cameraPath, cameraPath, 30);
                imgFile = new File(compressImgPath);

            }else if (requestCode == ALBUM_REQUEST_CODE){

                String imgPath = data.getData().getPath();
                String compress =  imgPath.replace(imgPath.substring(imgPath.length()-9,imgPath.length()-6),"compress") ;
                compressImgPath = PictureUtil.compressImage(imgPath, compress, 30);
                imgFile = new File(compressImgPath);
            }
            Map<String ,String> params = new HashMap<>();
            String s = user.getId().substring(0,user.getId().length()-2);
            params.put("id",s);
            post_img(AppConstants.UPLOAD_IMG_URL,params,imgFile);
        }
    }

    private void post_img(final String url, final Map<String,String> params, File file){

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

        if (params != null){
            //map里面是请求中所需要的key和value
            for(Map.Entry entry : params.entrySet()){
                requestBody.addFormDataPart(entry.getKey().toString(),entry.getValue().toString());
            }
        }
        Request request = new Request.Builder().url(Utils.getUrl(this,url)).addHeader("Content-Type","multipart/form-data").post(requestBody.build()).tag(this).build();

        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = "修改头像失败";
                handler.sendMessage(message);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();

                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Map<String,Object> map = new Gson().fromJson(str,Map.class);

                    if (map.get("code").equals("0")){
                        Map<String,Object> value = (Map<String, Object>) map.get("data");

                        user.setImgPath((String) value.get("imgPath"));
                        message.obj = "修改头像成功";
                        message.what=1;
                        handler.sendMessage(message);

                    }

                } else {
                    message.obj = "修改头像失败";
                    handler.sendMessage(message);

                }
            }
        });

    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
