package com.billy.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.billy.controller.core.ConnectionService;
import com.billy.controller.core.DebugListenerManager;
import com.billy.controller.core.IDebugListener;
import com.billy.controller.core.Status;
import com.billy.controller.log.collector.LogActivity;

import static com.billy.controller.core.Status.RUNNING;
import static com.billy.controller.core.Status.STOPPED;

/**
 * @author billy.qi
 * @since 17/5/26 12:49
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, IDebugListener {

    private Button mBtnOnOff;
    private Status status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnOnOff = (Button)findViewById(R.id.log_on_off);
        mBtnOnOff.setOnClickListener(this);
        setOnClickListeners(this, R.id.btn_log);
        status = STOPPED;
        ConnectionService.addListener(this);
        processOnOff();//开启连接服务 或 初始化显示当前连接状态
    }

    private void setOnClickListeners(View.OnClickListener listener, @IdRes int... btnIds) {
        if (btnIds != null && btnIds.length > 0) {
            for (int id : btnIds) {
                View view = findViewById(id);
                if (view != null) {
                    view.setOnClickListener(listener);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Class<? extends Activity> toActivity = null;
        switch (v.getId()) {
            case R.id.btn_log: toActivity = LogActivity.class; break;
            case R.id.log_on_off: processOnOff(); return;
        }
        if (toActivity != null) {
            startActivity(new Intent(this, toActivity));
        }
    }

    private void processOnOff() {
        Intent intent = new Intent(this, ConnectionService.class);
        if (status == RUNNING) {
            intent.putExtra("stop", true);
        }
        startService(intent);
    }

    Runnable refreshStatusButton = new Runnable() {
        @Override
        public void run() {
            if (status != null) {
                mBtnOnOff.setText(status.resId);
            }
        }
    };

    @Override
    public void onStatus(Status st) {
        if (st != null) {
            status = st;
            runOnUiThread(refreshStatusButton);
        }
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public String getDebugKey() {
        return DebugListenerManager.KEY_STATUS;
    }

    @Override
    protected void onDestroy() {
        ConnectionService.removeListener(this);
        super.onDestroy();
    }
}
