package com.billy.controller.lib.core;

import android.text.TextUtils;

/**
 * @author billy.qi
 * @since 17/5/31 14:36
 */
public abstract class AbstractMessageProcessor {
    private String key;

    protected final void sendMessage(String message) {
        if (isRunning()) {
            if (TextUtils.isEmpty(key)) {
                key = getKey() + ":";
            }
            MessageCache.put(key + message);
        }
    }

    /**
     * 接收到debug控制台的消息
     * @param message 消息内容
     */
    public abstract void onMessage(String message);

    /**
     * 获取当前processor的消息类型
     * 消息是以 key:value的形式组成字符串经socket通道发送给debug控制台
     * @return 当前类型的消息对应的key
     */
    public abstract String getKey();

    /**
     * 获取当前debug连接状态
     * @return true：已连接，false：未连接
     */
    protected boolean isRunning() {
        return ControllerService.running.get();
    }

    public abstract void onConnectionStart();

    public abstract void onConnectionStop();
}
