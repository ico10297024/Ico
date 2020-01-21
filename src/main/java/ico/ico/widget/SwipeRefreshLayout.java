package ico.ico.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by ICO on 2016/8/11 0011.
 */
public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {

    private float startY;
    private float startX;
    // 记录viewPager是否拖拽的标记
    private boolean mIsVpDragger;
    private int mTouchSlop;

    public SwipeRefreshLayout(Context context) {
        super(context);
        //在as的预览中如果不加这句就会报错
        isInEditMode();
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //在as的预览中如果不加这句就会报错
        isInEditMode();
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        SwipeRefreshLayout.this.post(new Runnable() {
            @Override
            public void run() {
                SwipeRefreshLayout.super.setRefreshing(refreshing);
            }
        });
    }

    /**
     * 问题：
     * ViewPager和SwipeRefreshLayout滑动冲突，ViewPager切换不准，容易被SwipeRefreshLayout拦截
     * 思路：
     * 1. 因为下拉刷新，只有纵向滑动的时候才有效，那么我们就判断此时是纵向滑动还是横向滑动就可以了。
     * 2. 纵向滑动就拦截事件，横向滑动不拦截。
     * 3. 怎么判断是纵向滑动还是横向滑动，只要判断Y轴的移动距离大于X轴的移动距离那么就判定为纵向滑动就行了。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }
}
