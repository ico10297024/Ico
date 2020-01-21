package ico.ico.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import ico.ico.util.IcoAsyncTask;

import static java.lang.Thread.sleep;

/**
 * Created by root on 18-2-3.
 */

public class LoopViewPager extends ViewPager {
    public final static int LS_STOP = 0;
    public final static int LS_PLAY = 1;
    public final static int LS_PAUSE = 2;
    int loopStatus = LS_STOP;
    long mInterval;
    LoopAsyncTask loopAsyncTask;

    public LoopViewPager(Context context) {
        super(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void startLoop(long interval) {
        this.mInterval = interval;
        loopStatus = LS_PLAY;
        if (loopAsyncTask == null) {
            loopAsyncTask = new LoopAsyncTask();
            loopAsyncTask.execute();
        } else {
            loopAsyncTask.notifynotify();
        }
    }

    public void stopLoop() {
        loopStatus = LS_STOP;
        loopAsyncTask.waitwait();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean flag = super.dispatchTouchEvent(ev);

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (loopStatus == LS_PLAY) {
                    stopLoop();
                    loopStatus = LS_PAUSE;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                if (loopStatus == LS_PAUSE) {
                    startLoop(mInterval);
                    loopStatus = LS_PLAY;
                }
                break;
        }
        return flag;
    }

    class LoopAsyncTask extends IcoAsyncTask<Integer, Integer, Integer> {

        boolean isWait;

        @Override
        protected Integer doInBackground(Integer... params) {
            while (true) {
                if (isWait) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    continue;
                } else {
                    try {
                        sleep(mInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isWait) {
                    continue;
                }
                if (isClosed()) {
                    break;
                }
                publishProgress(0);
            }
            return 0;
        }

        public void waitwait() {
            isWait = true;
        }

        public void notifynotify() {
            isWait = false;
            synchronized (this) {
                notify();
            }
        }

        @Override

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int i = getCurrentItem();
            i++;
            if (i >= getChildCount()) {
                i = 0;
            }
            setCurrentItem(i);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            loopAsyncTask = null;
        }

        @Override
        public void close(boolean cancel) {
            super.close(cancel);
            loopAsyncTask = null;
        }
    }
}
