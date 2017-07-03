package com.yonyou.diamondrank.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yonyou.diamondrank.bean.DepartmentBean;
import com.yonyou.diamondrank.callbacks.CommonCallback;
import com.yonyou.diamondrank.global.AppConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by libo on 2017/1/24.
 */

public class Utils {
    /**
     * 查看是否配置
     * @return
     */
    public static boolean isConfigUrl(Context context){
        String url = SpUtils.getString(context,AppConstants.MAIN_URL,"");
        if (url.isEmpty() || url.length() < 10){
            return false;
        }
        return true;
    }

    public static void getPositionInfo(Context context,final MyCallBack callBack){
        OkHttpUtils
                .get()//
                .url(Utils.getUrl(context,AppConstants.POSITION_INFO))//
                .build()//
                .execute(new CommonCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                       callBack.failed(e);
                    }

                    @Override
                    public void onResponse(Map<String, Object> stringObjectMap) {

                        Log.e("aa", stringObjectMap.toString());
                        if (stringObjectMap.get("code").equals("0")) {
                            Gson g = new Gson();
                             ArrayList<DepartmentBean> arrayList;
                            //g.fromJson(g.toJson(stringObjectMap.get("data")),ArrayList.class)
                            arrayList = g.fromJson(g.toJson(stringObjectMap.get("data")), new TypeToken<ArrayList<DepartmentBean>>() {
                            }.getType());
                            callBack.success(arrayList);
                        }
                    }
                });
    }

    /**
     * 手机号码格式校验
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){
        String telRegex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 电子邮箱格式验证
     * @param email
     * @return
     */
    public static boolean isEmail(String email){

        if (null == email || "".equals(email))return false;
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 请求地址包装
     * @return
     */

    public static String getUrl(Context context,String second_url){
        String first_url = SpUtils.getString(context,AppConstants.MAIN_URL,"");

        if (first_url.isEmpty()){
            return "";
        }else {
            String url = first_url + second_url;
            return url;
        }

    }


    public interface MyCallBack{
           void success(ArrayList<DepartmentBean> listBean);
           void failed(Exception e);
    }
}
