package com.billy.controller.log.collector;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.billy.controller.R;
import com.billy.controller.core.ConnectionService;
import com.billy.controller.core.DebugListenerManager;
import com.billy.controller.core.IDebugListener;
import com.billy.controller.core.Status;
import com.billy.controller.log.collector.fiter.LevelFilter;
import com.billy.controller.log.collector.fiter.StringFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogActivity extends AppCompatActivity implements IDebugListener {
//    private final Pool<>

    private LogAdapter adapter;
    List<Integer> idList = Arrays.asList(
            R.id.log_level_v
            , R.id.log_level_d
            , R.id.log_level_i
            , R.id.log_level_w
            , R.id.log_level_e);
    private LevelFilter levelFilter;
    private StringFilter stringFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.log_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new LogAdapter(this);
        levelFilter = new LevelFilter();
        stringFilter = new StringFilter();
        adapter.addLogFilter(levelFilter);
        adapter.addLogFilter(stringFilter);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ConnectionService.addListener(this);
        RadioGroup group = (RadioGroup) findViewById(R.id.log_level_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int logLevel = idList.indexOf(checkedId);
                levelFilter.setCurLogLevel(logLevel);
                adapter.refreshFilter();
            }
        });
        EditText editText = (EditText) findViewById(R.id.et_filter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                String filter = s.toString().trim();
                stringFilter.setFilterStr(filter);
                adapter.refreshFilter();
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.shareSelectedItems();
            }
        });
        CheckBox cbSelectAll = (CheckBox) findViewById(R.id.log_select_all);
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.selectAll(isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ConnectionService.removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onStatus(Status status) {
    }

    @Override
    public void onMessage(String message) {
        if (message != null) {
            cache.offer(message);
            if (!readingCache.get()) {
                runOnUiThread(readFromCache);
            }
        }
    }

    @Override
    public String getDebugKey() {
        return DebugListenerManager.KEY_LOG;
    }

    LinkedBlockingQueue<String> cache = new LinkedBlockingQueue<>();

    AtomicBoolean readingCache = new AtomicBoolean(false);

    Runnable readFromCache = new Runnable() {
        @Override
        public void run() {
            if (readingCache.compareAndSet(false, true)) {
                List<LogItem> list = new LinkedList<>();
                String msg;
                while((msg = cache.poll()) != null) {
                    list.add(new LogItem(msg));
                }
                readingCache.set(false);
                adapter.addLogItems(list);
//                int oldSize = data.size();
//                data.addAll(list);
//                adapter.notifyItemRangeInserted(oldSize, list.size());
            }
        }
    };

    public class LogItem {
        static final int LOG_TYPE_INDEX = 32;
        static final String LEVEL = "DIWE";
        public String content;
        public int level;
        boolean selected;

        LogItem(String s) {
            if (s != null) {
                content = s;
                if (s.length() > LOG_TYPE_INDEX) {
                    char c = s.charAt(LOG_TYPE_INDEX - 1);
                    level = LEVEL.indexOf(c) + 1;// 0, 1, 2, 3, 4
                }
            }
        }
    }
}
