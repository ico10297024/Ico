package ico.ico.widget.progressbar.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import ico.ico.ico.R;

/**
 * Created by qiqi on 15/11/3.
 */
public class ColorfulRingProgressView extends View {

    /**
     * 默认的背景色和前景色
     */
    public final static int DEFAULT_BG_COLOR = Color.RED;
    public final static int DEFAULT_FG_COLOR = Color.YELLOW;
    /**
     * 绘制区域的模式
     * {@link #SCALE_NONE}  以默认的绘制区域进行绘制
     * {@link #SCALE_SQUARE} 以正方形的绘制区域进行绘制
     */
    public final static int SCALE_NONE = 0;
    public final static int SCALE_SQUARE = 1;


    private float progress = 0;
    private float max = 100;
    private float strokeWidth;
    private float startAngle = 0;
    private int strokeCap = 0;


    /**
     * 背景色
     */
    private int bgColorStart = -1;
    private int bgColorMiddle = -1;
    private int bgColorEnd = -1;
    private int[] bgColors;
    private LinearGradient mBgShader;

    /**
     * 前景色
     */
    private int fgColorStart = -1;
    private int fgColorMiddle = -1;
    private int fgColorEnd = -1;
    private int[] fgColors;
    private LinearGradient mFgShader;

    /**
     * 绘制格式化
     * {@link #SCALE_NONE}  以默认的绘制区域进行绘制
     * {@link #SCALE_SQUARE} 以正方形的绘制区域进行绘制
     */
    private int scale = SCALE_NONE;

    private Context mContext;
    private RectF mOval;
    private Paint mPaint;


    public ColorfulRingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulRingProgressView, 0, 0);

        try {
            bgColorStart = a.getColor(R.styleable.ColorfulRingProgressView_bgColorStart, bgColorStart);
            bgColorMiddle = a.getColor(R.styleable.ColorfulRingProgressView_bgColorMiddle, bgColorMiddle);
            bgColorEnd = a.getColor(R.styleable.ColorfulRingProgressView_bgColorEnd, bgColorEnd);

            fgColorStart = a.getColor(R.styleable.ColorfulRingProgressView_fgColorStart, fgColorStart);
            fgColorMiddle = a.getColor(R.styleable.ColorfulRingProgressView_fgColorMiddle, fgColorMiddle);
            fgColorEnd = a.getColor(R.styleable.ColorfulRingProgressView_fgColorEnd, fgColorEnd);

            scale = a.getInt(R.styleable.ColorfulRingProgressView_scale, scale);
            progress = a.getFloat(R.styleable.ColorfulRingProgressView_android_progress, progress);
            max = a.getFloat(R.styleable.ColorfulRingProgressView_android_max, max);
            startAngle = a.getFloat(R.styleable.ColorfulRingProgressView_startAngle, startAngle);
            strokeWidth = a.getDimensionPixelSize(R.styleable.ColorfulRingProgressView_strokeWidth, dp2px(21));
            strokeCap = a.getInt(R.styleable.ColorfulRingProgressView_strokeCap, strokeCap);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        if (strokeCap == 1) {
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //轨道
        mPaint.setShader(null);

        if (mBgShader != null) {
            mPaint.setShader(mBgShader);
        } else {
            mPaint.setColor(DEFAULT_BG_COLOR);
        }
        canvas.drawArc(mOval, 0, 360, false, mPaint);

        //滑块
        mPaint.setShader(null);
        if (mFgShader != null) {
            mPaint.setShader(mFgShader);
        } else {
            mPaint.setColor(DEFAULT_FG_COLOR);
        }
        canvas.drawArc(mOval, startAngle, progress / max * 360, false, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateOval();
        initShader();
    }

    private void updateOval() {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = getWidth() - getPaddingRight();
        int b = getHeight() - getPaddingBottom();
        int w = r - l;
        int h = b - t;
        if (scale == SCALE_SQUARE) {
            int min = Math.min(w, h);
            l = (getWidth() - min) / 2;
            t = (getHeight() - min) / 2;
            r = l + min;
            b = t + min;
        }
        mOval = new RectF(l + strokeWidth / 2, t + strokeWidth / 2, r - strokeWidth / 2, b - strokeWidth / 2);
    }

    private void initShader() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (fgColors == null) {
            if (fgColorStart != -1) {
                list.add(fgColorStart);
            }
            if (fgColorMiddle != -1) {
                list.add(fgColorMiddle);
            }
            if (fgColorEnd != -1) {
                list.add(fgColorEnd);
            }
            if (list.size() == 0) {
                fgColors = new int[]{DEFAULT_FG_COLOR};
            } else {
                fgColors = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    fgColors[i] = list.get(i);
                }
            }
        }
        list.clear();
        if (bgColors == null) {
            if (bgColorStart != -1) {
                list.add(bgColorStart);
            }
            if (bgColorMiddle != -1) {
                list.add(bgColorMiddle);
            }
            if (bgColorEnd != -1) {
                list.add(bgColorEnd);
            }
            if (list.size() == 0) {
                bgColors = new int[]{DEFAULT_BG_COLOR};
            } else {
                bgColors = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    bgColors[i] = list.get(i);
                }
            }
        }

        if (fgColors.length > 2) {
            mFgShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, fgColors, null, Shader.TileMode.MIRROR);
        } else if (fgColors.length == 2) {
            mFgShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, fgColors[0], fgColors[1], Shader.TileMode.MIRROR);
        } else {
            mFgShader = null;
        }

        if (bgColors.length > 2) {
            mBgShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, bgColors, null, Shader.TileMode.MIRROR);
        } else if (bgColors.length == 2) {
            mBgShader = new LinearGradient(mOval.left, mOval.top, mOval.left, mOval.bottom, bgColors[0], bgColors[1], Shader.TileMode.MIRROR);
        } else {
            mBgShader = null;
        }

    }

    private int dp2px(float dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }


    public void refreshTheLayout() {
        updateOval();
        initShader();
        invalidate();
        requestLayout();
    }


    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
        updateOval();
        refreshTheLayout();
    }


    public void setStrokeWidthDp(float dp) {
        this.strokeWidth = dp2px(dp);
        mPaint.setStrokeWidth(strokeWidth);
        updateOval();
        refreshTheLayout();
    }


    public float getMax() {
        return max;
    }

    public ColorfulRingProgressView setMax(float max) {
        this.max = max;
        return this;
    }

    public float getProgress() {
        return progress;
    }

    public ColorfulRingProgressView setProgress(float progress) {
        this.progress = progress;
        refreshTheLayout();
        return this;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public ColorfulRingProgressView setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        refreshTheLayout();
        return this;
    }

    public int getBgColorStart() {
        return bgColorStart;
    }

    public ColorfulRingProgressView setBgColorStart(int bgColorStart) {
        this.bgColorStart = bgColorStart;
        setBgColors(null);
        return this;
    }

    public int getBgColorMiddle() {
        return bgColorMiddle;
    }

    public ColorfulRingProgressView setBgColorMiddle(int bgColorMiddle) {
        this.bgColorMiddle = bgColorMiddle;
        setBgColors(null);
        return this;
    }

    public int getBgColorEnd() {
        return bgColorEnd;
    }

    public ColorfulRingProgressView setBgColorEnd(int bgColorEnd) {
        this.bgColorEnd = bgColorEnd;
        setBgColors(null);
        return this;
    }

    public int[] getBgColors() {
        return bgColors;
    }

    public ColorfulRingProgressView setBgColors(int[] bgColors) {
        this.bgColors = bgColors;
        refreshTheLayout();
        return this;
    }

    public int getFgColorStart() {
        return fgColorStart;
    }

    public ColorfulRingProgressView setFgColorStart(int fgColorStart) {
        this.fgColorStart = fgColorStart;
        setFgColors(null);
        return this;
    }

    public int getFgColorMiddle() {
        return fgColorMiddle;
    }

    public ColorfulRingProgressView setFgColorMiddle(int fgColorMiddle) {
        this.fgColorMiddle = fgColorMiddle;
        setFgColors(null);
        return this;
    }

    public int getFgColorEnd() {
        return fgColorEnd;
    }

    public ColorfulRingProgressView setFgColorEnd(int fgColorEnd) {
        this.fgColorEnd = fgColorEnd;
        setFgColors(null);
        return this;
    }

    public int[] getFgColors() {
        return fgColors;
    }

    public ColorfulRingProgressView setFgColors(int[] fgColors) {
        this.fgColors = fgColors;
        refreshTheLayout();
        return this;
    }

    public int getScale() {
        return scale;
    }

    public ColorfulRingProgressView setScale(int scale) {
        this.scale = scale;
        return this;
    }

}
