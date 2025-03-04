package com.ljsdysq.clockin;


import android.content.Context;
import android.content.SharedPreferences;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WeWorkHook {
    public XC_LoadPackage.LoadPackageParam loadPackageParam;
    public Context mContext;
    public ClassLoader mClassloader;
    public SharedPreferences sp;
    public MapHook mapHook;
    public SystemLocationHook sysLocationHook;
    public DataSetting dataSetting;
    public WeWorkHook(Context context, ClassLoader classLoader, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
        this.mContext = context;
        this.mClassloader = classLoader;
        this.sp = context.getSharedPreferences("clock_in", Context.MODE_PRIVATE);
    }
    public void hookWW() {
        mapHook = new MapHook(this);
        mapHook.startHook();
        sysLocationHook = new SystemLocationHook(this);
        sysLocationHook.startHook();
        dataSetting = new DataSetting(this);
        dataSetting.startHook();
    }


}
