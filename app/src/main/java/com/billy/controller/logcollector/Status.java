package com.billy.controller.logcollector;

/**
 * @author billy.qi
 * @since 17/5/25 11:47
 */
public enum Status {
    STARTING(R.string.starting)
    , WAITING_CLIENT(R.string.status_waiting)
    , COLLECTING(R.string.status_on)
    , STOPPING(R.string.status_stopping)
    , STOPPED(R.string.status_off);


    int resId;
    Status(int resId) {
        this.resId = resId;
    }

}
