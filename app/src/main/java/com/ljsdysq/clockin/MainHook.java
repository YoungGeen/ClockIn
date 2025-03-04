package com.ljsdysq.clockin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.ljsdysq.clockin")){
            XposedHelpers.findAndHookMethod("com.ljsdysq.clockin.MainActivity",lpparam.classLoader,
                    "isActivate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult("已激活");
                }
            });
        }
        if (lpparam.packageName.equals("com.tencent.wework")){
            XposedHelpers.findAndHookMethod(Application.class,"attach", Context.class,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context context = (Context) param.args[0];
                    new WeWorkHook(context, context.getClassLoader(), lpparam).hookWW();
                }
            });
        }
    }
}
