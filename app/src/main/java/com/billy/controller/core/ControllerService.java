package com.billy.controller.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.billy.controller.R;
import com.billy.controller.logcollector.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.billy.controller.logcollector.Status.COLLECTING;
import static com.billy.controller.logcollector.Status.STARTING;
import static com.billy.controller.logcollector.Status.STOPPED;
import static com.billy.controller.logcollector.Status.STOPPING;
import static com.billy.controller.logcollector.Status.WAITING_CLIENT;

/**
 * @author billy.qi
 * @since 17/5/26 13:02
 */
public class ControllerService extends Service {

    private static AtomicBoolean running = new AtomicBoolean();
    EventBus bus = EventBus.getDefault();
    ExecutorService mExecutorService = Executors.newCachedThreadPool();  //create a thread pool
    public static final String IP = "127.0.0.1";
    public static final int PORT = 8090;
    public static final int CLIENT_COUNT = 1;//只接受指定数量的客户端
    public static final int WAIT_TIMEOUT = 3000;// 等待时间(ms)
    private static final String TAG = "ControllerService";
    long startTime;
    ServerSocket ss = null;
    Socket client = null;
    BufferedReader in = null;
    List<String> data = new ArrayList<>(100);
    private Status status;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus.register(this);
        setRunning(false);
    }

    private void setRunning(boolean value) {
        running.set(value);
        logcat("set running = " + value);
    }

    private void logcat(String message) {
//        Log.i("Controller", message);
    }

    private void setStatus(Status st) {
        Log.i(TAG, "status:" + st.toString());
        bus.post(st);
    }

    //开启
    Runnable sendConnectionBroadcast = new Runnable() {
        @Override
        public void run() {
            if (status == STOPPED) {
                try {
                    Intent intent = new Intent(getString(R.string.log_action));
                    intent.putExtra("ip", IP);
                    intent.putExtra("port", PORT);
                    sendBroadcast(intent);
                    setStatus(WAITING_CLIENT);
                } catch(Exception ignored) {
                }
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

    private void start() throws IOException {
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

//        sendMessage(WHAT_LOG_CONTENT, msg);
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
    public void onDestroy() {
        bus.unregister(this);
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdownNow();
        }
        closeSocket();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStopEvent(StopEvent event) {
        try{
            if (event.delayTime > 0) {
                Thread.sleep(event.delayTime);

            } else {
                stop();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartEvent(StartEvent event) {
        if (status == STOPPED) {
            try {
                setStatus(STARTING);
                start();
            } catch(Exception ignored) {
            } finally {
                closeSocket();
                setStatus(STOPPED);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastSendEvent(SendBroadcastEvent event) {
        try {
            Intent intent = new Intent(getString(R.string.log_action));
            intent.putExtra("ip", IP);
            intent.putExtra("port", PORT);
            sendBroadcast(intent);
            setStatus(WAITING_CLIENT);
            StopEvent delayStopEvent = new StopEvent();
            delayStopEvent.delayTime = WAIT_TIMEOUT;
            bus.post(delayStopEvent);
        } catch(Exception ignored) {
        }
    }

}
