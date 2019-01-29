package net.vpnsdk.wanve.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.vpnsdk.demo.R;
import net.vpnsdk.wanve.base.Constant;
import net.vpnsdk.wanve.utils.SpUtil;
import net.vpnsdk.wanve.utils.ToastUtil;


public class Web2Activity extends AppCompatActivity {

    private static final String TAG = "Web2Activity";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);
        WebView mWebView = findViewById(R.id.webView);

        String path = getIntent().getStringExtra(Constant.off365Url);
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
        mWebView.setWebViewClient(mWebViewClient);//这个一定要设置，要不然不会再本应用中加载
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setSupportZoom(true);
        /**
         * webView白屏问题
         */
        mWebView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        mWebView.loadUrl(path);
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
        }
    };
}
