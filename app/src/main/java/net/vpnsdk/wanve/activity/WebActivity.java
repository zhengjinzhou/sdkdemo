package net.vpnsdk.wanve.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinggrid.iappoffice.IAppOffice;

import net.vpnsdk.demo.R;
import net.vpnsdk.vpn.VPNManager;
import net.vpnsdk.wanve.api.httpUtil;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.bean.AppInfoBean;
import net.vpnsdk.wanve.bean.LookDocBean;
import net.vpnsdk.wanve.bean.SelectBean;
import net.vpnsdk.wanve.bean.TypeBean;
import net.vpnsdk.wanve.bean.VpnSelectBean;
import net.vpnsdk.wanve.utils.AppManager;
import net.vpnsdk.wanve.utils.JsonConvert;
import net.vpnsdk.wanve.utils.LastPoint;
import net.vpnsdk.wanve.utils.LoadDialog;
import net.vpnsdk.wanve.utils.SpUtil;
import net.vpnsdk.wanve.utils.ToastUtil;
import net.vpnsdk.wanve.utils.WebServiceUtil;
import net.vpnsdk.wanve.utils.WpsUtil;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.wps.moffice.client.ViewType;

public class WebActivity extends AppCompatActivity {


    /*
    * 调用打开文档的iAppOffice对象；
    */
    private IAppOffice iappoffice;
    private SaveReceiver saveReceiver;

    private static final String TAG = "WebActivity";
    private WebView mWebView;
    /**
     * 菜单弹出popup
     */
    static PopupWindow mPopupWindow;
    private String LastUrl = "";
    private String fileName;
    private String guid;
    private long downloadId;
    private LoadDialog loadDialog;
    private ProgressBar progress;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        loadDialog = new LoadDialog(this, true, "加载中...");

        AppManager.getAppManager().finishAllActivity();

        saveReceiver = new SaveReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.kinggrid.iappoffice.save");
        intentFilter.addAction("com.kinggrid.iappoffice.close");
        intentFilter.addAction("com.kinggrid.iappoffice.home");
        intentFilter.addAction("com.kinggrid.iappoffice.back");
        registerReceiver(saveReceiver, intentFilter);
        //获取从MyService中传过来的账号密码

        mWebView = findViewById(R.id.webView);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);//加载JavaScript
        //设置可以支持缩放
        //设置支持缩放
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        //不显示webview缩放按钮
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.addJavascriptInterface(new HuamboJsInterface(), "contact");
        mWebView.setWebViewClient(mWebViewClient);//这个一定要设置，要不然不会再本应用中加载
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setSupportZoom(true);
        /**
         * webView白屏问题
         */
        mWebView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        SelectBean selectBean = (SelectBean) SpUtil.getObject(this, Constant.Account, SelectBean.class);
        //拼合成主页面地址
        //String url = Constant.MAIN_URL + "?cmd={UserID:'" + selectBean.getUser() + "',UserPsw:'" + selectBean.getPsd() + "'}&From=APP";
        String url = Constant.MAIN_URL +"?UserID=" + selectBean.getUser() + Constant.ACCOUNT_SysID+ "&From=APP";
        Log.d(TAG, "onCreate: " + url);
        mWebView.loadUrl(url);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
        //更新
        initUpdate();
    }

    /**
     * 更新app，
     * 只需要服务器的
     * 版本号大于本地的版本号即可更新
     * 现在服务器的是1，本地的是2
     * 本次所有都变成3
     */
    private void initUpdate() {

        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            Log.d(TAG, "initUpdate: " + versionCode);
            final AppInfoBean appInfoBean = (AppInfoBean) SpUtil.getObject(this, Constant.AppInfo, AppInfoBean.class);
            if (appInfoBean==null)return;
            if (versionCode < appInfoBean.getAppVersion()) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("版本更新");
                builder.setMessage(appInfoBean.getUpdateMemo());
                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse(appInfoBean.getDownloadURL());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        startActivity(browserIntent);
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果要实现文件下载的功能，需要设置WebView的DownloadListener，通过实现自己的DownloadListener来实现文件的下载
     */
    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Log.d("tag", "url=" + url);
            Log.d("tag", "userAgent=" + userAgent);
            Log.d("tag", "contentDisposition=" + contentDisposition);
            Log.d("tag", "mimetype=" + mimetype);
            Log.d("tag", "contentLength=" + contentLength);
            /*Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);*/
            downloadBySystem(url, contentDisposition, mimetype);
        }
    }


    /**
     * 下载
     *
     * @param url
     * @param contentDisposition
     * @param mimeType
     */
    @SuppressLint("NewApi")
    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true);
        request.setVisibleInDownloadsUi(false);
        request.setAllowedOverRoaming(true);
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Log.d(TAG, "fileName:{123456}" + fileName);
        request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath().toString(), fileName);
        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        Log.d(TAG, "downloadId:{} " + downloadId);
    }

    //ChromeClient
    WebChromeClient mWebChromeClient = new WebChromeClient() {
        //监听网页加载
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

            result.confirm();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.d(TAG, "onReceivedTitle: " + view);
            super.onReceivedTitle(view, title);
        }
    };

    //WebViewClient
    WebViewClient mWebViewClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if (view.getUrl().contains("cmd") && view.getTitle().length() > 16) {
                SpUtil.remove(getApplicationContext(), Constant.Account);
                ToastUtil.show(getApplicationContext(), "账号或密码有误");
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
            LastUrl = url;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS 交互
     */
    public final class HuamboJsInterface {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @JavascriptInterface
        public void clickAndroid(String json) throws IOException, XmlPullParserException {
            Log.d(TAG, "clickAndroid: " + json);
            String mType = JsonConvert.analysisJson(json, new TypeToken<String>() {
            }.getType(), "type");
            if (!TextUtils.isEmpty(mType)) {
                switch (mType) {

                    case "ViewDocument":
                        //初始化iAppOffice, 注：先设置授权码，后调用init();
                        iappoffice = new IAppOffice(WebActivity.this);

                        Gson gs = new Gson();
                        LookDocBean lookDocBean = gs.fromJson(json, LookDocBean.class);
                        final String path = lookDocBean.getParams().getPath();

                        if (path.isEmpty()) {
                            ToastUtil.show(getApplicationContext(), "当前文件为空！");
                            return;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadDialog.show();
                            }
                        });
                        Log.d(TAG, "clickAndroid: 1文件下载地址" + path);
                        //如果使用 vpn 的则用365预览
                        VpnSelectBean vpnSelectBean = (VpnSelectBean) SpUtil.getObject(getApplicationContext(), Constant.vpnAccount, VpnSelectBean.class);

                        if (vpnSelectBean != null) {
                            if (vpnSelectBean.isYes()) {
                                Log.d(TAG, "clickAndroid: 测试vpn是否使用到" + vpnSelectBean.isYes());
                                Intent intent = new Intent(getApplicationContext(), Web2Activity.class);
                                intent.putExtra(Constant.off365Url, lookDocBean.getParams().getOffice365());
                                startActivity(intent);
                                return;
                            }
                        }

                        //如果安装了正规的wps，则使用优先使用正规的wps
                        if (WpsUtil.checkWps(getApplicationContext())) {
                            WpsUtil.openFile(getApplicationContext(), path);
                            return;
                        }

                        //判断是否安装了金格的wps
                        if (!iappoffice.isWPSInstalled()) {
                            Intent intent2 = new Intent(getApplicationContext(), Web2Activity.class);
                            intent2.putExtra(Constant.off365Url, lookDocBean.getParams().getOffice365());
                            startActivity(intent2);
                            return;
                        }
                        httpUtil.initSaveFWNGRegInfo(getApplicationContext());//上传手机号码物理地址

                        /********************************初始化iAppOffice*******************************/

                        HashMap<String, String> dat = new HashMap<>();
                        WebServiceUtil.callWebService(Constant.BASE_URL + "/DMS_Phone/WebServices/WebServiceForFWNG.asmx", "GetFWNGKeySN", dat, new WebServiceUtil.WebServiceCallBack() {
                            @Override
                            public void callBack(SoapObject result) {
                                if (result == null) return;
                                Log.d(TAG, "GetFWNGKeySN序列号: " + result.toString());
                                final String GetFWNGKeySN = result.getProperty(0).toString();

                                Log.d(TAG, "callBack: " + GetFWNGKeySN);
                                HashMap<String, String> ddd = new HashMap<>();
                                WebServiceUtil.callWebService(Constant.BASE_URL + "/DMS_Phone/WebServices/WebServiceForFWNG.asmx", "GetWPSKeySN", ddd, new WebServiceUtil.WebServiceCallBack() {
                                    @Override
                                    public void callBack(SoapObject result) {
                                        if (result == null) return;
                                        Log.d(TAG, "GetWPSKeySN序列号: " + result.toString());
                                        String GetWPSKeySN = result.getProperty(0).toString();
                                        Log.d(TAG, "callBack: " + GetWPSKeySN);

                                        iappoffice.setCopyRight(GetFWNGKeySN);//注册金格
                                        iappoffice.init();
                                        iappoffice.setFileProviderAuthor("net.vpnsdk.demo.fileProvider");
                                        iappoffice.setReadOnly(true);
                                        iappoffice.setIsReviseMode(false);
                                        iappoffice.setSerialNumber(GetWPSKeySN);//激活wps

                                        //禁止手写功能
                                        ArrayList<ViewType> list = new ArrayList<ViewType>();
                                        list.add(ViewType.VT_MENU_PEN);
                                        list.add(ViewType.VT_REVIEW_COMMENTREVISE);
                                        iappoffice.setViewHiddenList(list);
                                    }
                                });
                            }
                        });
                        /********************************初始化iAppOffice********************************/

                        // 指定下载地址
                        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(path));
                        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
                        request1.allowScanningByMediaScanner();
                        // 设置通知的显示类型，下载进行时和完成后显示通知
                        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        //request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);*//**隐藏进度条*//*
                        request1.setAllowedOverMetered(true);
                        // 允许该记录在下载管理界面可见
                        request1.setVisibleInDownloadsUi(false);
                        // 允许漫游时下载
                        request1.setAllowedOverRoaming(true);
                        // 允许下载的网路类型
                        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
                        // 设置下载文件保存的路径和文件名
                        //判断文件格式
                        final int lastPoint = path.lastIndexOf(".");
                        String downloadPath = path.substring(lastPoint + 1, path.length());
                        String point = LastPoint.getPoint(downloadPath);

                        if (point.equals(downloadPath)) {
                            Intent intent3 = new Intent(getApplicationContext(), Web2Activity.class);
                            intent3.putExtra(Constant.off365Url, lookDocBean.getParams().getOffice365());
                            startActivity(intent3);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadDialog.dismiss();
                                }
                            });
                            return;
                        }
                        fileName = URLUtil.guessFileName(path, "", point);
                        Log.d(TAG, "fileName:{===}" + fileName);
                        request1.setDestinationInExternalPublicDir("/", fileName);
                        //        另外可选一下方法，自定义下载路径
                        //        request.setDestinationUri()
                        //        request.setDestinationInExternalFilesDir()
                        DownloadManager downloadManager1 = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        // 添加一个下载任务
                        downloadId = downloadManager1.enqueue(request1);
                        Log.d(TAG, "downloadId:{} " + downloadId);


                        break;
                    case "phonebook":
                        //js不对，调不到通讯录,所以省略
                        // MailActivity.newInstance(WebActivity.this);
                        mPopupWindow.dismiss();
                        break;
                    case "logout":
                        //  SpUtil.clear();
                        SpUtil.remove(getApplicationContext(), Constant.Account);
                        SpUtil.remove(getApplicationContext(), Constant.VPN_AUTO);

                        Intent intent2 = new Intent(WebActivity.this, LoginActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        WebActivity.this.startActivity(intent2);
                        finish();
                        break;
                    case "quit":
                        finish();
                        break;

                    case "EditDocument":
                        Log.d(TAG, "clickAndroid: aaaaa啊啊啊啊啊");
                        Log.d(TAG, "打印: " + json);

                        if (json == null) {
                            return;
                        }
                        httpUtil.initSaveFWNGRegInfo(getApplicationContext());//上传手机号码物理地址
                        /********************************初始化iAppOffice******** ***********************/

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadDialog.show();
                            }
                        });

                        //初始化iAppOffice, 注：先设置授权码，后调用init();
                        iappoffice = new IAppOffice(WebActivity.this);
                        if (!iappoffice.isWPSInstalled()) {
                            ToastUtil.show(getApplicationContext(), "请安装金格的WPS！");
                            return;
                        }

                        HashMap<String, String> data = new HashMap<>();
                        WebServiceUtil.callWebService(Constant.BASE_URL + "/DMS_Phone/WebServices/WebServiceForFWNG.asmx", "GetFWNGKeySN", data, new WebServiceUtil.WebServiceCallBack() {
                            @Override
                            public void callBack(SoapObject result) {
                                if (result == null) return;
                                Log.d(TAG, "GetFWNGKeySN序列号: " + result.toString());
                                final String GetFWNGKeySN = result.getProperty(0).toString();

                                Log.d(TAG, "callBack: " + GetFWNGKeySN);
                                HashMap<String, String> ddd = new HashMap<>();
                                WebServiceUtil.callWebService(Constant.BASE_URL + "/DMS_Phone/WebServices/WebServiceForFWNG.asmx", "GetWPSKeySN", ddd, new WebServiceUtil.WebServiceCallBack() {
                                    @Override
                                    public void callBack(SoapObject result) {
                                        if (result == null) return;
                                        Log.d(TAG, "GetWPSKeySN序列号: " + result.toString());
                                        String GetWPSKeySN = result.getProperty(0).toString();
                                        Log.d(TAG, "callBack: " + GetWPSKeySN);

                                        iappoffice.setCopyRight(GetFWNGKeySN);//注册金格
                                        iappoffice.init();
                                        iappoffice.setFileProviderAuthor("net.vpnsdk.demo.fileProvider");
                                        iappoffice.setReadOnly(false);
                                        iappoffice.setIsReviseMode(true);
                                        iappoffice.setSerialNumber(GetWPSKeySN);//激活wps

                                        //禁止手写功能
                                        ArrayList<ViewType> list = new ArrayList<ViewType>();
                                        list.add(ViewType.VT_MENU_PEN);
                                        list.add(ViewType.VT_REVIEW_COMMENTREVISE);
                                        iappoffice.setViewHiddenList(list);
                                    }
                                });
                            }
                        });
                        /********************************初始化iAppOffice********************************/


                        Gson gson = new Gson();
                        TypeBean typeBean = gson.fromJson(json, TypeBean.class);
                        String url = typeBean.getParams().getPath();//下载地址
                        Log.d(TAG, "clickAndroid: " + url);
                        guid = typeBean.getParams().getGuid();
                        //设置编辑留痕人名
                        iappoffice.setUserName(typeBean.getParams().getName());

                        // 指定下载地址
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
                        request.allowScanningByMediaScanner();
                        // 设置通知的显示类型，下载进行时和完成后显示通知
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);/**隐藏进度条*/
                        request.setAllowedOverMetered(true);
                        // 允许该记录在下载管理界面可见
                        request.setVisibleInDownloadsUi(false);
                        // 允许漫游时下载
                        request.setAllowedOverRoaming(true);
                        // 允许下载的网路类型
                        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
                        // 设置下载文件保存的路径和文件名
                        int lastPoint2 = url.lastIndexOf(".");
                        String downloadPath2 = url.substring(lastPoint2 + 1, url.length());
                        String point2 = LastPoint.getPoint(downloadPath2);
                        if (point2.equals("")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadDialog.dismiss();
                                }
                            });
                            ToastUtil.show(getApplicationContext(), "该文件格式不支持预览！");
                            return;
                        }
                        fileName = URLUtil.guessFileName(url, "", point2);

                        Log.d(TAG, "fileName:{}" + fileName);
                        request.setDestinationInExternalPublicDir("/", fileName);
                        //        另外可选一下方法，自定义下载路径
                        //        request.setDestinationUri()
                        //        request.setDestinationInExternalFilesDir()
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        // 添加一个下载任务
                        downloadId = downloadManager.enqueue(request);
                        Log.d(TAG, "downloadId:{} " + downloadId);

                        break;
                    default:

                        break;
                }
            }
        }
    }

    /**
     * 接收下载完成后的广播
     */
    BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (completeDownloadId == downloadId) {
                Log.w(TAG, "下载完成");
                //确保下载完后1s之后再打开，确保没问题
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "确保下载完后1s之后再打开，确保没问题");
                        final String name = Environment.getExternalStorageDirectory().getPath().toString() + "/" + fileName;
                        iappoffice.setFileName(name);
                        iappoffice.appOpen(true);
                    }
                }, 1000);
                /*statusBar.setProgress(100);
                statusText.setText("100%");*/
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadDialog.dismiss();
            }
        });
        Log.d("-----------------------", "onResume: " + LastUrl);
        /** 注册下载完成接收广播 **/
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //规划局的要隐藏该功能
        /*if (LastUrl.length()>0){
            mWebView.loadUrl(LastUrl);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (iappoffice != null) {
            iappoffice.unInit();
        }
        unregisterReceiver(saveReceiver);
        unregisterReceiver(downloadCompleteReceiver);

        //因为有些单位不需要的用到vpn，所以需要判断才可以，要不然会错误
        if (VPNManager.getInstance() != null) {
            Log.d(TAG, "onDestroy: 杀了吗？");
            VPNManager.getInstance().stopVPN();
            VPNManager.getInstance().cancelLogin();
            //stopService(new Intent(getApplicationContext(), MyServpce.class));//停止服务
        }
        if (null != mWebView) {
            mWebView.destroy();
        }
    }

    /**
     * 接受广播
     */
    public class SaveReceiver extends BroadcastReceiver {
        public SaveReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + fileName);
            String action = intent.getAction();
            if (action.equals("com.kinggrid.iappoffice.close")) {

                httpUtil.UnlockFile(guid);//解锁
                /***退出wps之后40s删除文件**/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/" + fileName);
                        file.delete();
                        Log.d(TAG, "run: 删除文件成功");
                    }
                }, 10000);

            } else if (action.equals("com.kinggrid.iappoffice.save")) {
                try {
                    FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getPath().toString() + "/" + fileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int leng = 1024 * 1024;//文件读取缓存空间
                    byte[] buffer = new byte[leng];//分配缓存内存
                    int count = 0;
                    while ((count = fis.read(buffer)) >= 0) {
                        baos.write(buffer, 0, count);
                    }//获取文件所有数据
                    //开始分段上传
                    Log.d(TAG, "打印文件大小: " + baos.toByteArray().length);
                    byte[] filedata = baos.toByteArray();//输出byte数据数组
                    fis.close();
                    baos.flush();
                    int uploadlen = 1024 * 1024;//分块大小
                    int total = (int) Math.ceil((double) filedata.length / (double) uploadlen);//计算总块数
                    if (total == 1)//只有一块
                    {
                        String uploadBuffer = new String(Base64.encode(filedata, 0));
                        uploadWebService(0, total, uploadBuffer);//小文件上传
                    } else {
                        uploadsWebService(0, filedata);//大文件上传
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals("com.kinggrid.iappoffice.back")) {
                Log.d(TAG, "onReceive: com.kinggrid.iappoffice.back");
            } else if (action.equals("com.kinggrid.iappoffice.home")) {
                Log.d(TAG, "onReceive: com.kinggrid.iappoffice.home");
            }
        }
    }


    /**
     * 小文件上传
     */

    @SuppressLint("HandlerLeak")
    private void uploadWebService(int offset, int total, final String uploadBuffer) {

        HashMap<String, String> data = new HashMap<>();
        data.put("input", uploadBuffer);
        data.put("offset", offset + "");
        data.put("total", total + "");
        data.put("GUID", guid);
        WebServiceUtil.callWebService(Constant.UPLOAD_URL, "UploadFile", data, new WebServiceUtil.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                Log.d(TAG, "callBack: " + result.toString());
            }
        });
    }

    /**
     * 大文件上传
     *
     * @param offset
     * @param filedata
     */
    @SuppressLint("HandlerLeak")
    private void uploadsWebService(final int offset, final byte[] filedata) {//offset：当前块，filedata：当前文件总数据
        final int uploadlen = 1024 * 1024;//分块大小
        final int total = (int) Math.ceil((double) filedata.length / (double) uploadlen);//计算总块数

        int nowlen = (filedata.length - offset * uploadlen) < uploadlen ? filedata.length - offset * uploadlen : uploadlen;//计算当前块大小
        byte[] data = new byte[nowlen];//分配内存
        int j = 0;//记录内存偏移
        for (int i = offset * uploadlen; i < offset * uploadlen + nowlen; i++) {
            data[j] = filedata[i];
            j++;
        }//获取当前块的数据
        String uploadBuffer = new String(Base64.encode(data, 0));
        final HashMap<String, String> uploaddata = new HashMap<>();
        uploaddata.put("input", uploadBuffer);
        uploaddata.put("offset", offset + "");
        uploaddata.put("total", total + "");
        uploaddata.put("GUID", guid);

        WebServiceUtil.callWebService(Constant.UPLOAD_URL, "UploadFile", uploaddata, new WebServiceUtil.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                if (result != null)
                    Log.d(TAG, "callBack: " + result.toString());
                //在这里加入是否成功判断 ,上传成功 offset+=1;上传失败offset不变;
                int noffset = offset + 1;
                if (noffset >= total)//如果上传完成
                {
                    Log.d(TAG, "上传成功");
                } else {
                    uploadsWebService(noffset, filedata);
                }
            }

        });
    }

}
