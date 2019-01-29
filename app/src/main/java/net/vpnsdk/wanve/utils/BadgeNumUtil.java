package net.vpnsdk.wanve.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by zhou
 * on 2018/7/24.
 */

public class BadgeNumUtil {

    public static void setBadgeNum(Context context, int num){
        try{
            Bundle bunlde =new Bundle();
            bunlde.putString("package", "zhou.com.vpn"); // com.test.badge is your package name
            bunlde.putString("class", "zhou.com.vpn.WebActivity"); // com.test. badge.MainActivityis your apk main activity
            bunlde.putInt("badgenumber",num);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        }catch(Exception e){
            Log.d("-----", "setBadgeNum: "+e.getMessage());
        }
    }
}
