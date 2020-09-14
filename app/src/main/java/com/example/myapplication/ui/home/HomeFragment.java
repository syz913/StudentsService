package com.example.myapplication.ui.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.myapplication.R;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String url = "https://zlapp.fudan.edu.cn/site/ncov/fudanDaily";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestLocation();
        initView();
    }
    private void requestLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            if (PermissionsUtil.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //有获取地理位置的权限
            } else {
                PermissionsUtil.requestPermission(getActivity(), new PermissionListener() {
                    @Override
                    public void permissionGranted(@NonNull String[] permissions) {
                        //用户授予权限
                    }

                    @Override
                    public void permissionDenied(@NonNull String[] permissions) {
                        //用户拒绝了权限申请
                    }
                }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            }
        } else {
            Log.e("BRG", "系统检测到未开启GPS定位服务");
            Toast.makeText(getActivity(), "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }

    private void initView() {
        webView = (WebView) getActivity().findViewById(R.id.school_bus_view);
        swipeRefreshLayout = getActivity().findViewById(R.id.swipe_contain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新加载刷新页面
                webView.loadUrl(webView.getUrl());
            }
        });

        //为SwipeRefreshLayout设置刷新时的颜色变化，最多可以设置4种
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        webView.getSettings().setJavaScriptEnabled(true); //与js交互
        webView.getSettings().setDomStorageEnabled(true); //开启DOM形式存储
        webView.getSettings().setDatabaseEnabled(true); //开启数据库形式存储
        String appCacheDir = getActivity().getDir("cache", Context.MODE_PRIVATE).getPath();   //缓存数据的存储地址
        webView.getSettings().setAppCachePath(appCacheDir);
        webView.getSettings().setAppCacheEnabled(true); //开启缓存功能
        webView.getSettings().setCacheMode(webView.getSettings().LOAD_DEFAULT);      //缓存模式
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.loadUrl(url);
        //设置在当前WebView继续加载网页
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
    }

    class MyWebViewClient extends WebViewClient {
        @Override  //WebView代表是当前的WebView
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //表示在当前的WebView继续打开网页
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("WebView", "开始访问网页");
            //延迟加载图片
            webView.getSettings().setBlockNetworkImage(true);
            //提高渲染的优先级
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("WebView", "访问网页结束");
//            DialogUtils.closeDialog(dialog);
            webView.getSettings().setBlockNetworkImage(false);
            if (!webView.getSettings().getLoadsImagesAutomatically()) {
                webView.getSettings().setBlockNetworkImage(false);
                webView.getSettings().setLoadsImagesAutomatically(true);
            }
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override //监听加载进度
        public void onProgressChanged(WebView view, int newProgress) {
            //通过监听网页加载newProgress来判断是否显示进度条，当达到100时隐藏进度条，未达到100时，更新进度条的指示值就可以了
            if (newProgress == 100) {
                swipeRefreshLayout.setRefreshing(false);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override//接受网页标题
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //把当前的Title设置到Activity的title上显示
            getActivity().setTitle(title);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    private void clickCheckMe() {
        if (webView == null) return;
        //View view = webView.getChildAt(0);
        setSimulateClick(webView, 600, 460);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSimulateClick(webView, 600, 660);
            }
        }, 3000);
    }

    private void setSimulateClick(View view, float x, float y) {
        System.out.println("自动点击 : " + x + " " + y);
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        // downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
//	        view.onTouchEvent(downEvent);
//	        view.onTouchEvent(upEvent);
        view.dispatchTouchEvent(downEvent);
        view.dispatchTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }
}