package com.billy.controller.lib;

import com.billy.controller.lib.core.AbstractMessageProcessor;
import com.billy.controller.lib.processors.LogMessageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author billy.qi
 * @since 17/5/31 14:33
 */
public class DebugController {


    private static final List<AbstractMessageProcessor> PROCESSORS = new ArrayList<>();

    static {
        addProcessor(new LogMessageProcessor());//默认添加一个日志监控
    }

    public static void addProcessor(AbstractMessageProcessor processor) {
        if (processor != null && !PROCESSORS.contains(processor)) {
            PROCESSORS.add(processor);
        }
    }

    public static void removeProcessor(AbstractMessageProcessor processor) {
        if (processor != null && PROCESSORS.contains(processor)) {
            PROCESSORS.remove(processor);
        }
    }

    public static void onConnectionStart() {
        for (AbstractMessageProcessor processor : PROCESSORS) {
            processor.onConnectionStart();
        }
    }
    public static void onConnectionStop() {
        for (AbstractMessageProcessor processor : PROCESSORS) {
            processor.onConnectionStop();
        }
    }

}
