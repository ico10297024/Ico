package ico.ico.widget.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 用户设置垂直和水平布局的分割线，分割线支持4种布局方式
 * {@link DividerItemDecoration#LAYOUT_START,DividerItemDecoration#LAYOUT_CENTER,DividerItemDecoration#LAYOUT_END,DividerItemDecoration#LAYOUT_ALL}
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    //水平布局
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    //垂直布局
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    //在每一个item的前面设置分隔线，没有头线
    public static final int LAYOUT_START = 0;
    //在所有item之间设置分隔线，没有尾线
    public static final int LAYOUT_CENTER = 1;
    //在每一个item的后面设置分隔线，没有头尾线
    public static final int LAYOUT_END = 2;
    //所有item之间、头、尾各一条分割线
    public static final int LAYOUT_ALL = 3;

    //分隔线资源
    private final Drawable mDivider;
    //分隔线的高度/宽度
    private final int mHeight;
    /**
     * 当前的排列方向
     * {@link DividerItemDecoration#HORIZONTAL,DividerItemDecoration#VERTICAL}
     * {@link LinearLayoutManager#HORIZONTAL,LinearLayoutManager#VERTICAL}
     */
    private int mOrientation;

    /**
     * 分隔线的布局方式
     * {@link DividerItemDecoration#LAYOUT_START,DividerItemDecoration#LAYOUT_CENTER,DividerItemDecoration#LAYOUT_END}
     */
    private int mLayout;

    /**
     * 初始函数
     *
     * @param orientation  列表的方向
     *                     {@link DividerItemDecoration#HORIZONTAL,DividerItemDecoration#VERTICAL}
     *                     {@link LinearLayoutManager#HORIZONTAL,LinearLayoutManager#VERTICAL}
     * @param layout       分隔线的布局方式
     *                     {@link DividerItemDecoration#LAYOUT_START,DividerItemDecoration#LAYOUT_CENTER,DividerItemDecoration#LAYOUT_END,DividerItemDecoration#LAYOUT_ALL}
     * @param dividerWidth 分隔线的宽度
     * @param color        颜色值，不是颜色资源值
     */
    public DividerItemDecoration(int orientation, int layout, int dividerWidth, int color) {
        mDivider = new ColorDrawable(color);
        mHeight = dividerWidth;
        setOrientation(orientation);
        setLayout(layout);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    public void setLayout(int layout) {
        if (layout != LAYOUT_START && layout != LAYOUT_CENTER && layout != LAYOUT_END && layout != LAYOUT_ALL) {
            throw new IllegalArgumentException("invalid layout");
        }
        mLayout = layout;
    }

    //设置item的偏移量，为分隔线留出位置
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int l = 0, t = 0, r = 0, b = 0;
        if (mOrientation == VERTICAL) {
            switch (mLayout) {
                case LAYOUT_START:
                    t = mHeight;
                    break;
                case LAYOUT_CENTER:
                    if (parent.getChildAdapterPosition(view) != state.getItemCount() - 1) {
                        b = mHeight;
                    }
                    break;
                case LAYOUT_END:
                    b = mHeight;
                    break;
                case LAYOUT_ALL:
                    t = mHeight;
                    if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                        b = mHeight;
                    }
                    break;
            }
        } else {
            switch (mLayout) {
                case LAYOUT_START:
                    l = mHeight;
                    break;
                case LAYOUT_CENTER:
                    if (parent.getChildAdapterPosition(view) != state.getItemCount() - 1) {
                        r = mHeight;
                    }
                    break;
                case LAYOUT_END:
                    r = mHeight;
                    break;
                case LAYOUT_ALL:
                    l = mHeight;
                    if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                        r = mHeight;
                    }
                    break;
            }
        }
        outRect.set(l, t, r, b);
    }


    //绘制item之前绘制分隔线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        //整个recyclerview可绘制区域的l和r
        int l = parent.getPaddingLeft();
        int r = parent.getWidth() - parent.getPaddingRight();
        //item数量
        int childCount = parent.getChildCount();

        View childView;
        int t = 0;
        int b = 0;

        for (int position = 0; position < childCount; position++) {
            //获取childView
            childView = parent.getChildAt(position);
            //根据分割线的布局方式设置t，b值
            switch (mLayout) {
                case LAYOUT_START:
                case LAYOUT_ALL:
                    t = childView.getTop() - mHeight;
                    b = childView.getTop();
                    break;
                case LAYOUT_CENTER:
                    if (position != childCount - 1) {
                        t = childView.getBottom();
                        b = childView.getBottom() + mHeight;
                    }
                    break;
                case LAYOUT_END:
                    t = childView.getBottom();
                    b = childView.getBottom() + mHeight;
                    break;
            }
            mDivider.setBounds(l, t, r, b);
            mDivider.draw(c);
            //如果是最后一个child&&分隔线布局方式为ALL
            //则需要设置尾线
            if (position == childCount - 1 && mLayout == LAYOUT_ALL) {
                t = childView.getBottom();
                b = childView.getBottom() + mHeight;
            }
            mDivider.setBounds(l, t, r, b);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        //整个recyclerview可绘制区域的t和b
        int t = parent.getPaddingTop();
        int b = parent.getHeight() - parent.getPaddingBottom();
        //item数量
        int childCount = parent.getChildCount();

        View childView;
        int l = 0;
        int r = 0;

        for (int position = 0; position < childCount; position++) {
            //获取childView
            childView = parent.getChildAt(position);
            //根据分割线的布局方式设置l,f值
            switch (mLayout) {
                case LAYOUT_START:
                case LAYOUT_ALL:
                    l = childView.getLeft() - mHeight;
                    r = childView.getLeft();
                    break;
                case LAYOUT_CENTER:
                    if (position != childCount - 1) {
                        l = childView.getRight();
                        r = childView.getRight() + mHeight;
                    }
                    break;
                case LAYOUT_END:
                    l = childView.getRight();
                    r = childView.getRight() + mHeight;
                    break;
            }
            mDivider.setBounds(l, t, r, b);
            mDivider.draw(c);
            //如果是最后一个child&&分隔线布局方式为ALL
            //则需要设置尾线
            if (position == childCount - 1 && mLayout == LAYOUT_ALL) {
                l = childView.getRight();
                r = childView.getRight() + mHeight;
            }
            mDivider.setBounds(l, t, r, b);
            mDivider.draw(c);
        }
    }


}
