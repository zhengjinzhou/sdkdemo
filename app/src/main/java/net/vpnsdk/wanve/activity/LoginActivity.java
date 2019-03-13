package net.vpnsdk.wanve.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.vpnsdk.demo.LoginSdkActivity;
import net.vpnsdk.demo.MainActivity;
import net.vpnsdk.demo.R;
import net.vpnsdk.vpn.Common;
import net.vpnsdk.vpn.SDKVpnService;
import net.vpnsdk.vpn.VPNManager;
import net.vpnsdk.vpn.Version;
import net.vpnsdk.wanve.base.BaseActivity;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.bean.LoginBean;
import net.vpnsdk.wanve.bean.SelectBean;
import net.vpnsdk.wanve.bean.VpnSelectBean;
import net.vpnsdk.wanve.contract.loginContract;
import net.vpnsdk.wanve.presenter.LoginPresenter;
import net.vpnsdk.wanve.utils.AppManager;
import net.vpnsdk.wanve.utils.SpUtil;
import net.vpnsdk.wanve.utils.ToastUtil;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements View.OnClickListener, loginContract.View {

    public final static int REQUEST_READ_PHONE_STATE = 1;
    public final static int REQUEST_WRITE_EXTERNAL_STATE = 2;
    private static VPNManager.AAAMethod[] mMethods = null;
    private EditText user;
    private EditText etPsd;
    private Thread mThreadStartVpn = null;
    private Thread mThreadStopVpn = null;

    private LoginPresenter presenter = new LoginPresenter(this);

    private boolean REM = false;
    private boolean AUTO = false;

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

        //权限申请
        initPermission();

        initView();

        presenter.attachView(this);
    }


    /**
     * 获取控件id
     */
    private void initView() {

        AppManager.getAppManager().addActivity(LoginActivity.this);

        findViewById(R.id.llVpn).setOnClickListener(this);
        findViewById(R.id.btLogin).setOnClickListener(this);
        findViewById(R.id.ivShow).setOnClickListener(this);
        findViewById(R.id.ivClear).setOnClickListener(this);

        final CheckBox isRem = findViewById(R.id.isRem);
        final CheckBox isAuto = findViewById(R.id.isAuto);

        user = findViewById(R.id.editText3);
        etPsd = findViewById(R.id.etPsd);

        VPNManager.initialize(this).setHandler(mHandler);
        VPNManager.getInstance().setLogLevel(Common.LogLevel.LOG_DEBUG, 0);
        //bind 服务，不bind则无法启动VPN
        VPNManager.getInstance().bindVPNService("net.vpnsdk.vpn.VPN_SERVICE");
        //把证书复制到一个临时文件中
        saveRawResourceToFile(R.raw.rsaclient, "rsaclient.p12");
        saveRawResourceToFile(R.raw.sm2agclientenc, "sm2agclientenc.p12");
        saveRawResourceToFile(R.raw.sm2agclientsign, "sm2agclientsign.p12");


        /**
         * 将保存的密码显示
         */
        SelectBean selectBean = (SelectBean) SpUtil.getObject(getApplicationContext(), Constant.Account, SelectBean.class);

        boolean aBoolean = SpUtil.getBoolean(this, Constant.REM, false);

        Log.d("新的vpn", "initView:--------------- " + aBoolean);

        if (selectBean != null) {
            if (aBoolean) {
                user.setText(selectBean.getUser());
                etPsd.setText(selectBean.getPsd());
                isRem.setChecked(aBoolean);
            }
        }

        //记住密码
        isRem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(getApplicationContext(), Constant.REM, isChecked);
            }
        });

        //自动登录
        isAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(getApplicationContext(), Constant.VPN_AUTO, isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClear:
                user.setText("");
                break;
            case R.id.ivShow:
                if (etPsd.getInputType() == 129) {
                    etPsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPsd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPsd.setSelection(etPsd.getText().toString().length());
                break;
            case R.id.llVpn:
                startActivity(new Intent(getApplicationContext(), VpnLoginActivity.class));
                break;
            case R.id.btLogin:
                if (TextUtils.isEmpty(user.getText().toString())) {
                    ToastUtil.show(getApplicationContext(), "账号不能为空");
                    return;
                }

                if (TextUtils.isEmpty(etPsd.getText().toString())) {
                    ToastUtil.show(getApplicationContext(), "密码不能为空");
                    return;
                }
                VpnSelectBean vpnSelectBean = (VpnSelectBean) SpUtil.getObject(getApplicationContext(), Constant.vpnAccount, VpnSelectBean.class);
                if (vpnSelectBean == null) {
                    ToastUtil.show(getApplicationContext(), "注意！！VPN账号为空。");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                //记住账号密码的状态
                SelectBean selectBean = new SelectBean(user.getText().toString(), etPsd.getText().toString(), REM, AUTO);
                SpUtil.putObject(this, Constant.Account, selectBean);
                if (vpnSelectBean != null) {
                    //vpn被记住的时候
                    if (vpnSelectBean.isSelect()) {
                      //  int status = VPNManager.getInstance().getStatus();
                        //使用vpn登录的时候
                        String server = "mobile.dg.cn";
                        String user = vpnSelectBean.getVpnUser();
                        String pass = vpnSelectBean.getVpnPsd();
                        Log.d("", "onClick: "+user);
                        Log.d("", "onClick: "+pass);
                        int flag = VPNManager.VpnFlag.VPN_FLAG_NATIVE_L3VPN;
                        start(server, 443, user, pass, "", "", flag);
                    }
                } else {
                    Log.d("新的vpn", "onClick: vpn不被记住的时候");
                    //vpn不被记住的时候
                    presenter.login();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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
        mThreadStartVpn = new LoginActivity.StartVpnThread(host, port, username, password,
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

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        dialog.dismiss();
    }

    @Override
    public void loginSuccess(LoginBean loginBean) {
        if (loginBean.isResult()) {
            Intent intent = new Intent(getApplicationContext(), WebActivity.class);
            intent.putExtra(Constant.ACCOUNT_USER, user.getText().toString());
            intent.putExtra(Constant.ACCOUNT_PSD, etPsd.getText().toString());
            startActivity(intent);
            finish();
        } else {
            //tvTip.setText(loginBean.getMessage());
            ToastUtil.show(getApplicationContext(), loginBean.getMessage());
        }
        dialog.dismiss();
    }

    @Override
    public void vpnLoginSuccess(LoginBean loginBean) {

    }

    @Override
    public String setUser() {
        return user.getText().toString();
    }

    @Override
    public String setPsd() {
        return etPsd.getText().toString();
    }

    /**
     * 动态权限申请
     */
    private void initPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        if (!permissionList.isEmpty()) {  //申请的集合不为空时，表示有需要申请的权限
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else { //所有的权限都已经授权过了

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0) { //安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {

                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) { //这个是权限拒绝
                            String s = permissions[i];
                            // ToastUtil.show(this,s+"权限被拒绝了,请自行到应用管理权限开启");
                        } else { //授权成功了
                            //do Something
                        }
                    }
                }
                break;
            default:
                break;
        }
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
                    dialog.dismiss();
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
                    dialog.dismiss();
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
     * Show a message dialog.
     *
     * @param title the title of the dialog
     * @param msg   the message of the dialog
     */
    private void alert(String title, String msg) {
        if (title.isEmpty() || msg.isEmpty()) {
            return;
        }
        if (isFinishing()) {
            return;
        }
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setNeutralButton(R.string.ok, null).show();
    }

}
