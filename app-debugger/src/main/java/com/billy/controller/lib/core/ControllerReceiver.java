package com.billy.controller.lib.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.security.MessageDigest;

/**
 * @author billy.qi
 * @since 17/5/25 13:15
 */
public class ControllerReceiver extends BroadcastReceiver {

    private static final String META_KEY_PACKAGE_NAME = "debugger_package_name";
    private static final String META_KEY_SIGN = "debugger_sign";

    /**
     * 将会判断消息发送者是否合法 (不接受非法app)
     * 默认只接收符合下列条件的app
     *      1. 包名为com.billy.controller
     *      2. 签名用getMetaData(Context context, String key) 获取的值为d0de25a2855e83080290318caea6aa5f
     *
     * 如果要修改,可以在AndroidManifest.xml的application中创建meta-data子节点
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean validSender = isValidSender(context);
        if (validSender) {
            String ip = intent.getStringExtra("ip");
            int port = intent.getIntExtra("port", -1);
            if (!TextUtils.isEmpty(ip) && port > 0) {
                Intent serviceIntent = new Intent(context, ControllerService.class);
                serviceIntent.putExtras(intent);
                context.startService(serviceIntent);
            }
        }
    }

    /**
     * 判断消息发送者是否合法 (不接受非法app)
     */
    private boolean isValidSender(Context context) {
        boolean validSender = false;
        try {
            String packageName = getMetaData(context, META_KEY_PACKAGE_NAME);
            if (TextUtils.isEmpty(packageName)) {
                packageName = "com.billy.controller";//默认为app-debugger包名
            }
            String signMd5 = getMetaData(context, META_KEY_SIGN);
            if (TextUtils.isEmpty(signMd5)) {
                signMd5 = "d0de25a2855e83080290318caea6aa5f";//默认为demo.jks的签名信息md5
            }
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            Signature signature = signatures[0];
            String md5Str = md5(signature);
            if (signMd5.equals(md5Str)) {
                validSender = true;
            } else {
                Log.e("ControllerReceiver", "app-debugger signMd5 error! app with package name '" + packageName + "' signMd5=" + md5Str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return validSender;
    }

    private String getMetaData(Context context, String key) {
        try{
            ApplicationInfo appInfo = context.getApplicationContext().getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null) {
                return bundle.getString(key);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5(Signature signature) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    signature.toByteArray());
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
