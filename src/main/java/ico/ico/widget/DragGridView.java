package ico.ico.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.GridView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.animation.AnimatorProxy;

import ico.ico.ico.R;
import ico.ico.util.IcoAsyncTask;

/**
 * @author xiaanming
 * @blog http://blog.csdn.net/xiaanming
 */
public class DragGridView extends GridView implements AbsListView.OnScrollListener {


    /**
     * 拖拽模式
     *
     * @params DRAGMODE_NORMAL   普通的拖拽模式，item长按一定响应时间后可以拖拽item，为默认拖拽模式
     * @params DRAGMODE_RESIDENT 常驻的拖拽模式，通过方法开启和关闭拖拽模式，在该模式下，响应时间为0秒，点击立即可以进行拖拽
     */
    public final static int DRAGMODE_NORMAL = 0, DRAGMODE_RESIDENT = 1;
    /**
     * item发生变化回调的接口
     */
    protected OnItemChangeListener onItemChangeListener;
    protected OnDragingChangedListener onDragingChangedListener;
    private DragGridView dragGridView;
    private WindowManager mWindowManager;
    /**
     * 界面 滚动监听器
     */
    private AbsListView.OnScrollListener onScrollListener;
    /**
     * @params isDrag           是否可以拖拽
     * @params isDragModeing   是否已进入拖拽模式中，用于常驻的模式
     * @params isDraging        是否正在拖拽中
     */
    private boolean isDrag = false, isDragModeing = false, isDraging = false;
    /**
     * @params mDragPosition    正在拖拽item的position,
     * @params mDownX            触摸点第一次按下时的X坐标
     * @params mDownY            触摸点第一次按下时的Y坐标
     * @params moveX             触摸点移动时的X坐标
     * @params moveY             触摸点移动时的Y坐标
     * @params mStatusHeight     状态栏的高度
     * @params mDownScrollBorder, mUpScrollBorder   DragGridView自动向下滚动和自动向上滚动的边界值
     * @params dragMode          拖拽的模式，默认为normal模式
     * @params scrollInterval    每次滚动的间隔时间，默认为500MS
     * @params scrollOffset      每次滚动的偏移量，若该值为空则以每次滚动一行
     */
    private int mDragPosition = -1, mDownX, mDownY, moveX, moveY, mStatusHeight, mDownScrollBorder, mUpScrollBorder, dragMode = DRAGMODE_NORMAL, scrollInterval = 500, scrollOffset = -1;

    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private View mDragView;
    /**
     * 震动器
     */
    private Vibrator mVibrator;
    /**
     * 开始拖拽时，震动提醒的时间
     */
    private int vibrateTime = 50;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    /**
     * @params viewLocationOnWindow     DragGridView相对屏幕的坐标
     * @params itemViewLocationOnWindow 选中的item相对屏幕的坐标
     */
    private int[] viewLocationOnWindow = new int[2], itemViewLocationOnWindow = new int[2];


    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    private int dragResponseTime = 700;

    /**
     * @params itemAnimation        开始拖拽后所有item的动画资源ID
     * @params dragViewAnimation    拖拽镜像视图的动画资源ID
     * @params dragItemAnimation    拖拽镜像视图所表示的item动画资源，默认使用itemAnimation，若为-1则不使用itemAnimation
     */
    private int itemAnimation = -2, dragViewAnimation = -2, dragItemAnimation = -2;


    /**
     * @params dragViewAlpha    拖拽镜像的透明度，默认1（不透明）
     * @params dragItemAlpha    拖拽镜像所表示的item的透明度，默认0.3
     */
    private float dragViewAlpha = 1f, dragItemAlpha = 0.3f;
    /**
     * 自动滚动的异步任务
     */
    private ScrollAsyncTask mScrollAsyncTask;
    /**
     * 拖拽的长按控制
     */
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (mDragPosition == -1) {
                return;
            }
            mVibrator.vibrate(vibrateTime); //震动一下
            //根据我们按下的点显示item镜像
            setDraging(true);
            View itemView = getChildAt(mDragPosition - getFirstVisiblePosition());
            if (itemView != null) {
                ViewHelper.setAlpha(itemView, dragItemAlpha);
            }

        }
    };


    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragGridView = this;
        super.setOnScrollListener(this);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); //获取状态栏的高度


        /*公共的*/
        TypedArray typedArray1 = getContext().obtainStyledAttributes(attrs, R.styleable.DragGridView);
        //是否启用拖拽功能
        isDrag = typedArray1.getBoolean(R.styleable.DragGridView_drag, isDrag);
        //拖拽模式
        dragMode = typedArray1.getInt(R.styleable.DragGridView_dragMode, dragMode);
        //拖拽响应时间vibratorTime
        dragResponseTime = typedArray1.getInt(R.styleable.DragGridView_dragResponseTime, dragResponseTime);
        //开始拖拽时的震动时间
        vibrateTime = typedArray1.getInt(R.styleable.DragGridView_vibrateTime, vibrateTime);
        //开始拖拽后所有item的动画资源ID
        itemAnimation = typedArray1.getResourceId(R.styleable.DragGridView_itemAnimation, itemAnimation);


        //拖拽镜像视图的动画资源ID
        dragViewAnimation = typedArray1.getResourceId(R.styleable.DragGridView_dragViewAnimation, dragViewAnimation);
        //拖拽镜像视图所表示的item动画资源
        dragItemAnimation = typedArray1.getResourceId(R.styleable.DragGridView_dragItemAnimation, dragItemAnimation);
        //拖拽镜像的透明度
        dragViewAlpha = typedArray1.getFloat(R.styleable.DragGridView_dragViewAlpha, dragViewAlpha);
        //拖拽镜像所表示的item的透明度
        dragItemAlpha = typedArray1.getFloat(R.styleable.DragGridView_dragItemAlpha, dragItemAlpha);

        //两种滚动方式
        //1、每隔一定时间滚动一定长度（scrollOffset，scrollInterval都有值）
        //2、每隔一定时间，滚动一行（除第一种外都用第二种）
        scrollInterval = typedArray1.getInt(R.styleable.DragGridView_scrollInterval, scrollInterval);//每次滚动的间隔时间,MS
        scrollOffset = typedArray1.getInteger(R.styleable.DragGridView_scrollOffset, scrollOffset);//每次滚动的偏移量
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        refreshItem();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //获取gridview距离
        this.getLocationOnScreen(viewLocationOnWindow);
        //获取DragGridView自动向滚动的偏移量，小于这个值，DragGridView向下滚动
        mDownScrollBorder = getHeight() / 4 * 3;
        //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
        mUpScrollBorder = getHeight() / 4;

        refreshItem();
    }

    /**
     * 用于转发触摸事件，根据触点来判断该交由谁来执行
     * 如果一个点按着不动则会持续调用该方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isDrag()) {
            return super.dispatchTouchEvent(ev);
        }
        //如果已经在拖拽了，则直接调用onTouch返回
        if (isDraging()) {
            return onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //保存第一次按下时的值
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                //根据按下的X,Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX, mDownY);

                //若触点无效则直接返回
                if (mDragPosition == -1) {
                    return super.dispatchTouchEvent(ev);
                }
                //若已经在拖拽模式中，则直接post
                if (isDragModeing()) {
                    dragGridView.postDelayed(mLongClickRunnable, 100);
                } else {
                    dragGridView.postDelayed(mLongClickRunnable, dragResponseTime);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //保存移动时的触点
                moveX = (int) ev.getX();
                moveY = (int) ev.getY();
                //根据按下的X,Y坐标获取所点击item的position
                if ((!isDraging()) && (mDragPosition == -1 || Math.abs(moveX - mDownX) >= 20 || Math.abs(moveY - mDownY) >= 20)) {
                    dragGridView.removeCallbacks(mLongClickRunnable);
                    mDragPosition = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragPosition = -1;
                dragGridView.removeCallbacks(mLongClickRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isDrag()) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //保存移动时的触点
                moveX = (int) ev.getX();
                moveY = (int) ev.getY();

                if (mDragView != null) {
                    //拖动item
                    onDragItem(moveX, moveY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (this.isDraging()) {
                    setDraging(false);
                    return true;
                }
                break;
        }
        if (isDraging()) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 获取当前是否已到底部
     *
     * @return
     */
    public boolean isBottom() {
        if (getLastVisiblePosition() != getAdapter().getCount() - 1) {
            return false;
        }
        View lastVisibleView = dragGridView.getChildAt(getLastVisiblePosition() - getFirstVisiblePosition());
        int[] _viewLocation = new int[2];
        lastVisibleView.getLocationInWindow(_viewLocation);
        if (_viewLocation[1] + lastVisibleView.getHeight() >= viewLocationOnWindow[1] + dragGridView.getHeight()) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否已到顶部
     *
     * @return
     */
    public boolean isTop() {
        if (getFirstVisiblePosition() != 0) {
            return false;
        }
        View lastVisibleView = dragGridView.getChildAt(0);
        int[] _viewLocation = new int[2];
        lastVisibleView.getLocationInWindow(_viewLocation);
        if (_viewLocation[1] >= viewLocationOnWindow[1]) {
            return true;
        }
        return false;
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeDragImage() {
        mDragPosition = -1;
        if (mDragView != null) {
            mWindowManager.removeView(mDragView);
            mDragView = null;
        }
    }


    /**
     * 初始化拖拽的镜像，并将该镜像放入window中
     * 原本使用itemView绘图缓存作为镜像，但是由于destroy后还是有可能出现重复镜像
     * //开启mDragItemView绘图缓存
     * itemView.setDrawingCacheEnabled(true);
     * //获取mDragItemView在缓存中的Bitmap对象
     * mDragBitmap = Bitmap.createBitmap(itemView.getDrawingCache());
     * //这一步很关键，释放绘图缓存，避免出现重复的镜像
     * itemView.destroyDrawingCache();
     * itemView.setDrawingCacheEnabled(false);
     */
    private void initDragView() {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        //根据position获取该item所对应的View
        View itemView = getChildAt(mDragPosition - getFirstVisiblePosition());
        //获取item距离window的坐标值，设置为镜像的坐标值
        itemView.getLocationInWindow(itemViewLocationOnWindow);
        mWindowLayoutParams.x = itemViewLocationOnWindow[0];
        mWindowLayoutParams.y = itemViewLocationOnWindow[1] - mStatusHeight;
        //透明度
        mWindowLayoutParams.alpha = dragViewAlpha;
        //镜像宽高
        mWindowLayoutParams.width = itemView.getWidth();
        mWindowLayoutParams.height = itemView.getHeight();
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mDragView = getAdapter().getView(mDragPosition, null, this);
        mWindowManager.addView(mDragView, mWindowLayoutParams);
    }


    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = itemViewLocationOnWindow[0] + moveX - mDownX;
        mWindowLayoutParams.y = itemViewLocationOnWindow[1] - mStatusHeight + moveY - mDownY;
        mWindowManager.updateViewLayout(mDragView, mWindowLayoutParams); //更新镜像的位置
        onSwapItem(moveX, moveY);
        refreshItem();
        //判断是否自动滚动
        if (moveY < mUpScrollBorder || moveY > mDownScrollBorder) {
            if (mScrollAsyncTask == null) {
                mScrollAsyncTask = new ScrollAsyncTask();
                mScrollAsyncTask.execute();
            }
        } else {
            if (mScrollAsyncTask != null) {
                mScrollAsyncTask.close(true);
                mScrollAsyncTask = null;
            }
        }
    }

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        int tempPosition = pointToPosition(moveX, moveY);
        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (mDragPosition != -1 && tempPosition != -1 && tempPosition != mDragPosition) {
            if (onItemChangeListener != null) {
                onItemChangeListener.onItemChange(mDragPosition, tempPosition);
            }
            mDragPosition = tempPosition;
        }
    }

    /**
     * 刷新所有item，根据当前状态设置item的动画和透明度
     */
    public void refreshItem() {
        this.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < dragGridView.getChildCount(); i++) {
                    View itemView = dragGridView.getChildAt(i);
                    if (mDragPosition - getFirstVisiblePosition() >= 0 && mDragPosition - getFirstVisiblePosition() == i) {
                        ViewHelper.setAlpha(itemView, dragItemAlpha);
                    } else {
                        ViewHelper.setAlpha(itemView, 1f);
                    }
                    //正在拖拽中||已进入常驻拖拽模式中
                    if (isDraging() || isDragModeing()) {
                        //item的动画已存在
                        if (itemAnimation > 0) {
                            //动画并没有开始(3.0以下getAnimation获取到的不为null，为一个动画器代理)
                            if ((itemView.getAnimation() == null) || (itemView.getAnimation() instanceof AnimatorProxy)) {
                                Animation animation = AnimationUtils.loadAnimation(dragGridView.getContext(), itemAnimation);
                                itemView.startAnimation(animation);
                            }
                        }
                    } else {
                        itemView.clearAnimation();
                    }
                }
            }
        });
    }


    //region GETSETADD


    public OnItemChangeListener getOnItemChangeListener() {
        return onItemChangeListener;
    }

    public DragGridView setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
        return this;
    }

    public OnDragingChangedListener getOnDragingChangedListener() {
        return onDragingChangedListener;
    }

    public DragGridView setOnDragingChangedListener(OnDragingChangedListener onDragingChangedListener) {
        this.onDragingChangedListener = onDragingChangedListener;
        return this;
    }

    /**
     * 获取拖拽长按的响应时间
     *
     * @return
     */
    public long getDragResponseTime() {
        return dragResponseTime;
    }

    /**
     * 设置拖拽长按的响应时间，默认为700MS
     *
     * @param dragResponseTime
     */
    public void setDragResponseTime(int dragResponseTime) {
        this.dragResponseTime = dragResponseTime;
    }

    public int getVibrateTime() {
        return vibrateTime;
    }

    public DragGridView setVibrateTime(int vibrateTime) {
        this.vibrateTime = vibrateTime;
        return this;
    }


    public boolean isDrag() {
        return isDrag;
    }

    public DragGridView setDrag(boolean isDrag) {
        this.isDrag = isDrag;
        return this;
    }

    /**
     * 当前是否已进入拖拽模式中
     *
     * @return
     */
    public boolean isDragModeing() {
        return isDragModeing;
    }

    /**
     * 开启或关闭拖拽模式
     *
     * @param isDragModeing
     * @return IcoGridView
     */
    public DragGridView setDragModeing(boolean isDragModeing) {
        //未开启拖拽功能||拖拽为普通模式
        if ((!isDrag()) || dragMode == DRAGMODE_NORMAL) {
            this.isDragModeing = false;
            return this;
        }
        //若未修改则直接返回
        if (this.isDragModeing == isDragModeing) {
            return this;
        }
        this.isDragModeing = isDragModeing;
        //根据当前拖拽状态进行不同的UI处理
        if (isDraging) {//初始化拖拽镜像视图，添加进窗口
            initDragView();
        } else {//删除拖拽镜像视图，还原所有item，停止自动滚动
            removeDragImage();
            if (mScrollAsyncTask != null) {
                mScrollAsyncTask.close(true);
                mScrollAsyncTask = null;
            }
        }
        refreshItem();
        return this;
    }

    /**
     * 当前是否正在拖拽中
     *
     * @return
     */
    public boolean isDraging() {
        return isDraging;
    }

    /**
     * 开始或者停止拖拽
     *
     * @param isDraging
     * @return IcoGridView
     */
    public DragGridView setDraging(boolean isDraging) {
        //未开启拖拽功能
        if (!isDrag()) {
            return this;
        }
        //若状态未改变则直接返回
        if (this.isDraging == isDraging) {
            return this;
        }
        this.isDraging = isDraging;
        //在状态改变后调用监听器,若监听器返回true则直接返回
        if ((onDragingChangedListener != null) && (onDragingChangedListener.onDragingChanged(dragGridView, isDraging))) {
            refreshItem();
            return this;
        }
        //根据当前拖拽状态进行不同的UI处理
        if (isDraging) {//初始化拖拽镜像视图，添加进窗口
            initDragView();
        } else {//删除拖拽镜像视图，还原所有item，停止自动滚动
            removeDragImage();
            refreshItem();
            if (mScrollAsyncTask != null) {
                mScrollAsyncTask.close(true);
                mScrollAsyncTask = null;
            }
        }
        return this;
    }

    public int getDragMode() {
        return dragMode;
    }

    public DragGridView setDragMode(int dragMode) {
        this.dragMode = dragMode;
        return this;
    }

    public int getScrollInterval() {
        return scrollInterval;
    }

    public DragGridView setScrollInterval(int scrollInterval) {
        this.scrollInterval = scrollInterval;
        return this;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public DragGridView setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        return this;
    }

    public int getDragViewAnimation() {
        return dragViewAnimation;
    }

    public DragGridView setDragViewAnimation(int dragViewAnimation) {
        this.dragViewAnimation = dragViewAnimation;
        return this;
    }

    public int getDragItemAnimation() {
        return dragItemAnimation;
    }

    public DragGridView setDragItemAnimation(int dragItemAnimation) {
        this.dragItemAnimation = dragItemAnimation;
        return this;
    }

    public float getDragViewAlpha() {
        return dragViewAlpha;
    }

    public DragGridView setDragViewAlpha(float dragViewAlpha) {
        this.dragViewAlpha = dragViewAlpha;
        return this;
    }

    public float getDragItemAlpha() {
        return dragItemAlpha;
    }

    public DragGridView setDragItemAlpha(float dragItemAlpha) {
        this.dragItemAlpha = dragItemAlpha;
        return this;
    }

    public int getItemAnimation() {
        return itemAnimation;
    }

    public DragGridView setItemAnimation(int itemAnimation) {
        this.itemAnimation = itemAnimation;
        return this;
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    //endregion GETSETADD


    /**
     * item位置变更监听
     */
    public interface OnItemChangeListener {

        /**
         * 当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
         *
         * @param from 开始的position
         * @param to   拖拽到的position
         */
        void onItemChange(int from, int to);
    }

    /**
     * 拖拽状态更改监听
     */
    public interface OnDragingChangedListener {

        /**
         * 拖拽状态更改时触发
         *
         * @param dragGridView
         * @param isDraging    拖拽状态
         * @return boolean 若返回true则代表已处理完毕，直接返回
         */
        boolean onDragingChanged(DragGridView dragGridView, boolean isDraging);
    }


    /**
     * 用于执行自动滚动的异步任务
     */
    private class ScrollAsyncTask extends IcoAsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            while (!isClosed()) {
                int scrollY;
                if (moveY > mUpScrollBorder) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        scrollY = -scrollOffset;
                    } else {
                        scrollY = scrollOffset;
                    }
                } else if (moveY < mDownScrollBorder) {
                    if (Build.VERSION.SDK_INT >= 11) {
                        scrollY = scrollOffset;
                    } else {
                        scrollY = -scrollOffset;
                    }
                } else {
                    scrollY = 0;
                }
                publishProgress(scrollY);
                try {
                    this.wait(scrollInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int scrollY = values[0];
            //当我们的手指到达GridView向上或者向下滚动的偏移量的时候，可能我们手指没有移动，但是DragGridView在自动的滚动
            //所以我们在这里调用下onSwapItem()方法来交换item
            onSwapItem(moveX, moveY);
            //实现GridView的自动滚动
            if (scrollOffset < 0) {//每次滚动一行
                if (Build.VERSION.SDK_INT >= 11) {
                    if (moveY > mDownScrollBorder) {
                        dragGridView.smoothScrollToPosition(getLastVisiblePosition() + 1);
                    } else if (moveY < mUpScrollBorder) {
                        dragGridView.smoothScrollToPosition(getFirstVisiblePosition() - 1);
                    }
                } else {
                    if (moveY > mDownScrollBorder) {
                        dragGridView.smoothScrollToPosition(getLastVisiblePosition());
                    } else if (moveY < mUpScrollBorder) {
                        dragGridView.smoothScrollToPosition(getFirstVisiblePosition());
                    }
                }

            } else {//每次滚动一定的偏移量
                if (Build.VERSION.SDK_INT >= 11) {
                    View view = getChildAt(mDragPosition - getFirstVisiblePosition());
                    dragGridView.smoothScrollToPositionFromTop(mDragPosition, view.getTop() + scrollY, 50);
                } else {
                    //若所有item有动画时，使用该方法无法滚动
                    //但若使用scrollBy或者scrollTo，将滚动整个GridView，而不是滚动内容
                    dragGridView.smoothScrollBy(scrollY, 50);
                }
            }
            refreshItem();
        }
    }
}
