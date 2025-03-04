package com.ljsdysq.clockin;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MapHook {
    public final WeWorkHook weWorkHook;
    public double latitude;
    public double longitude;
    public String placeName;
    public String placeAddress;
    public boolean isOpen;

    public MapHook(WeWorkHook context) {
        this.weWorkHook = context;
        this.latitude = Double.parseDouble(context.sp.getString("LATITUDE",""));
        this.longitude = Double.parseDouble(context.sp.getString("LONGITUDE",""));
        this.placeName = context.sp.getString("PLACE_NAME","");
        this.placeAddress = context.sp.getString("PLACE_ADDRESS","");
    }

    public final void locationParamHook(XC_MethodHook.MethodHookParam methodHookParam) {

        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getLatitude", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult(MapHook.this.latitude);
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getLongitude", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult(MapHook.this.longitude);
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getName", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getCity", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getStreet", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getDistrict", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getProvince", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (isOpen) {
                    param.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod(methodHookParam.args[0].getClass(), "getPoiList", new XC_MethodHook() {
            @Override
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (!isOpen) {
                    return;
                }
                List<?> list = (List<?>) param.getResult();
                if (list == null) {
                    return;
                }
                Class<?> clazz = list.get(0).getClass();
                XposedHelpers.findAndHookMethod(clazz, "getAddress", new XC_MethodHook() {
                    public void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (isOpen) {
                            param.setResult(MapHook.this.placeAddress);
                        }
                    }
                });
                XposedHelpers.findAndHookMethod(clazz, "getName", new XC_MethodHook() {
                    public void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (isOpen) {
                            param.setResult(MapHook.this.placeName);
                        }
                    }
                });
            }
        });
    }

    public void startHook() {

        Class<?> tencentLocationManager;
        Class<?> tencentLocation;
        try {
            tencentLocationManager = weWorkHook.mClassloader.loadClass("com.tencent.map.geolocation.sapp.TencentLocationManager");
            tencentLocation = weWorkHook.mClassloader.loadClass("com.tencent.map.geolocation.sapp.TencentLocation");
        } catch (ClassNotFoundException e) {
            XposedBridge.log("class not found!"+e.getMessage());
            return;
        }

        Class<?> finalTencentLocation = tencentLocation;
        XposedBridge.hookAllMethods(tencentLocationManager, "requestLocationUpdates", new XC_MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.findAndHookMethod(param.args[1].getClass(), "onLocationChanged",
                        finalTencentLocation, Integer.TYPE, String.class, new XC_MethodHook() {
                            @Override
                            public void beforeHookedMethod(MethodHookParam hookParam) throws Throwable {
                                super.beforeHookedMethod(hookParam);
                                isOpen = weWorkHook.sp.getBoolean("IS_OPEN",false);
                                if (isOpen) {
                                    locationParamHook(hookParam);
                                }
                            }
                        });
            }
        });
    }
}
