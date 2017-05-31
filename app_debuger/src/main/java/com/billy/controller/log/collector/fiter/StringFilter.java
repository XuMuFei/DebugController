package com.billy.controller.log.collector.fiter;

import android.text.TextUtils;

import com.billy.controller.log.collector.LogActivity;

/**
 * @author billy.qi
 * @since 17/5/30 10:47
 */
public class StringFilter implements ILogFilter {
    private String filterStr;

    @Override
    public boolean filter(LogActivity.LogItem item) {
        return item != null &&
                (TextUtils.isEmpty(filterStr) || item.content.contains(filterStr));
    }

    public String getFilterStr() {
        return filterStr;
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }
}
