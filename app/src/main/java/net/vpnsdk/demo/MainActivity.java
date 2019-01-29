package net.vpnsdk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.vpnsdk.vpn.Common;
import net.vpnsdk.vpn.Common.LogLevel;
import net.vpnsdk.vpn.Common.VpnError;
import net.vpnsdk.vpn.Common.VpnMsg;
import net.vpnsdk.vpn.SDKVpnService;
import net.vpnsdk.vpn.VPNAccount;
import net.vpnsdk.vpn.VPNManager;
import net.vpnsdk.vpn.VPNManager.AAAMethod;
import net.vpnsdk.vpn.Version;
import net.vpnsdk.wanve.activity.WebActivity;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private String Tag = "VPNSDKDemo";
    SharedPreferences mPerferences = null;
    Button mButtonLaunch;
    EditText mTextServer, mTextMethod, mTextUser, mTextPass;
    TextView mStatus, mDeviceId;

    private Thread mThreadStartVpn = null;
    private Thread mThreadStopVpn = null;
    private VPNAccount account;

    private static AAAMethod[] mMethods = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mButtonLaunch = (Button) findViewById(R.id.buttonVPN);
        mButtonLaunch.setOnClickListener(mButtonListener);
        mTextServer = (EditText) findViewById(R.id.textServer);
        mTextMethod = (EditText) findViewById(R.id.method);
        mTextUser = (EditText) findViewById(R.id.textUser);
        mTextPass = (EditText) findViewById(R.id.textPass);
        mStatus = (TextView) findViewById(R.id.textStatus);
        mDeviceId = (TextView) findViewById(R.id.textDeviceId);

        mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
        String value = mPerferences.getString("host", mTextServer.getText()
                .toString());
        mTextServer.setText(value);
        value = mPerferences.getString("user", "");
        mTextUser.setText(value);
        value = mPerferences.getString("pass", "");
        mTextPass.setText(value);
        VPNManager.initialize(this).setHandler(mHandler);
        VPNManager.getInstance().setLogLevel(LogLevel.LOG_DEBUG, 0);
        //bind 服务，不bind则无法启动VPN
        VPNManager.getInstance().bindVPNService("net.vpnsdk.vpn.VPN_SERVICE");
        //把证书复制到一个临时文件中
        saveRawResourceToFile(R.raw.rsaclient, "rsaclient.p12");
        saveRawResourceToFile(R.raw.sm2agclientenc, "sm2agclientenc.p12");
        saveRawResourceToFile(R.raw.sm2agclientsign, "sm2agclientsign.p12");
        Log.i(Tag, "SDK version: " + Version.VERSION + "\n" + "Build Date: " + Version.BUILD_DATE);

    }

    private void doSave() {
        SharedPreferences.Editor editor = mPerferences.edit();

        String value = mTextServer.getText().toString().trim();
        editor.putString("host", value);

        value = mTextUser.getText().toString().trim();
        editor.putString("user", value);

        value = mTextPass.getText().toString();
        editor.putString("pass", value);

        value = mTextMethod.getText().toString();
        editor.putString("method", value);

        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Tag, "onActivityResult");

        VPNManager.getInstance().onActivityResult(requestCode, resultCode);

        //if (account.getHost().length() > 0 ){
        //	start(account.getHost(), account.getPort(), account.getUsername(), account.getPassword(), account.getCertPath(), account.getCertPass(), account.getFlag());
        //}
    }

    public void onDestroy() {
        Log.d(Tag, "onDestroy");
        doSave();

        super.onDestroy();
    }

    public void OnPause() {
        super.onPause();
        Log.d(Tag, "OnPause");

        doSave();
    }

    public void doInit() {
        int status = VPNManager.getInstance().getStatus();
        if (status == Common.VpnStatus.CONNECTED) {
            mStatus.setText("VPN connected.");
            mButtonLaunch.setText("Disconnect");
        } else if (status == Common.VpnStatus.CONNECTING) {
            mStatus.setText("VPN is connecting.");
            mButtonLaunch.setText("Disconnect");
        } else if (status == Common.VpnStatus.DISCONNECTING) {
            mStatus.setText("VPN is disconnecting.");
            mButtonLaunch.setText("Disconnect");
        } else if (status == Common.VpnStatus.IDLE) {
            mStatus.setText("VPN is not running.");
            mButtonLaunch.setText("Connect");
        } else if (status == Common.VpnStatus.RECONNECTING) {
            mStatus.setText("VPN is not running.");
            mButtonLaunch.setText("Disconnect");
        }
    }

    public void onResume() {
        super.onResume();
        doInit();
    }

    // 保存资源到文件中
    private void saveRawResourceToFile(int id, String filename) {
        Log.d(Tag, "file dir is " + getFilesDir());
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

    private void createCertFile() {
        Log.d(Tag, "dir is " + getFilesDir());
        DataInputStream in = new DataInputStream(getResources().openRawResource(R.raw.rsaclient));
        try {
            try {
                FileOutputStream outputStream = openFileOutput("client.p12", Context.MODE_PRIVATE);
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

    //点击按钮后启动或停止VPN
    private OnClickListener mButtonListener = new OnClickListener() {
        public void onClick(View v) {
            doSave();
            int status = VPNManager.getInstance().getStatus();
            Log.i(Tag, "vpn status: " + status);

            if (status == Common.VpnStatus.IDLE) {
                // 启动VPN
                String server = mTextServer.getText().toString();
                server = server.trim().replace(" ", "");
                String user = mTextUser.getText().toString().trim();
                String pass = mTextPass.getText().toString();
                int flag = VPNManager.VpnFlag.VPN_FLAG_NATIVE_L3VPN;
                //account = new VPNAccount();
                //account.setFlag(flag);
                //account.setHost(server);
                //account.setUsername(user);
                //account.setPassword(pass);
                //account.setVerifySvrCert(false);
                //account.setCertPass("123456");
                //account.setCertPath(getFilesDir() + "/rsaclient.p12");

                //不需要证书的情况,证书路径和证书密码为空即可
                //start(server, 443, user, pass, "", "", flag);


                // 商密证书的情况
                /*
                 * rsaclient.p12 rsa证书， 密码123456
                 * */
                //start(server, 443, user, pass, getFilesDir() + "/rsaclient.p12", "123456", flag);

                // 国密证书的情况
                /*
                 * 国密证书有一般有两个文件（PFX格式，后缀是pfx或p12），一个用作加密，一个用作签名，两个文件名用'|'分割，密码用'\n'分割。
                 * 也可以只传递一个证书文件和一个密码，即用作加密，也用作签名，传递一个证书文件时不需要用分隔符分割，
                 * 传递一个密码时也不需要用分隔符分割。
                 * sm2agclientenc.p12 加密证书， 密码123456
                 * sm2agclientsign.p12 签名证书， 密码654321
                 * */
                //start(server, 443, user, pass, getFilesDir() + "/sm2agclientenc.p12|" + getFilesDir() + "/sm2agclientsign.p12", "123456\n654321", flag);
                //start(server, 443, user, pass, getFilesDir() + "/sm2agclientenc.p12" , "123456", flag);
                start(server, 443, user, pass, "", "", flag);
            } else {
                // 停止VPN
                stop();
            }
        }
    };

    /**
     * 启动
     */
    public void start(String host, int port, String username, String password,
                      String certpath, String certpass, int flag) {
        SDKVpnService.setCurPortal(host);
        if (mThreadStartVpn != null) {
            Log.w(Tag, "ThreadStartVpn is not null, will kill it!");
            mThreadStartVpn.interrupt();
            try {
                mThreadStartVpn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadStartVpn = null;
        }
        mThreadStartVpn = new StartVpnThread(host, port, username, password,
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
     * 停止
     */
    public void stop() {
        if (Common.VpnStatus.IDLE == VPNManager.getInstance().getStatus()) {
            return;
        }
        if (mThreadStopVpn != null) {
            Log.w(Tag, "ThreadStopVpn is not null, will kill it.");
            mThreadStopVpn.interrupt();
            try {
                mThreadStopVpn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadStopVpn = null;
        }

        if (mThreadStartVpn != null) {
            Log.w(Tag, "ThreadStartVpn is not null, will kill it!");
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
                VPNManager.getInstance().stopVPN();
                mThreadStopVpn = null;
            }
        });

        mThreadStopVpn.start();
    }

    public static AAAMethod[] getMethods() {
        return mMethods;
    }

    /**
     * 处理VPN的各种消息
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(Tag, "handleMessage " + msg.what);
            switch (msg.what) {
                case VpnMsg.MSG_VPN_CONNECTING:
                    Log.i(Tag, "vpn connecting");
                    mStatus.setText("VPN is connecting...");
                    mButtonLaunch.setText("Disconnect");
                    break;
                case VpnMsg.MSG_VPN_CONNECTED:
                    Log.i(Tag, "vpn connected ");
                    mStatus.setText("VPN connected.");
                    mButtonLaunch.setText("Disconnect");
                    Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                    startActivity(intent);
                    break;
                case VpnMsg.MSG_VPN_DISCONNECTING:
                    Log.i(Tag, "vpn disconnecting ");
                    mStatus.setText("VPN is disconnecting...");
                    break;
                case VpnMsg.MSG_VPN_DISCONNECTED:
                    Log.i(Tag, "vpn disconnected");
                    mStatus.setText("VPN is not running.");
                    mButtonLaunch.setText("Connect");
                    showError(msg.getData().getInt(VpnMsg.MSG_VPN_ERROR_CODE));
                    break;
                case VpnMsg.MSG_VPN_CONNECT_FAILED:
                    Log.i(Tag, "vpn connect failed");
                    mStatus.setText("VPN is not running.");
                    mButtonLaunch.setText("Connect");
                    showError(msg.getData().getInt(VpnMsg.MSG_VPN_ERROR_CODE));
                    break;
                case VpnMsg.MSG_VPN_RECONNECTING:
                    Log.i(Tag, "vpn reconnecting");
                    mStatus.setText("VPN is reconnecting...");
                    break;
                case VpnMsg.MSG_VPN_LOGIN:
                    int error = msg.getData().getInt(VpnMsg.MSG_VPN_ERROR_CODE);
                    mMethods = (AAAMethod[]) (msg.obj);
                    if (0 == mMethods.length) {
                        VPNManager.getInstance().cancelLogin();
                    }
                    handleLogin(error);
                    break;
                case VpnMsg.MSG_VPN_DEVREG:
                    mMethods = (AAAMethod[]) (msg.obj);
                    handleDevReg();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private void handleLogin(int error) {
        Intent intent = new Intent(this, LoginSdkActivity.class);
        intent.putExtra(LoginSdkActivity.DEVICE_REG, false);
        intent.putExtra(LoginSdkActivity.ERROR_CODE, error);
        this.startActivity(intent);
    }

    private void handleDevReg() {
        Intent intent = new Intent(this, LoginSdkActivity.class);
        intent.putExtra(LoginSdkActivity.DEVICE_REG, true);
        this.startActivity(intent);
    }


    /**
     * Show the error message when login failed or device registration failed.
     *
     * @param msgcode the error code.
     */
    private void showError(int msgcode) {
        Log.d(Tag, "vpn error code " + msgcode);
        if (Common.VpnError.ERR_WRONG_USER_PASS == msgcode) {
            alert(getString(R.string.error),
                    getString(R.string.user_pwd_error));
        } else if (VpnError.ERR_INTERRUPTED == msgcode) {
            // A user cancelled login or device registration
        } else if (VpnError.ERR_TUN_DOWN == msgcode) {
            // user stop vpn connection from notification area
        } else if (VpnError.ERR_CONFIG_L3VPN == msgcode) {
            // user stop vpn connection from notification area
            alert(getString(R.string.error), "failed to config L3VPN tunnel, please make sure you has vpn permission or stop app and try again");
        } else if ((VpnError.ERR_DEVID_APPROVE_PENDING == msgcode)
                || (VpnError.ERR_DEVID_APPROVE_DENY == msgcode)
                || (VpnError.ERR_DEVID_USER_LIMIT == msgcode)
                || (VpnError.ERR_DEVID_DEV_LIMIT == msgcode)
                || (VpnError.ERR_DEVID_UNREG == msgcode)) {
            // Show device registration errors
            String[] msg = VPNManager.getInstance().getErrorInfo(msgcode);
            alert(msg[1], msg[0]);
        } else if (VpnError.ERR_CALLBACK_FAILED == msgcode) {
            // Do nothing
        } else if ((msgcode >= VpnError.ERR_CERT_NO)
                && (msgcode <= VpnError.ERR_CERT_SERVER_REVOKED)) {
            // Show certificate errors
            String[] msg = VPNManager.getInstance().getErrorInfo(msgcode);
            alert(msg[0], msg[1]);
        } else {
            alert(getString(R.string.error),
                    getString(R.string.network_error));
        }
    }

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