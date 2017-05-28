package com.billy.controller.logcollector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.billy.controller.R;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.billy.controller.logcollector.Status.COLLECTING;
import static com.billy.controller.logcollector.Status.STARTING;
import static com.billy.controller.logcollector.Status.STOPPED;
import static com.billy.controller.logcollector.Status.STOPPING;
import static com.billy.controller.logcollector.Status.WAITING_CLIENT;

public class LogActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_START = "com.billy.log.collection.start";//开启日志收集
    ExecutorService mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
    public static final String IP = "127.0.0.1";
    public static final int PORT = 8090;
    public static final int CLIENT_COUNT = 1;//只接受指定数量的客户端
    public static final int WAIT_TIMEOUT = 3000;// 10s 等待时间
    private static final String TAG = "LogCollector";
    private Button mBtnOnOff;
    private Status status = STOPPED;
    long startTime;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);

    Handler handler;

    ServerSocket ss = null;
    Socket client = null;
    BufferedReader in = null;
    List<String> data = new ArrayList<>(100);
    private LogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init current status
        handler = new MyHandler(this);
        setContentView(R.layout.log_activity);
        mBtnOnOff = (Button)findViewById(R.id.log_on_off);
        mBtnOnOff.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.log_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new LogAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.log_on_off:
                if (status == STOPPED) {
                    start();
                } else if (status == COLLECTING) {
                    mExecutorService.execute(stopServerSocket);
                }
                break;
        }
    }

    private void setStatus(Status st) {
        Log.i(TAG, "status:" + st.toString());
        status = st;
        sendMessage(WHAT_REFRESH_BTN);
    }


    //开启
    Runnable startServerSocket = new Runnable() {
        @Override
        public void run() {
            try {
                setStatus(STARTING);
                startToReceiveLog();
            } catch(Exception ignored) {
            } finally {
                closeSocket();
                setStatus(STOPPED);
            }
        }
    };
    //超时停止
    TimeoutStopTask timeoutStopTask = new TimeoutStopTask();

    private class TimeoutStopTask implements Runnable {
        long time;
        @Override
        public void run() {
            try{
                Thread.sleep(WAIT_TIMEOUT);
                if (time == startTime) {
                    stop();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    Runnable stopServerSocket = new Runnable() {
        @Override
        public void run() {
            try{
                stop();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void startToReceiveLog() throws IOException {
        //创建一个ServerSocket ，监听客户端socket的连接请求
        ss = new ServerSocket(PORT, CLIENT_COUNT);
        setStatus(WAITING_CLIENT);
        client = ss.accept();
        in = null;
        startTime = 0;
        String msg;
        if (status == WAITING_CLIENT) {
            setStatus(COLLECTING);
            printMessage("开始日志收集...");
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(status == COLLECTING && (msg = in.readLine()) != null) {
                printMessage(msg);
            }
            printMessage("结束日志收集...");
        }
    }

    private void printMessage(String msg) {
        Logger.log(msg);
        sendMessage(WHAT_LOG_CONTENT, msg);
    }

    private void closeSocket() {
        if (client != null) {
            try{
                client.shutdownInput();//关闭输入流，让in.readLine()阻塞中止
                client.shutdownOutput();//关闭输出流，让客户端的in.readLine()阻塞中止
            } catch(Exception ignored) {
            }
        }
        close(in);
        close(client);
        close(ss);
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try{
                closeable.close();
            } catch(Exception ignored) {
            }
        }
    }

    private void start() {
        mExecutorService.execute(startServerSocket);
        Intent intent = new Intent(ACTION_START);
        intent.putExtra("ip", IP);
        intent.putExtra("port", PORT);
        sendBroadcast(intent);
        //超时停止
        timeoutStopTask.time = startTime = System.currentTimeMillis();
        mExecutorService.execute(timeoutStopTask);
    }

    private void stop() {
        setStatus(STOPPING);
        if (status == WAITING_CLIENT) {
            //通过创建一个无用的client来让ServerSocket跳过accept阻塞，从而中止
            Socket socket = null;
            try {
                socket = new Socket(IP, PORT);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(socket);
            }
        } else {
            closeSocket();
        }
    }

    @Override
    protected void onDestroy() {
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdownNow();
        }
        closeSocket();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void sendMessage(int what) {
        if (handler != null) {
            handler.obtainMessage(what).sendToTarget();
        }
    }
    private void sendMessage(int what, Object obj) {
        if (handler != null) {
            handler.obtainMessage(what, obj).sendToTarget();
        }
    }

    private static final int WHAT_REFRESH_BTN = 100;
    private static final int WHAT_LOG_CONTENT = 101;
    private static class MyHandler extends Handler {
        WeakReference<LogActivity> reference;

        MyHandler(LogActivity activity) {
            this.reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LogActivity activity = reference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_LOG_CONTENT:
                    activity.data.add((String) msg.obj);
                    activity.adapter.notifyItemInserted(activity.data.size() - 1);
                    break;
                case WHAT_REFRESH_BTN:
                    //刷新按钮显示文字
                    activity.mBtnOnOff.setText(activity.status.resId);
                    break;
            }
        }
    }

}
