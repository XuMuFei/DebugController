package com.billy.controller.core;

import com.billy.controller.R;

/**
 * @author billy.qi
 * @since 17/5/25 11:47
 */
public enum Status {
    STARTING(R.string.log_starting)
    , WAITING_CLIENT(R.string.log_status_waiting)
    , RUNNING(R.string.log_status_on)
    , STOPPING(R.string.log_status_stopping)
    , STOPPED(R.string.log_status_off);


    public int resId;
    Status(int resId) {
        this.resId = resId;
    }

}
