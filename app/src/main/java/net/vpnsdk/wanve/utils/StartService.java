package net.vpnsdk.wanve.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by zhou
 * on 2018/7/26.
 */

public class StartService extends Service implements Runnable {
    private Thread thread;
    public StartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //创建服务时候调用，第一次创建
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyServer", "onCreate: 创建服务");
        //onCreate的时候创建初始化
        thread = new Thread( this);
        thread.start();
    }
    //每次服务启动调用，每次启动
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyServer", "onCreate: 启动服务");
        //如果服务并停止了，重新生成一个新的
        if(thread.isInterrupted()){
            thread = new Thread(this);
            thread.start();
        }
        return Service.START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void run() {
        int i=0;
        while (true){
            try {
                //每10秒钟进行一次输出
                Thread.sleep(10000);
                //Toast.makeText(getApplicationContext(),"服务启动"+i++,Toast.LENGTH_LONG).show();
                Log.d("MyServer", "服务启动"+i++);
                //openApp("zhou.com.myapplication");
                BadgeNumUtil.setBadgeNum(getApplicationContext(),i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    //    private void openApp(View v, String packageName) {
    private void openApp(String packageName) {
        //Context context = v.getContext();
        PackageInfo pi = null;
        //PackageManager pm = context.getPackageManager();
        PackageManager pm = getPackageManager();
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null ) {
            packageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    //服务销毁的时候
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyServer", "onCreate: 销毁服务");
        Intent intent = new Intent("com.zhou.service.destroy");
        sendBroadcast(intent);
    }
}