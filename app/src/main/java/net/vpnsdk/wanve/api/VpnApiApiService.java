package net.vpnsdk.wanve.api;


import net.vpnsdk.wanve.bean.LoginBean;
import net.vpnsdk.wanve.bean.NumBean;
import net.vpnsdk.wanve.bean.SNIDList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by zhou
 * on 2018/8/1.
 */

public interface VpnApiApiService {
    @GET("/DMS_Phone/Login/LoginHandler.ashx")
    Observable<LoginBean> vpnLogin(@Query("Action") String action, @Query("cmd") String cmd);

    //{cmd={form=app}}
    @GET
    Call<String> requestPost(@Url() String url);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @FormUrlEncoded
    @POST("/oasystem/Handlers/DMS_Handler.ashx")
    Observable<NumBean>
    GetPhoneAppNum(@Query("Action") String action, @Query("UserID") String UserID, @Query("Signature") String Signature, @Query("Timestamp") String Timestamp);

    @GET("/DMSPhoneAppService/AppConfigHandler.ashx")
    Observable<SNIDList> getSnidList(@Query("Action") String action, @Query("keyword") String keyword, @Query("maxnum") String maxnum);


}
