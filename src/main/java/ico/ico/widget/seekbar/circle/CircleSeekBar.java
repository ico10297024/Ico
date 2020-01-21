package ico.ico.widget.seekbar.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import ico.ico.ico.R;

/**
 * 圆形的SeekBar
 *
 * @author lee
 */

public class CircleSeekBar extends View {
    //滑块的图片
    protected Drawable mThumbDrawable = null;
    //滑块的宽高
    protected int mThumbHeight = 0;
    protected int mThumbWidth = 0;
    //滑块的坐标
    protected float mThumbLeft = 0;
    protected float mThumbTop = 0;
    //滑块正常状态下和点击状态下
    protected int[] mThumbNormal = null;
    protected int[] mThumbPressed = null;
    //总进度
    protected int mMax = 100;
    //当前进度
    protected int mProgress = 0;
    //进度条的宽度
    protected float mProgressStrokeWidth;
    //当前进度所代表弧的角度
    protected float mSeekBarDegree = 0;
    //轨道的画笔
    protected Paint mTrackPaint = null;
    protected int mTrackColor = Color.GRAY;
    //进度条的画笔
    protected Paint mProgressPaint = null;
    protected int mProgressColor = Color.BLUE;
    protected Paint.Cap mPaintCap = Paint.Cap.BUTT;
    //是否显示文本
    protected boolean mIsShowProgressText = false;
    protected Paint mProgressTextPaint = null;
    protected int mProgressTextSize = 50;
    protected int mProgressTextStrokeWidth = 5;
    protected int mProgressTextColor = Color.GREEN;
    /**
     * 起始的角度
     * 弧经过的角度
     */
    protected float startAngle = 0f;
    protected float sweepAngle = 360f;
    //控件绘图的范围
    protected RectF mArcRectF = null;
    //控件的宽高
    protected int mViewHeight = 0;
    protected int mViewWidth = 0;
    //直径
    protected int mSeekBarSize = 0;
    //半径
    protected int mSeekBarRadius = 0;
    //中心点
    protected int mSeekBarCenterX = 0;
    protected int mSeekBarCenterY = 0;

    protected OnCircleSeekBarChangeListener onCircleSeekBarChangeListener;

    public CircleSeekBar(Context context) {
        this(context, null);
    }

    public CircleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray localTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleSeekBar);

        //thumb的属性是使用android:thumb属性进行设置的
        //返回的Drawable为一个StateListDrawable类型，即可以实现选中效果的drawable list
        //mThumbNormal和mThumbPressed则是用于设置不同状态的效果，当点击thumb时设置mThumbPressed，否则设置mThumbNormal
        Drawable thumbDrawable = localTypedArray.getDrawable(R.styleable.ArcSeekBar_android_thumb);
        if (thumbDrawable != null) {
            setProgressThumb(thumbDrawable);
        }
        mThumbNormal = new int[]{-android.R.attr.state_focused, -android.R.attr.state_pressed, -android.R.attr.state_selected, -android.R.attr.state_checked};
        mThumbPressed = new int[]{android.R.attr.state_focused, android.R.attr.state_pressed, android.R.attr.state_selected, android.R.attr.state_checked};

        //总进度
        mMax = localTypedArray.getInteger(R.styleable.CircleSeekBar_android_max, mMax);
        //当前进度
        mProgress = localTypedArray.getInteger(R.styleable.ArcSeekBar_android_progress, 0);
        /*画笔*/
        mProgressStrokeWidth = localTypedArray.getDimension(R.styleable.ArcSeekBar_strokeWidth, 5);//进度条的宽度
        mTrackColor = localTypedArray.getColor(R.styleable.ArcSeekBar_trackColor, Color.GRAY);//轨道的颜色
        mProgressColor = localTypedArray.getColor(R.styleable.ArcSeekBar_progressColor, Color.BLUE);//进度条的颜色
        int paintCap = localTypedArray.getInt(R.styleable.ArcSeekBar_paintCap, 0);//画笔笔头的样式
        //初始化轨道和进度条的画笔
        mProgressPaint = new Paint();
        mTrackPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mTrackPaint.setColor(mTrackColor);
        mProgressPaint.setAntiAlias(true);
        mTrackPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mTrackPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressStrokeWidth);
        mTrackPaint.setStrokeWidth(mProgressStrokeWidth);
        switch (paintCap) {
            case 1:
                mPaintCap = Paint.Cap.ROUND;
                break;
            case 2:
                mPaintCap = Paint.Cap.SQUARE;
                break;
        }
        mProgressPaint.setStrokeCap(mPaintCap);
        mTrackPaint.setStrokeCap(mPaintCap);
        /*起始和终止角度*/
        startAngle = localTypedArray.getFloat(R.styleable.ArcSeekBar_startAngle, startAngle);
        sweepAngle = localTypedArray.getFloat(R.styleable.ArcSeekBar_sweepAngle, sweepAngle);

        /*文字相关*/
        mIsShowProgressText = localTypedArray.getBoolean(R.styleable.ArcSeekBar_showProgressText, mIsShowProgressText);//是否显示文字
        mProgressTextSize = (int) localTypedArray.getDimension(R.styleable.ArcSeekBar_progressTextSize, mProgressTextSize);//文字大小
        mProgressTextStrokeWidth = (int) localTypedArray.getDimension(R.styleable.ArcSeekBar_progressTextStrokeWidth, mProgressTextStrokeWidth);//文字画笔的粗细
        mProgressTextColor = localTypedArray.getColor(R.styleable.ArcSeekBar_progressTextColor, mProgressTextColor);//文字的颜色
        mProgressTextPaint = new Paint();
        mProgressTextPaint.setColor(mProgressTextColor);
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setStrokeWidth(mProgressTextStrokeWidth);
        mProgressTextPaint.setTextSize(mProgressTextSize);

        localTypedArray.recycle();
        setProgress(mProgress, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        //直径
        mSeekBarSize = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;
        //半径
        if (mThumbWidth > mProgressStrokeWidth) {
            mSeekBarRadius = mSeekBarSize / 2 - mThumbWidth / 2;
        } else {
            mSeekBarRadius = (int) (mSeekBarSize / 2 - mProgressStrokeWidth / 2);
        }
        //中心点坐标
        mSeekBarCenterX = mViewWidth / 2;
        mSeekBarCenterY = mViewHeight / 2;

        //绘制的范围
        int left = mSeekBarCenterX - mSeekBarRadius;
        int right = mSeekBarCenterX + mSeekBarRadius;
        int top = mSeekBarCenterY - mSeekBarRadius;
        int bottom = mSeekBarCenterY + mSeekBarRadius;

        mArcRectF = new RectF();
        mArcRectF.set(left, top, right, bottom);

        // 起始位置，三点钟方向
        setThumbPosition(Math.toRadians(mSeekBarDegree + startAngle));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //轨道
        canvas.drawCircle(mSeekBarCenterX, mSeekBarCenterY, mSeekBarRadius, mTrackPaint);
        //进度
        canvas.drawArc(this.mArcRectF, startAngle, mSeekBarDegree, false, mProgressPaint);
        //滑块
        drawThumbBitmap(canvas);
        drawProgressText(canvas);

        super.onDraw(canvas);
    }

    protected void drawThumbBitmap(Canvas canvas) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setBounds((int) mThumbLeft, (int) mThumbTop, (int) (mThumbLeft + mThumbWidth), (int) (mThumbTop + mThumbHeight));
            this.mThumbDrawable.draw(canvas);
        }
    }

    protected void drawProgressText(Canvas canvas) {
        if (true == mIsShowProgressText) {
            float textWidth = mProgressTextPaint.measureText("" + mProgress);
            canvas.drawText("" + mProgress, mSeekBarCenterX - textWidth / 2, mSeekBarCenterY + mProgressTextSize / 2, mProgressTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onCircleSeekBarChangeListener != null) {
                    onCircleSeekBarChangeListener.onStartTrackingTouch(this);
                }
                seekTo(eventX, eventY, true);
                setThumbState(mThumbPressed);
                break;
            case MotionEvent.ACTION_MOVE:
                seekTo(eventX, eventY, true);
                setThumbState(mThumbPressed);
                break;
            case MotionEvent.ACTION_UP:
                if (onCircleSeekBarChangeListener != null) {
                    onCircleSeekBarChangeListener.onStopTrackingTouch(this);
                }
                seekTo(eventX, eventY, false);
                setThumbState(mThumbNormal);
                break;
        }
        return true;
    }

    /**
     * 将进度根据指针的位置进行更新
     *
     * @param eventX  用户触点的X坐标
     * @param eventY  用户触点的Y坐标
     * @param isTouch 是否正在触摸中
     */
    protected void seekTo(float eventX, float eventY, boolean isTouch) {
//        if (isPointOnThumb(eventX, eventY) && false == isUp) {
        double radian = Math.atan2(eventY - mSeekBarCenterY, eventX - mSeekBarCenterX);
        // 由于atan2返回的值为[-pi,pi]（就是可能为负数）
        //因此需要将弧度值转换一下，使得区间为[0,2*pi]（类似角度如果为负数则+360即可，弧度+2π）
        if (radian < 0) {
            radian = radian + 2 * Math.PI;
        }
        //计算角度
        float _seekBarDegree = (float) Math.toDegrees(radian) - startAngle;
        if (_seekBarDegree < 0) {
            _seekBarDegree += 360f;
        }
        if (_seekBarDegree > sweepAngle) {
            _seekBarDegree = sweepAngle;
        }
        mSeekBarDegree = _seekBarDegree;
        mProgress = (int) (mMax / sweepAngle * mSeekBarDegree);
        setProgress(mProgress, true);
//        }
    }

    protected boolean isPointOnThumb(float eventX, float eventY) {
        boolean result = false;
        double distance = Math.sqrt(Math.pow(eventX - mSeekBarCenterX, 2)
                + Math.pow(eventY - mSeekBarCenterY, 2));
        if (distance < mSeekBarSize && distance > (mSeekBarSize / 2 - mThumbWidth)) {
            result = true;
        }
        return result;
    }

    protected void setThumbPosition(double radian) {
        double x = mSeekBarCenterX + mSeekBarRadius * Math.cos(radian);
        double y = mSeekBarCenterY + mSeekBarRadius * Math.sin(radian);
        mThumbLeft = (float) (x - mThumbWidth / 2);
        mThumbTop = (float) (y - mThumbHeight / 2);
    }

    public int getProgress() {
        return mProgress;
    }

    /*
     * 增加set方法，用于在java代码中调用
     */
    public void setProgress(int progress) {
        setProgress(progress, false);
    }

    protected void setProgress(int progress, boolean fromUser) {
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (mProgress != progress) {
            mProgress = progress;
            if (onCircleSeekBarChangeListener != null) {
                onCircleSeekBarChangeListener.onProgressChanged(this, mProgress, fromUser);
            }
        } else {
            mProgress = progress;
        }
        //计算当前进度代表的角度
        mSeekBarDegree = (sweepAngle / mMax * progress);
        setThumbPosition(Math.toRadians(mSeekBarDegree + startAngle));

        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setProgressThumb(int thumbId) {
        setProgressThumb(getContext().getResources().getDrawable(thumbId));
    }

    public void setProgressThumb(Drawable drawable) {
        this.mThumbDrawable = drawable;
        if (mThumbDrawable != null) {
            mThumbWidth = this.mThumbDrawable.getIntrinsicWidth();
            mThumbHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            mThumbWidth = 0;
            mThumbHeight = 0;
        }
    }

    protected void setThumbState(int[] stateSet) {
        if (mThumbDrawable != null) {
            mThumbDrawable.setState(mThumbPressed);
        }
    }

    public void setProgressStrokeWidth(int width) {
        this.mProgressStrokeWidth = width;
        mProgressPaint.setStrokeWidth(mProgressStrokeWidth);
        mTrackPaint.setStrokeWidth(mProgressStrokeWidth);
    }

    public void setTrackColor(int color) {
        this.mTrackColor = color;
        mTrackPaint.setColor(mTrackColor);
    }

    public void setProgressColor(int color) {
        this.mProgressColor = color;
        mProgressPaint.setColor(mProgressColor);
    }

    public void setProgressTextColor(int color) {
        this.mProgressTextColor = color;
        mProgressTextPaint.setColor(mProgressTextColor);
    }

    public void setProgressTextSize(int size) {
        this.mProgressTextSize = size;
        mProgressTextPaint.setTextSize(mProgressTextSize);
    }

    public void setProgressTextStrokeWidth(int width) {
        this.mProgressTextStrokeWidth = width;
        mProgressTextPaint.setStrokeWidth(mProgressTextStrokeWidth);
    }

    public void setIsShowProgressText(boolean isShow) {
        mIsShowProgressText = isShow;
    }

    public void setOnCircleSeekBarChangeListener(OnCircleSeekBarChangeListener onCircleSeekBarChangeListener) {
        this.onCircleSeekBarChangeListener = onCircleSeekBarChangeListener;
    }

    /**
     * A callback that notifies clients when the progress level has been
     * changed. This includes changes that were initiated by the user through a
     * touch gesture or arrow key/trackball as well as changes that were initiated
     * programmatically.
     */
    public interface OnCircleSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param circleSeekBar The SeekBar whose progress has changed
         * @param progress      The current progress level. This will be in the range 0..max where max
         *                      was set by {@link ProgressBar#setMax(int)}. (The default value for max is 100.)
         * @param fromUser      True if the progress change was initiated by the user.
         */
        void onProgressChanged(CircleSeekBar circleSeekBar, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param circleSeekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(CircleSeekBar circleSeekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param circleSeekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(CircleSeekBar circleSeekBar);
    }
}
