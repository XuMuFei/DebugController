package com.billy.controller;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.billy.controller.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import static com.billy.controller.util.PreferenceUtil.KEY_PACKAGE_NAME;

/**
 * @author billy.qi
 * @since 17/6/1 14:16
 */
public class WelcomeActivity extends BaseActivity {

    private AutoCompleteTextView editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        editText = (AutoCompleteTextView) findViewById(R.id.et_debug_app_package_name);
        String packageName = PreferenceUtil.getString(KEY_PACKAGE_NAME, "");
        editText.setText(packageName);

        String [] arr = getPackageNameList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        editText.setAdapter(arrayAdapter);
        findViewById(R.id.btn_connection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.putString(KEY_PACKAGE_NAME, editText.getText().toString().trim());
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private String[] getPackageNameList() {
        //获取手机中所有已安装的应用，并判断是否系统应用
        ArrayList<String> appList = new ArrayList<>(); //用来存储获取的应用信息数据，手机上安装的应用数据都存在appList里
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for(int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            //判断是否系统应用
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //非系统应用
                appList.add(packageInfo.packageName);
            }
        }
        return appList.toArray(new String[]{});
    }
}
