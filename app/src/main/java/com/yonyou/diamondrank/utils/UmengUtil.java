package com.yonyou.diamondrank.utils;

import android.content.Context;
import android.util.Log;

import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

/**
 * Created by libo on 2017/6/27.
 */

public class UmengUtil {

    public static void initPush(Context context,String appkey,String appScret) {
        //"57958227e0f55a6265002ab5","fcffcd358319ed5b185fb88fb3f47231"
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setAppkeyAndSecret(appkey,appScret);
        mPushAgent.enable();

        PushAgent.getInstance(context).onAppStart();

        String device_token = UmengRegistrar.getRegistrationId(context);
        Log.d("UMENG-LOG",device_token);
//        AvaDmHcp3dMfSx-_x5-yvE8Ty1LgGmYWyWQD7b-BUsZN

        // AlKEzSy_lkTa7kFUWiThnxxpQaoKGiosGKHeBrJRP2k9

        // AlKEzSy_lkTa7kFUWiThnxxpQaoKGiosGKHeBrJRP2k9
    }

}
