package ico.ico.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

import ico.ico.util.IcoThread;

/**
 * Created by ICO on 2015/12/28 0028.
 */
public class IcoGallery extends Gallery {

    /**
     * 标志当前是否为滚动模式
     */
    private boolean isCycle = false;
    /**
     * 自动滚动的时间间隔
     */
    private int speed = 1000;

    /**
     * 用于表示当前是否正在触摸中
     */
    private boolean isTouch = false;

    /**
     * 用于监听滚动事件，当前主要用于监听滚动停止
     * 滚动开始，手指放开后滚动 这两个状态暂时没有
     * 方法的参数值参考{@link android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)}
     */
    private OnScrollStateChangedListener onScrollStateChangedListener;
    private Runnable scrollStopTask = new Runnable() {
        @Override
        public void run() {
            if (onScrollStateChangedListener != null) {
                onScrollStateChangedListener.onItemScrollStateChanged(0);
            }
        }
    };
    private Handler handler = new Handler();
    private CycleThread cycleThread;

    public IcoGallery(Context context) {
        super(context);
    }

    public IcoGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IcoGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // e1是按下的事件，e2是抬起的事件
//        int keyCode;
//        if (isScrollingLeft(e1, e2)) {
//            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
//        } else {
//            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
//        }
//        onKeyDown(keyCode, null);
        return true;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!isTouch()) {
            handler.removeCallbacks(scrollStopTask);
            handler.postDelayed(scrollStopTask, 50);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setTouch(true);
                if (isCycle()) {
                    stopCycle();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setTouch(false);
                if (isCycle()) {
                    startCycle();
                }
                break;
        }


        return super.onTouchEvent(event);
    }

    public void startCycleScroll() {
        setCycle(true);
        if (cycleThread == null || cycleThread.isClosed()) {
            cycleThread = new CycleThread();
            cycleThread.start();
        }
    }

    public void stopCycleScroll() {
        setCycle(false);
        if (cycleThread != null) {
            cycleThread.close();
        }
    }

    private void startCycle() {
        if (cycleThread == null || cycleThread.isClosed()) {
            cycleThread = new CycleThread();
            cycleThread.start();
        }
    }

    private void stopCycle() {
        if (cycleThread != null) {
            cycleThread.close();
        }
    }

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    public boolean isCycle() {
        return isCycle;
    }

    protected IcoGallery setCycle(boolean cycle) {
        isCycle = cycle;
        return this;
    }

    public OnScrollStateChangedListener getOnScrollStateChangedListener() {
        return onScrollStateChangedListener;
    }

    public void setOnScrollStateChangedListener(OnScrollStateChangedListener onScrollStateChangedListener) {
        this.onScrollStateChangedListener = onScrollStateChangedListener;
    }

    public int getSpeed() {
        return speed;
    }

    public IcoGallery setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    /**
     * item切换监听器，同时监听滚动周期
     */
    public interface OnScrollStateChangedListener {
        /**
         * 仿照ViewPager的OnPageChangeListener
         *
         * @param state
         */
        public void onItemScrollStateChanged(int state);
    }

    /** 执行自动滚动使用的线程 */
    private class CycleThread extends IcoThread {
        @Override
        public void run() {
            while (!isClosed()) {
                try {
                    sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isClosed()) {
                    break;
                }
                if (IcoGallery.this.getAdapter() != null && IcoGallery.this.getAdapter().getCount() > 1) {
                    int index = IcoGallery.this.getSelectedItemPosition() + 1;
                    if (index >= IcoGallery.this.getChildCount()) {
                        index = 0;
                    }
                    final int finalIndex = index;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            IcoGallery.this.setSelection(finalIndex, false);
                        }
                    });
                }
            }
        }
    }
}
