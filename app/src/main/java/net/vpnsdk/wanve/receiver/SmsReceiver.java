package net.vpnsdk.wanve.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.vpnsdk.wanve.utils.ToastUtil;

/**
 * Created by zhou
 * on 2019/3/21.
 */

public class SmsReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.show(context,"收到信息");
        Log.d("-", "onReceive: ---------------------");
    }
}
