package com.billy.controller.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author billy.qi
 * @since 17/5/29 19:07
 */
public class DebugListenerManager {
    private static final char SEPRATOR = ':';
    public static final String KEY_STATUS = "status";
    public static final String KEY_LOG = "log";

    private static HashMap<String, List<IDebugListener>> allListeners = new HashMap<>();

    public static void addListener(IDebugListener listener) {
        if (listener != null && listener.getDebugKey() != null) {
            String debugKey = listener.getDebugKey();
            List<IDebugListener> debugListeners = allListeners.get(debugKey);
            if (debugListeners == null) {
                debugListeners = new LinkedList<>();
                allListeners.put(debugKey, debugListeners);
            }
            if (!debugListeners.contains(listener)) {
                debugListeners.add(listener);
            }
        }
    }

    public static void removeListener(IDebugListener listener) {
        if (listener != null && listener.getDebugKey() != null) {
            List<IDebugListener> debugListeners = allListeners.get(listener.getDebugKey());
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

    public static void onStatus(Status status) {
        List<IDebugListener> listeners = allListeners.get(KEY_STATUS);
        if (listeners != null && !listeners.isEmpty()) {
            for (IDebugListener listener : listeners) {
                listener.onStatus(status);
            }
        }
    }

    public static void onMessage(String message) {
        if (message != null) {
            int index = message.indexOf(SEPRATOR);
            if (index >= 0 && message.length() > index + 1) {
                String key = message.substring(0, index);
                String content = message.replaceFirst(key + SEPRATOR, "");
                List<IDebugListener> listeners = allListeners.get(key);
                if (listeners != null && !listeners.isEmpty()) {
                    for (IDebugListener listener : listeners) {
                        listener.onMessage(content);
                    }
                }
            }
        }
    }


}
