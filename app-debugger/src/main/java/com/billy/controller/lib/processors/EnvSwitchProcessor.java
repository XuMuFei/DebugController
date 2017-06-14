package com.billy.controller.lib.processors;

import android.content.Context;
import android.text.TextUtils;

import com.billy.controller.lib.core.AbstractMessageProcessor;
import com.billy.controller.lib.core.PreferenceUtil;
import com.billy.controller.lib.core.PreferenceUtil.SharedPreference;

/**
 * 环境切换
 * @author billy.qi
 * @since 17/6/14 15:59
 */
public class EnvSwitchProcessor extends AbstractMessageProcessor {
    private static final String FILE_NAME = "bl_shared_preference_name";

    private static PreferenceUtil PREFERENCE = new PreferenceUtil(FILE_NAME, Context.MODE_PRIVATE);

    private SharedPreference<String> env_type = PREFERENCE.value( "bl_network_env_type", "");

    @Override
    public void onMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            if ("get".equals(message)) {
                sendCurEnvToServer();
            } else {
                env_type.put(message);
                sendMessage(message);
            }
        }
    }

    @Override
    public String getKey() {
        return "evn_switch";
    }

    @Override
    public void onConnectionStart(Context context) {
        PREFERENCE.init(context);
        sendCurEnvToServer();
    }

    private void sendCurEnvToServer() {
        String type = env_type.get();
        if (!TextUtils.isEmpty(type)) {
            sendMessage(type);
        }
    }

    @Override
    public void onConnectionStop() {

    }
}
