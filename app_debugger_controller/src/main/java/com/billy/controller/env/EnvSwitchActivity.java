package com.billy.controller.env;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.controller.BaseActivity;
import com.billy.controller.R;
import com.billy.controller.core.ConnectionStatus;
import com.billy.controller.core.IServerMessageProcessor;
import com.billy.controller.core.ServerMessageProcessorManager;

import java.util.HashMap;

/**
 * 环境切换页
 * @author billy.qi
 * @since 17/6/14 17:54
 */
public class EnvSwitchActivity extends BaseActivity implements IServerMessageProcessor {

    private TextView currentEnvTextView;

    private static HashMap<String, Integer> map = new HashMap<String, Integer>(){ {
        put("sit", R.string.env_type_sit);
        put("pre", R.string.env_type_pre);
        put("prd", R.string.env_type_release);
    } };
    private static HashMap<String, Integer> mapRadio = new HashMap<String, Integer>(){ {
        put("sit", R.id.env_type_sit);
        put("pre", R.id.env_type_pre);
        put("prd", R.id.env_type_release);
    } };
    private static SparseArray<String> arr = new SparseArray<String>(){ {
        put(R.id.env_type_sit, "sit");
        put(R.id.env_type_pre, "pre");
        put(R.id.env_type_release, "prd");
    } };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env);
        currentEnvTextView = (TextView) findViewById(R.id.env_type_current);
        ServerMessageProcessorManager.addProcessor(this);
        RadioGroup group = (RadioGroup) findViewById(R.id.log_level_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                String message = arr.get(checkedId);
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(EnvSwitchActivity.this, R.string.env_invalid_type, Toast.LENGTH_SHORT).show();;
                } else {
                    ServerMessageProcessorManager.sendMessageToClient(EnvSwitchActivity.this, message);
                }
            }
        });
        ServerMessageProcessorManager.sendMessageToClient(EnvSwitchActivity.this, "get");
    }

    @Override
    public void onStatus(ConnectionStatus status) {
        if (status == ConnectionStatus.STOPPED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    public void onMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Integer resId = map.get(message);
                if (resId != null) {
                    currentEnvTextView.setText(resId);
                    Integer id = mapRadio.get(message);
                    if (id != null) {
                        RadioButton view = (RadioButton) findViewById(id);
                        view.setChecked(true);
                    }
                } else {
                    Toast.makeText(EnvSwitchActivity.this, getString(R.string.env_unkunow_type, message), Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }

    @Override
    public String getDebugKey() {
        return "evn_switch";
    }

    @Override
    protected void onDestroy() {
        ServerMessageProcessorManager.removeProcessor(this);
        super.onDestroy();
    }
}
