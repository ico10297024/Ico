package ico.ico.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import ico.ico.util.Common;

/**
 * Created by root on 18-5-28.
 */

public class BitmapHelper {

    private Context mContext;
    private Bitmap mBitmap;
    private Paint mPaint;
    private float mDensity;

    public BitmapHelper(Context context, int resId) {
        this.mContext = context;
        //加载图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);
        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextSize(Common.sp2px(mContext, 16));
        //初始化密度
        mDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 设置文本的颜色,默认{@link Color#BLACK}
     *
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 设置文本字体的大小,单位sp,默认22
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        mPaint.setTextSize(sp2px(mContext, textSize));
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * 在指定x,y坐标处绘制文本
     *
     * @param x    X轴坐标
     * @param y    Y轴坐标
     * @param text 需要绘制的文本
     */
    public void drawText(float x, float y, String text) {
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawText(text, x, y, mPaint);
    }


    /**
     * 在指定x,y坐标处绘制图片
     *
     * @param x     X轴坐标
     * @param y     Y轴坐标
     * @param resId 需要绘制的图片ID
     */
    public void drawBitmap(float x, float y, int resId) {
        //加载图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawBitmap(mBitmap, x, y, mPaint);
    }


    /**
     * 在指定x,y坐标处绘制文本,x,y值将与密度进行计算
     *
     * @param x    X轴坐标
     * @param y    Y轴坐标
     * @param text 需要绘制的文本
     */
    public void drawTextOffset(float x, float y, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawText(text, x * mDensity, y * mDensity, mPaint);
    }

    /**
     * 在指定x,y坐标处绘制文本,x,y值将与密度进行计算
     *
     * @param x      X轴坐标
     * @param y      Y轴坐标
     * @param bitmap 需要绘制的图片ID
     */
    public void drawBitmapOffset(float x, float y, Bitmap bitmap) {
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawBitmap(bitmap, x * mDensity, y * mDensity, mPaint);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
