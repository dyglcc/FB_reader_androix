package dyg.activity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BackendSupport extends HandlerThread {
    private static final String TAG = "TranslateAction";
    private List<Message> messages = new ArrayList<>();
    private List<DelayedRunnable> runnables = new ArrayList<>();


    private Handler handler = null;
    private static volatile BackendSupport instance = null;


    public static BackendSupport getInstance() {
        if (instance == null) {
            synchronized (BackendSupport.class) {
                instance = new BackendSupport("back_worker");
                instance.start();
            }
        }
        return instance;
    }

    public BackendSupport(String name) {
        super(name);
    }


    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        if (runnables.size() != 0) {
            for (int i = 0; i < runnables.size(); i++) {
                DelayedRunnable delayedRunnable = runnables.get(i);
                handler.postDelayed(delayedRunnable.runnable, delayedRunnable.delayed);
            }
        }
        if (messages.size() != 0) {
            for (int i = 0; i < messages.size(); i++) {
                handler.sendMessage(messages.get(i));
            }
        }
    }

    public void sendMessage(Message message) {
        synchronized (this) {
            if (handler == null) {
                messages.add(message);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    public void postDelayed(long delay, Runnable runnable) {
        synchronized (this) {
            if (handler == null) {
                DelayedRunnable delayedRunnable = new DelayedRunnable(runnable, delay);
                runnables.add(delayedRunnable);
            } else {
                handler.postDelayed(runnable, delay);
            }
        }

    }

    public void removeRunable(Runnable runnable) {
        synchronized (this) {
            if (handler == null) {
                Log.e(TAG, "removeRunable: remove before " + runnables.size());
                runnables.remove(runnable);
                Log.e(TAG, "removeRunable: removed after " + runnables.size());
            } else {
                handler.removeCallbacks(runnable);
                Log.e(TAG, "handler removeCallback");
            }
        }

    }

    private static class DelayedRunnable {
        private Runnable runnable;
        private long delayed;

        DelayedRunnable(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delayed = delay;
        }
    }
}
