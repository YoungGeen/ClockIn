package com.ljsdysq.clockin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

public class GpsChoose {
    public final Context context;
    public final WeWorkHook weWorkHook;

    public GpsChoose(Context context, WeWorkHook weWorkHook) {
        this.weWorkHook = weWorkHook;
        this.context = context;
        this.gpsChooseDialog();
    }

    public void gpsChooseDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        WebView webView = new WebView(context);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String[] latngs = uri.getQueryParameter("latng").split(",");
                String placeName = uri.getQueryParameter("name");
                String placeAddress = uri.getQueryParameter("addr");
                if (latngs.length < 2) {
                    Toast.makeText(context, "獲取位置失敗", Toast.LENGTH_SHORT).show();
                    return true;
                }
                weWorkHook.sp.edit().putString("LATITUDE", latngs[0]).apply();
                weWorkHook.sp.edit().putString("LONGITUDE", latngs[1]).apply();
                weWorkHook.sp.edit().putString("PLACE_NAME", placeName).apply();
                weWorkHook.sp.edit().putString("PLACE_ADDRESS", placeAddress).apply();
                SystemLocationHook sysLocationHook = weWorkHook.sysLocationHook;
                sysLocationHook.latitude = Double.parseDouble(latngs[0]);
                sysLocationHook.longitude = Double.parseDouble(latngs[0]);
                MapHook mapHook = weWorkHook.mapHook;
                mapHook.latitude = Double.parseDouble(latngs[0]);
                mapHook.longitude = Double.parseDouble(latngs[0]);
                weWorkHook.mapHook.placeAddress = placeAddress;
                weWorkHook.mapHook.placeName = placeName;
                weWorkHook.dataSetting.latitude.setText(latngs[0]);
                weWorkHook.dataSetting.longitude.setText(latngs[1]);
                weWorkHook.dataSetting.name.setText(placeName);
                weWorkHook.dataSetting.address.setText(placeAddress);
                view.destroy();
                dialog.dismiss();
                return true;
            }
        });
        webView.loadUrl("你申請的騰訊地圖選點url");
        webView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        dialog.setContentView(webView);
        dialog.setOnCancelListener((DialogInterface dialogInterface0) -> webView.destroy());
        WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        params.width = (int) (((double) this.context.getResources().getDisplayMetrics().widthPixels) * 0.9);
        params.height = (int) (((double) this.context.getResources().getDisplayMetrics().heightPixels) * 0.8);
        dialog.show();
    }
}
