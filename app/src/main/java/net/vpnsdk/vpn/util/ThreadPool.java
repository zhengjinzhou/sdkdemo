package net.vpnsdk.vpn.util;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    public static final String TAG = ThreadPool.class.getSimpleName();
    static ExecutorService threadPool;

    public static void initThreadPool(int max){ // 可以在Application中进行配置
        if(max > 0){
            max = max < 3 ? 3 : max;
            threadPool = Executors.newFixedThreadPool(max);
        }else{
            threadPool = Executors.newCachedThreadPool();
        }

        Log.d(TAG, "[ThreadPool]ThreadPool init success...max thread: " + max);

    }

    public static ExecutorService getInstances(){
        return threadPool;
    }

    public synchronized static void go(Runnable runnable){
        if(null == threadPool){
        	Log.e(TAG, "ThreadPool has not been initialized");
            return;
        }
        threadPool.execute(runnable);
    }



}
