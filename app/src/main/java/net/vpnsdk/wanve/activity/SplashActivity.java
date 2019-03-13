package net.vpnsdk.wanve.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import net.vpnsdk.demo.R;
import net.vpnsdk.vpn.Common;
import net.vpnsdk.vpn.SDKVpnService;
import net.vpnsdk.vpn.VPNManager;
import net.vpnsdk.wanve.api.VpnApi;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.bean.AppInfoBean;
import net.vpnsdk.wanve.bean.LoginBean;
import net.vpnsdk.wanve.bean.SNIDList;
import net.vpnsdk.wanve.bean.SelectBean;
import net.vpnsdk.wanve.bean.VpnSelectBean;
import net.vpnsdk.wanve.contract.SelectContract;
import net.vpnsdk.wanve.presenter.SelectPresenter;
import net.vpnsdk.wanve.utils.AppManager;
import net.vpnsdk.wanve.utils.SpUtil;
import net.vpnsdk.wanve.utils.ToastUtil;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity implements SelectContract.View{

    private static final String TAG = "SplashActivity";
    private SelectPresenter presenter = new SelectPresenter(this);
    private String SNID;
    private Thread mThreadStartVpn = null;
    private Thread mThreadStopVpn = null;
    private static VPNManager.AAAMethod[] mMethods = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        presenter.attachView(this);
        presenter.getAppInfo();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppManager.getAppManager().addActivity(SplashActivity.this);

        final boolean aBoolean = SpUtil.getBoolean(getApplicationContext(), Constant.VPN_AUTO, false);//是否自动登录
        VpnSelectBean vpnSelectBean = (VpnSelectBean) SpUtil.getObject(getApplicationContext(), Constant.vpnAccount, VpnSelectBean.class);//vpn 的账号密码
        SelectBean selectBean = (SelectBean) SpUtil.getObject(getApplicationContext(), Constant.Account, SelectBean.class);//账号密码

        Log.d(TAG, "onCreate: 123");
        if (aBoolean) {
            VPNManager.initialize(this).setHandler(mHandler);
            VPNManager.getInstance().setLogLevel(Common.LogLevel.LOG_DEBUG, 0);
            //bind 服务，不bind则无法启动VPN
            VPNManager.getInstance().bindVPNService("net.vpnsdk.vpn.VPN_SERVICE");
            //把证书复制到一个临时文件中
            saveRawResourceToFile(R.raw.rsaclient, "rsaclient.p12");
            saveRawResourceToFile(R.raw.sm2agclientenc, "sm2agclientenc.p12");
            saveRawResourceToFile(R.raw.sm2agclientsign, "sm2agclientsign.p12");

            Log.d(TAG, "onCreate: 1234");
            //vpn自动登录
            if (vpnSelectBean != null && selectBean != null) {
                //点击过vpn登录
                if (vpnSelectBean.isSelect()) {
                    //  startService(new Intent(getApplicationContext(), MyServpce.class));
                    String server = "mobile.dg.cn";
                    String user = vpnSelectBean.getVpnUser();
                    String pass = vpnSelectBean.getVpnPsd();
                    Log.d("", "onClick: "+user);
                    Log.d("", "onClick: "+pass);
                    int flag = VPNManager.VpnFlag.VPN_FLAG_NATIVE_L3VPN;
                    start(server, 443, user, pass, "", "", flag);
                } else {
                    //没有点击过vpn登录
                    login(selectBean.getUser(), selectBean.getPsd());
                }
            } else {
                if (selectBean != null){
                    //没有vpn 的自动登录
                    login(selectBean.getUser(), selectBean.getPsd());
                }else {
                    Log.d(TAG, "onCreate: 123456");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, 2000);
                }

            }
        } else {
            Log.d(TAG, "onCreate: 123456");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }, 2000);
        }
    }

    /**
     * 启动
     */
    public void start(String host, int port, String username, String password,
                      String certpath, String certpass, int flag) {
        SDKVpnService.setCurPortal(host);
        if (mThreadStartVpn != null) {
            Log.w("新的vpn", "ThreadStartVpn is not null, will kill it!");
            mThreadStartVpn.interrupt();
            try {
                mThreadStartVpn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadStartVpn = null;
        }
        mThreadStartVpn = new SplashActivity.StartVpnThread(host, port, username, password,
                certpath, certpass, flag);

        mThreadStartVpn.start();
    }

    private class StartVpnThread extends Thread {
        String mHost;
        int mPort;
        String mUserName;
        String mPassword;
        String mCertPath;
        String mCertPass;
        int mFlag;

        public StartVpnThread(String host, int port, String username,
                              String password, String certpath, String certpass, int flag) {
            mHost = host;
            mPort = port;
            mUserName = username;
            mPassword = password;
            mCertPath = certpath;
            mCertPass = certpass;
            mFlag = flag;
        }

        public void run() {
            VPNManager.getInstance().startVPN(mHost, mPort, mUserName,
                    mPassword, mCertPath, mCertPass, mFlag);
        }
    }
    /**
     * 处理VPN的各种消息
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("vpn新包1", "handleMessage " + msg.what);
            switch (msg.what) {
                case Common.VpnMsg.MSG_VPN_CONNECTING:
                    Log.i("vpn新包2", "vpn connecting");
                    break;
                case Common.VpnMsg.MSG_VPN_CONNECTED:
                    Log.i("vpn新包3", "vpn connected ");
                    Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                    startActivity(intent);
                    break;
                case Common.VpnMsg.MSG_VPN_DISCONNECTING:
                    Log.i("vpn新包4", "vpn disconnecting ");
                    break;
                case Common.VpnMsg.MSG_VPN_DISCONNECTED:
                    Log.i("vpn新包5", "vpn disconnected");
                    stop();
                    break;
                case Common.VpnMsg.MSG_VPN_CONNECT_FAILED:
                    Log.i("vpn新包6", "vpn connect failed");
                    stop();
                    ToastUtil.show(getApplicationContext(), "VPN有误，请重新输入");
                    SpUtil.remove(getApplicationContext(), Constant.VPN_AUTO);
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    break;
                case Common.VpnMsg.MSG_VPN_RECONNECTING:
                    Log.i("vpn新包7", "vpn reconnecting");
                    break;
                case Common.VpnMsg.MSG_VPN_LOGIN:
                    int error = msg.getData().getInt(Common.VpnMsg.MSG_VPN_ERROR_CODE);
                    mMethods = (VPNManager.AAAMethod[]) (msg.obj);
                    if (0 == mMethods.length) {
                        VPNManager.getInstance().cancelLogin();
                    }
                    stop();
                    ToastUtil.show(getApplicationContext(), "VPN有误，请重新输入");
                    SpUtil.remove(getApplicationContext(), Constant.VPN_AUTO);
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    break;
                case Common.VpnMsg.MSG_VPN_DEVREG:
                    mMethods = (VPNManager.AAAMethod[]) (msg.obj);
                    Log.d("", "handleMessage: 2222");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };


    /**
     * 停止
     */
    public void stop() {
        if (Common.VpnStatus.IDLE == VPNManager.getInstance().getStatus()) {
            return;
        }
        if (mThreadStopVpn != null) {
            Log.w("vpn新包", "ThreadStopVpn is not null, will kill it.");
            mThreadStopVpn.interrupt();
            try {
                mThreadStopVpn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadStopVpn = null;
        }

        if (mThreadStartVpn != null) {
            Log.w("vpn新包", "ThreadStartVpn is not null, will kill it!");
            mThreadStartVpn.interrupt();
            try {
                mThreadStartVpn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadStartVpn = null;
        }

        mThreadStopVpn = new Thread(new Runnable() {
            public void run() {
                Log.d("", "run: 杀啊");
                VPNManager.getInstance().stopVPN();
                VPNManager.getInstance().cancelLogin();
                mThreadStopVpn = null;
            }
        });

        mThreadStopVpn.start();
    }

    /**
     * 没有使用vpn的自动登录
     *
     * @param user
     * @param psd
     */
    private void login(final String user, final String psd) {
        new VpnApi(new OkHttpClient()).vpnLogin("Login", "{UserID:'" + user + "',UserPsw:'" + psd + "'}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(getApplicationContext(),"请检查网络");
                    }

                    @Override
                    public void onNext(LoginBean loginBean) {
                        if (loginBean.isResult()){
                            Intent intent = new Intent(getApplicationContext(),WebActivity.class);
                            intent.putExtra(Constant.ACCOUNT_USER,user);
                            intent.putExtra(Constant.ACCOUNT_PSD,psd);
                            startActivity(intent);
                            finish();
                        }else {
                            ToastUtil.show(getApplicationContext(),loginBean.getMessage());
                        }
                    }
                });
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void getSnidListSuccess(List<SNIDList> snidList) {

    }

    @Override
    public String setKeywork() {
        return null;
    }

    @Override
    public void getAppinfoSuccess(AppInfoBean appInfoBean) {
        if (appInfoBean==null)return;
        Log.d(TAG, "getAppinfoSuccess: "+appInfoBean.toString());
        SpUtil.putObject(getApplicationContext(),Constant.AppInfo,appInfoBean);
    }

    @Override
    public String setSNID() {
        return null;
    }

    // 保存资源到文件中
    private void saveRawResourceToFile(int id, String filename) {
        Log.d("vpn新包", "file dir is " + getFilesDir());
        DataInputStream in = new DataInputStream(getResources().openRawResource(id));
        try {
            try {
                FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                in.close();
            } catch (Exception e) {
            }
        } finally {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("vpn新包", "onActivityResult");

        VPNManager.getInstance().onActivityResult(requestCode, resultCode);

        //if (account.getHost().length() > 0 ){
        //	start(account.getHost(), account.getPort(), account.getUsername(), account.getPassword(), account.getCertPath(), account.getCertPass(), account.getFlag());
        //}
    }

    public static VPNManager.AAAMethod[] getMethods() {
        return mMethods;
    }
}
