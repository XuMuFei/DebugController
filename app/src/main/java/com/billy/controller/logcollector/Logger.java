package com.billy.controller.logcollector;

import android.util.Log;

/**
 * @author billy.qi
 * @since 17/5/25 11:20
 */
public class Logger {

    static void log(String content) {
        Log.i("collector", content);
    }
}
