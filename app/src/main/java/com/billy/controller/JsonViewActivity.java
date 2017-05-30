package com.billy.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.controller.core.JsonFormat;

/**
 * @author billy.qi
 * @since 17/5/30 15:13
 */
public class JsonViewActivity extends AppCompatActivity {

    public static final String EXTRA_CONTENT = "content";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new com.androidessence.pinchzoomtextview.PinchZoomTextView(this);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setVerticalScrollBarEnabled(true);
        textView.setHorizontallyScrolling(true);
        textView.setTextIsSelectable(true);
        setContentView(textView);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        if (!TextUtils.isEmpty(content)) {
            try{
                String json = JsonFormat.format(content);
                textView.setText(json + json + json + json);
            } catch(Exception e) {
                e.printStackTrace();
                textView.setText("格式化出错：\n" + content);
            }
        } else {
            textView.setText("json字符串为空");
        }
        Toast.makeText(this, "支持手势缩放", Toast.LENGTH_SHORT).show();
    }

}
