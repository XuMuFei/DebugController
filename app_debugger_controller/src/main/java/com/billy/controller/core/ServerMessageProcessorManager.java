package com.billy.controller.core;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author billy.qi
 * @since 17/5/29 19:07
 */
public class ServerMessageProcessorManager {
    static final String SEPARATOR = ":";
    public static final String KEY_STATUS = "status";
    public static final String KEY_LOG = "log";

    private static HashMap<String, List<IServerMessageProcessor>> allListeners = new HashMap<>();

    public static void addListener(IServerMessageProcessor listener) {
        if (listener != null && listener.getDebugKey() != null) {
            String debugKey = listener.getDebugKey();
            List<IServerMessageProcessor> debugListeners = allListeners.get(debugKey);
            if (debugListeners == null) {
                debugListeners = new LinkedList<>();
                allListeners.put(debugKey, debugListeners);
            }
            if (!debugListeners.contains(listener)) {
                debugListeners.add(listener);
            }
        }
    }

    public static void removeListener(IServerMessageProcessor listener) {
        if (listener != null && listener.getDebugKey() != null) {
            List<IServerMessageProcessor> debugListeners = allListeners.get(listener.getDebugKey());
            if (debugListeners != null) {
                if (debugListeners.contains(listener)) {
                    debugListeners.remove(listener);
                }
            }
        }
    }

    public static void clear() {
        allListeners.clear();
    }

    static void onStatus(ConnectionStatus status) {
        List<IServerMessageProcessor> listeners = allListeners.get(KEY_STATUS);
        if (listeners != null && !listeners.isEmpty()) {
            for (IServerMessageProcessor listener : listeners) {
                listener.onStatus(status);
            }
        }
    }

    static void onMessage(String message) {
        if (message != null) {
            int index = message.indexOf(SEPARATOR);
            if (index >= 0 && message.length() > index + 1) {
                String key = message.substring(0, index);
                String content = message.replaceFirst(key + SEPARATOR, "");
                List<IServerMessageProcessor> listeners = allListeners.get(key);
                if (listeners != null && !listeners.isEmpty()) {
                    for (IServerMessageProcessor listener : listeners) {
                        listener.onMessage(content);
                    }
                }
            }
        }
    }

    public static void sendMessageToClient(IServerMessageProcessor listener, String message) {
        if (listener != null && !TextUtils.isEmpty(message)) {
            String key = listener.getDebugKey();
            if (!TextUtils.isEmpty(key)) {
                senMessageToClient(key, message);
            }
        }
    }

    public static void senMessageToClient(String key, String message) {
        ServerMessageCache.put(key + SEPARATOR + message);
    }

    static void sendMessageToBreakMessageCacheGetMethod() {
        ServerMessageCache.put(SEPARATOR);
    }

}
