package net.vpnsdk.wanve.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.vpnsdk.wanve.activity.SplashActivity;
import net.vpnsdk.wanve.api.VpnApi;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.base.RxPresenter;
import net.vpnsdk.wanve.bean.AppInfoBean;
import net.vpnsdk.wanve.bean.SNIDList;
import net.vpnsdk.wanve.contract.SelectContract;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhou
 * on 2018/12/12.
 */

public class SelectPresenter extends RxPresenter<SelectContract.View> implements SelectContract.Presenter<SelectContract.View>{

    VpnApi vpnApi;
    SplashActivity splashActivity;

    public SelectPresenter(SplashActivity splashActivity){
        this.splashActivity = splashActivity;
        //this.vpnApi = new VpnApi(new OkHttpClient());
    }

    @Override
    public void getSnidList() {
        Log.d("---------------", "getSnidList: "+getKeywork());

        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个request对象
        final Request request = new Request.Builder()
                .url(Constant.BASE_URL+"/DMSPhoneAppService/AppConfigHandler.ashx?Action=search&keyword="+getKeywork()+"&maxnum=10")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("----------------", "onError: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String string = response.body().string();
                Log.d("--0000000-", "onResponse: "+string);
                Gson gson = new Gson();
                List<SNIDList> lists=gson.fromJson(string,new TypeToken<List<SNIDList>>(){}.getType());
                mView.getSnidListSuccess(lists);
            }
        });
    }

    @Override
    public String getKeywork() {
        return mView.setKeywork();
    }

    @Override
    public void getAppInfo() {
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个request对象
        final Request request = new Request.Builder()
                .url("http://wx.wanve.com/DMSPhoneAppService/AppConfigHandler.ashx?Action=getappinfo&SNID=0003")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("----------------", "onError: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                AppInfoBean appInfoBean = gson.fromJson(string, AppInfoBean.class);
                mView.getAppinfoSuccess(appInfoBean);
            }
        });
    }

    @Override
    public String getSNID() {
        return mView.setSNID();
    }
}
