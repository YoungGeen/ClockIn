package com.ljsdysq.clockin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class DataSetting {
    public final WeWorkHook weWorkHook;
    public TextView latitude;
    public TextView longitude;
    public TextView name;
    public TextView address;
    private Button choose;

    public DataSetting(WeWorkHook context) {
        this.weWorkHook = context;
    }

    public final void showSetting(Context context) {
        View view = null;
        try {
            view = LayoutInflater.from(context.createPackageContext("com.ljsdysq.clockin", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY)).inflate(R.layout.setting, null, false);
        } catch (PackageManager.NameNotFoundException e) {
            XposedBridge.log("view not found "+e.getMessage());
        }
        boolean isOpen = weWorkHook.sp.getBoolean("IS_OPEN",false);
        Switch open = view.findViewById(R.id.hook_open);
        open.setChecked(isOpen);
        open.setOnCheckedChangeListener((buttonView, isChecked) -> {
            weWorkHook.sp.edit().putBoolean("IS_OPEN", isChecked).apply();
        });
        choose = view.findViewById(R.id.choose);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        choose.setOnClickListener(v -> new GpsChoose(context,weWorkHook));
        latitude.setText(weWorkHook.sp.getString("LATITUDE",""));
        longitude.setText(weWorkHook.sp.getString("LONGITUDE",""));
        name.setText(weWorkHook.sp.getString("PLACE_NAME",""));
        address.setText(weWorkHook.sp.getString("PLACE_ADDRESS",""));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle("位置選擇");
        builder.setCancelable(true);
        builder.setPositiveButton("取消", (DialogInterface dialogInterface,int v) -> dialogInterface.dismiss());
        builder.show();
    }

    public void startHook() {
        Class<?> WwMainActivity;
        try {
            WwMainActivity = weWorkHook.mClassloader.loadClass("com.tencent.wework.setting.controller.SettingDetailActivity");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        XposedHelpers.findAndHookMethod(WwMainActivity, "bindView", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String fieldName = "mNewHelpCenter";
                ((View)XposedHelpers.getObjectField(param.thisObject, fieldName)).setOnLongClickListener(view -> {
                    Context context = view.getContext();
                    DataSetting.this.showSetting(context);
                    return true;
                });
            }
        });
    }

}
