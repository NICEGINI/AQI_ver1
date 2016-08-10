package kr.ac.kmu.cs.airpollution.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.ac.kmu.cs.airpollution.R;

/**
 * Created by KCS on 2016-08-03.
 */
public class WebControlActivity extends AppCompatActivity {
    private WebView webView;
    private static String Url;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_webview);

        webView = (WebView)findViewById(R.id.wv_Loginsite);

        webView.setWebViewClient(new CallBrowser());
        //webView = new WebView(this);
        webView.setWebChromeClient(new WebChromeClient());
        setWebView(webView);
    }

    //setting display webview
    public void setWebView(View v){
        webView.setInitialScale(290);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(v.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Url);
    }

    private class CallBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public static void setUrl(String url){
        Url = url;
    }

}
