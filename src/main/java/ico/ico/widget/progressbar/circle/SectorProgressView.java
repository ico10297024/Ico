package ico.ico.widget.progressbar.circle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import ico.ico.ico.R;

/**
 * Created by qiqi on 15/9/17.
 */
public class SectorProgressView extends View {
    /**
     * 绘制区域的模式
     * {@link #SCALE_NONE}  以默认的绘制区域进行绘制
     * {@link #SCALE_SQUARE} 以正方形的绘制区域进行绘制
     */
    public final static int SCALE_NONE = 0;
    public final static int SCALE_SQUARE = 1;
    private int bgColor;
    private int fgColor;
    private Paint bgPaint;
    private Paint fgPaint;
    private RectF oval;
    private float progress = 0;
    private float max = 100;
    private float startAngle = 0;

    /**
     * 绘制格式化
     * {@link #SCALE_NONE}  以默认的绘制区域进行绘制
     * {@link #SCALE_SQUARE} 以正方形的绘制区域进行绘制
     */
    private int scale = SCALE_NONE;

    public SectorProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SectorProgressView, 0, 0);

        try {
            scale = a.getInt(R.styleable.SectorProgressView_scale, scale);
            bgColor = a.getColor(R.styleable.SectorProgressView_bgColor, 0xffe5e5e5);
            fgColor = a.getColor(R.styleable.SectorProgressView_fgColor, 0xffff765c);
            progress = a.getFloat(R.styleable.SectorProgressView_android_progress, progress);
            max = a.getFloat(R.styleable.SectorProgressView_android_max, max);
            startAngle = a.getFloat(R.styleable.SectorProgressView_startAngle, startAngle);
        } finally {
            a.recycle();
        }

        initPaint();
    }

    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);

        fgPaint = new Paint();
        fgPaint.setAntiAlias(true);
        fgPaint.setColor(fgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updateOval();
        /*float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingBottom() + getPaddingTop());

        float wwd = (float) w - xpad;
        float hhd = (float) h - ypad;

        oval = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + wwd, getPaddingTop() + hhd);*/
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
        oval = new RectF(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(oval, 0, 360, true, bgPaint);
        canvas.drawArc(oval, startAngle, progress / max * 360f, true, fgPaint);
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        initPaint();
        refreshTheLayout();
    }

    public int getFgColor() {
        return fgColor;
    }

    public void setFgColor(int fgColor) {
        this.fgColor = fgColor;
        initPaint();
        refreshTheLayout();
    }

    private void refreshTheLayout() {
        invalidate();
        requestLayout();
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidate();
        requestLayout();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
        requestLayout();
    }

    public float getMax() {
        return max;
    }

    public SectorProgressView setMax(float max) {
        this.max = max;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public SectorProgressView setScale(int scale) {
        this.scale = scale;
        return this;
    }
}
