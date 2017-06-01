package com.billy.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.billy.controller.util.PreferenceUtil;

import static com.billy.controller.util.PackageUtil.getAppName;
import static com.billy.controller.util.PackageUtil.getPackageNameList;
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
                String pkg = editText.getText().toString().trim();
                PreferenceUtil.putString(KEY_PACKAGE_NAME, pkg);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("appName", getAppName(pkg));
                startActivity(intent);
            }
        });
    }

}
