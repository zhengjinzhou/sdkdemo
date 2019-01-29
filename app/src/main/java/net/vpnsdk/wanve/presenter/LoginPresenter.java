package net.vpnsdk.wanve.presenter;

import android.util.Log;

import net.vpnsdk.wanve.activity.LoginActivity;
import net.vpnsdk.wanve.api.VpnApi;
import net.vpnsdk.wanve.base.RxPresenter;
import net.vpnsdk.wanve.bean.LoginBean;
import net.vpnsdk.wanve.contract.loginContract;

import okhttp3.OkHttpClient;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhou
 * on 2019/1/18.
 */

public class LoginPresenter extends RxPresenter<loginContract.View> implements loginContract.Presenter<loginContract.View>{

    VpnApi vpnApi;
    LoginActivity loginActivity;

    public LoginPresenter(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
        this.vpnApi = new VpnApi(new OkHttpClient());
    }

    @Override
    public void login() {
        Log.d("", "vpnLogin: "+"{UserID:'"+getUser()+"',UserPsw:'"+getPsd()+"}");
        Subscription rxSubscription = vpnApi.vpnLogin("Login", "{UserID:'"+getUser()+"',UserPsw:'"+getPsd()+"'}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginBean>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                        Log.d("", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError();
                        Log.d("", "onError: "+e.getMessage());
                    }

                    @Override
                    public void onNext(LoginBean loginBean) {
                        Log.d("", "onNext: "+loginBean.toString());
                        mView.loginSuccess(loginBean);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void vpnLogin() {
        //cmd={UserID:'shb',UserPsw:'123456'}

    }

    @Override
    public String getUser() {
        return mView.setUser();
    }

    @Override
    public String getPsd() {
        return mView.setPsd();
    }
}

