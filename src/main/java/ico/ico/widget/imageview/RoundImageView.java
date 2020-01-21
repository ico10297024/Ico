package ico.ico.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import ico.ico.ico.R;

/**
 * 基于画布裁剪完成的圆角图片显示
 */
public class RoundImageView extends ImageView {
    //绘制范围
    protected RectF drawRect;
    //圆角属性
    private float radius = -1;
    private float topLeftRadius = 0;
    private float topRightRadius = 0;
    private float bottomLeftRadius = 0;
    private float bottomRightRadius = 0;
    private float[] mRadius = new float[8];

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        //获取关于圆角的属性
        radius = typedArray.getDimension(R.styleable.RoundImageView_riv_corner_radius, -1);
        topLeftRadius = typedArray.getDimension(R.styleable.RoundImageView_riv_corner_radius_top_left, 0);
        topRightRadius = typedArray.getDimension(R.styleable.RoundImageView_riv_corner_radius_top_right, 0);
        bottomLeftRadius = typedArray.getDimension(R.styleable.RoundImageView_riv_corner_radius_bottom_right, 0);
        bottomRightRadius = typedArray.getDimension(R.styleable.RoundImageView_riv_corner_radius_bottom_left, 0);
        //若全局没有设置，则获取单个角的
        if (radius > -1) {
            for (int i = 0; i < mRadius.length; i++) {
                mRadius[i] = radius;
            }
        } else {
            mRadius[0] = topLeftRadius;
            mRadius[1] = topLeftRadius;
            mRadius[2] = topRightRadius;
            mRadius[3] = topRightRadius;
            mRadius[4] = bottomLeftRadius;
            mRadius[5] = bottomLeftRadius;
            mRadius[6] = bottomRightRadius;
            mRadius[7] = bottomRightRadius;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = getWidth() - getPaddingRight();
        int b = getHeight() - getPaddingBottom();
        drawRect = new RectF(l, t, r, b);
    }

    /**
     * 绘制圆角矩形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(drawRect, mRadius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w;
        int h;

        // Desired aspect ratio of the view's contents (not including padding)
        float desiredAspect = 0.0f;

        // We are allowed to change the view's width
        boolean resizeWidth = false;

        // We are allowed to change the view's height
        boolean resizeHeight = false;

        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        Drawable mDrawable = getDrawable();
        boolean mAdjustViewBounds = getAdjustViewBounds();
        int mMaxWidth = getMaxWidth();
        int mMaxHeight = getMaxHeight();
        if (mDrawable == null) {
            // If no drawable, its intrinsic size is 0.
//            mDrawableWidth = -1;
//            mDrawableHeight = -1;
            w = h = 0;
        } else {
            w = mDrawable.getIntrinsicWidth();
            h = mDrawable.getIntrinsicHeight();
            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }

            // We are supposed to adjust view bounds to match the aspect
            // ratio of our drawable. See if that is possible.
            if (mAdjustViewBounds) {
                resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

                desiredAspect = (float) w / (float) h;
            }
        }

        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        int widthSize;
        int heightSize;

        if (resizeWidth || resizeHeight) {
            /* If we get here, it means we want to resize to match the
                drawables aspect ratio, and we have the freedom to change at
                least one dimension.
            */

            // Get the max possible width given our constraints
            widthSize = resolveAdjustedSize(w + pleft + pright, mMaxWidth, widthMeasureSpec);

            // Get the max possible height given our constraints
            heightSize = resolveAdjustedSize(h + ptop + pbottom, mMaxHeight, heightMeasureSpec);

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                float actualAspect = (float) (widthSize - pleft - pright) /
                        (heightSize - ptop - pbottom);

                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

                    boolean done = false;

                    // Try adjusting width to be proportional to height
                    if (resizeWidth) {
                        int newWidth = (int) (desiredAspect * (heightSize - ptop - pbottom)) +
                                pleft + pright;

                        // Allow the width to outgrow its original estimate if height is fixed.
                        if (!resizeHeight) {
                            widthSize = resolveAdjustedSize(newWidth, mMaxWidth, widthMeasureSpec);
                        }

                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            done = true;
                        }
                    }

                    // Try adjusting height to be proportional to width
                    if (!done && resizeHeight) {
                        int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect) +
                                ptop + pbottom;

                        // Allow the height to outgrow its original estimate if width is fixed.
                        if (!resizeWidth) {
                            heightSize = resolveAdjustedSize(newHeight, mMaxHeight, heightMeasureSpec);
                        }

                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            /* We are either don't want to preserve the drawables aspect ratio,
               or we are not allowed to change view dimensions. Just measure in
               the normal way.
            */
            w += pleft + pright;
            h += ptop + pbottom;

            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize,
                                    int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize);
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

}
