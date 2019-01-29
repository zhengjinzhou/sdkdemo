package net.vpnsdk.wanve.api;

import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.bean.LoginBean;
import net.vpnsdk.wanve.bean.NumBean;
import net.vpnsdk.wanve.bean.SNIDList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by zhou
 * on 2019/1/17.
 */

public class VpnApi {

    public static VpnApi bookApi;
    private VpnApiApiService service;

    public VpnApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .client(okHttpClient)
                .build();
        service = retrofit.create(VpnApiApiService.class);
    }

    public Observable<LoginBean> vpnLogin(String action, String cmd){
        return service.vpnLogin(action,cmd);
    }

    public Observable<NumBean> GetPhoneAppNum(String action, String UserID, String Signature, String Timestamp){
        return service.GetPhoneAppNum(action,UserID,Signature,Timestamp);
    }

    public Observable<SNIDList> getSnidList(String action, String keyword, String maxnum){
        return service.getSnidList(action,keyword,maxnum);
    }
}
