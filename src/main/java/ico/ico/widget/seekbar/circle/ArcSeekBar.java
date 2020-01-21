package ico.ico.widget.seekbar.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * 弧形的SeekBar
 *
 * @author lee
 */

public class ArcSeekBar extends CircleSeekBar {


    public ArcSeekBar(Context context) {
        this(context, null);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //轨道
        canvas.drawArc(this.mArcRectF, startAngle, sweepAngle, false, mTrackPaint);
        //进度
        canvas.drawArc(this.mArcRectF, startAngle, mSeekBarDegree, false, mProgressPaint);
        //滑块
        drawThumbBitmap(canvas);
        drawProgressText(canvas);
    }
}
