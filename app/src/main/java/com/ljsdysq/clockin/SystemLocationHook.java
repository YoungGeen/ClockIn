package com.ljsdysq.clockin;

import android.location.Location;
import android.os.SystemClock;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SystemLocationHook {
    public final WeWorkHook weWorkHook;
    public double latitude;
    public double longitude;

    public SystemLocationHook(WeWorkHook context) {
        this.weWorkHook = context;
        this.latitude = Double.parseDouble(context.sp.getString("LATITUDE",""));
        this.longitude = Double.parseDouble(context.sp.getString("LONGITUDE",""));
    }

    public void startHook() {
        Class<?> locationManager = null;
        Class<?> location = null;
        try {
            locationManager = weWorkHook.mClassloader.loadClass("android.location.LocationManager");
            location = weWorkHook.mClassloader.loadClass("android.location.Location");
        } catch (Exception e) {
            XposedBridge.log("class not found "+e.getMessage());
            return;
        }

        XposedHelpers.findAndHookMethod(locationManager, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!weWorkHook.sp.getBoolean("IS_OPEN",false)) {
                    return;
                }
                Location l = new Location(((String) param.args[0]));
                l.setLatitude(latitude);
                l.setLongitude(longitude);
                l.setAccuracy(100.0f);
                l.setTime(System.currentTimeMillis());
                l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                param.setResult(l);
            }
        });
        XposedHelpers.findAndHookMethod(location, "getLatitude", new XC_MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!weWorkHook.sp.getBoolean("IS_OPEN",false)) {
                    return;
                }
                param.setResult(latitude);
            }
        });
        XposedHelpers.findAndHookMethod(location, "getLongitude", new XC_MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (!weWorkHook.sp.getBoolean("IS_OPEN",false)) {
                    return;
                }
                param.setResult(longitude);
            }
        });
    }
}
