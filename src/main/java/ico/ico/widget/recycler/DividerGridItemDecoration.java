package ico.ico.widget.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import ico.ico.util.log;

/**
 * Created by ICO on 2016/4/7 0007.
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    //用来指定分割线的布局方式
    public static final int LAYOUT_ALL = 0x1F;
    public static final int LAYOUT_LEFT = 0x01;
    public static final int LAYOUT_TOP = 0x02;
    public static final int LAYOUT_RIGHT = 0x04;
    public static final int LAYOUT_BOTTOM = 0x08;
    public static final int LAYOUT_CENTER = 0x10;
    //保存所有item的偏移量,以l,t,r,b的顺序
    int[][] itemOffsets;
    int l = 0, t = 1, r = 2, b = 3;
    int mItemTotalWidth;
    int mItemTotalHeight;
    int pos;
    //保存行数列数,当有变更时需要重新计算所有item的偏移量
    private Drawable mDivider;
    private int mDividerWidth = 0;
    private int mDividerHeight = 0;
    private int mDividerColor = 0;
    private int mDividerLayout = 0;
    private Paint paint;

    public DividerGridItemDecoration(Drawable divider) {
        this.mDivider = divider;
        if (this.mDivider != null) {
            mDividerWidth = mDivider.getMinimumWidth();
            mDividerHeight = mDivider.getMinimumHeight();
        }
    }

    /**
     * 初始函数
     *
     * @param layout          设置分割线的布局方式
     *                        {@link DividerGridItemDecoration#LAYOUT_ALL}
     *                        {@link DividerGridItemDecoration#LAYOUT_LEFT}
     *                        {@link DividerGridItemDecoration#LAYOUT_TOP}
     *                        {@link DividerGridItemDecoration#LAYOUT_RIGHT}
     *                        {@link DividerGridItemDecoration#LAYOUT_BOTTOM}
     *                        {@link DividerGridItemDecoration#LAYOUT_CENTER}
     * @param dividerWidth    设置分割线的宽度
     * @param dividerColorInt 设置分割线的颜色，这里是颜色值，不是颜色资源值
     */
    public DividerGridItemDecoration(int layout, int dividerWidth, int dividerColorInt) {
        this.mDividerLayout = layout;
        this.mDividerWidth = dividerWidth;
        this.mDividerHeight = dividerWidth;
        this.mDividerColor = dividerColorInt;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mDividerColor);
        paint.setStrokeWidth(mDividerWidth);
    }

    public DividerGridItemDecoration(int layout, int dividerWidth, int dividerColorInt, int pos) {
        this.mDividerLayout = layout;
        this.mDividerWidth = dividerWidth;
        this.mDividerHeight = dividerWidth;
        this.mDividerColor = dividerColorInt;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mDividerColor);
        paint.setStrokeWidth(mDividerWidth);
        this.pos = pos;
    }

    //设置item的偏移量，为分隔线留出位置
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int itemCount = parent.getAdapter().getItemCount();
        int rowCount = itemCount < spanCount ? 1 : (itemCount - 1) / spanCount + 1;
        //1   1 2 3 4 5
        //2   6 7 8 9 10
        //3   11 12 13 14 15
        if (itemOffsets == null || itemOffsets.length != itemCount) {
            itemOffsets = new int[itemCount][4];

            int itemTotalWidth = 0;//每个item需要承担的左右间距
            int itemTotalHeight = 0;//每个item需要承担的上下间距

            /*首先检查横向有几条间距*/
            int horizontalCount = 0;//横向间距数
            if ((mDividerLayout & LAYOUT_LEFT) == LAYOUT_LEFT) {
                horizontalCount++;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                if (spanCount > 1) {
                    horizontalCount += spanCount - 1;
                }
            }
            if ((mDividerLayout & LAYOUT_RIGHT) == LAYOUT_RIGHT) {
                horizontalCount++;
            }
            //计算所有左右间距总的宽度
            int horizontalTotalWidth = horizontalCount * mDividerWidth;
            //计算每个item需要承担的左右间距
            itemTotalWidth = horizontalTotalWidth / spanCount;

            /*然后检查竖向有几条间距*/
            int verticalCount = 0;//竖向间距数
            if ((mDividerLayout & LAYOUT_TOP) == LAYOUT_TOP) {
                verticalCount++;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                if (rowCount > 1) {
                    verticalCount += rowCount - 1;
                }
            }
            if ((mDividerLayout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM) {
                verticalCount++;
            }
            //计算所有上下间距总的高度
            int verticalTotalHeight = verticalCount * mDividerHeight;
            //计算每个item需要承担的上下间距
            itemTotalHeight = verticalTotalHeight / rowCount;

            //赋值
            mItemTotalWidth = itemTotalWidth;
            mItemTotalHeight = itemTotalHeight;
        }
        int position = parent.getChildAdapterPosition(view);





        /*已算出每个item需要承担的上下间距和左右间距*/
        //横向
        if ((position + spanCount) % spanCount == 0) {//最左侧一列
            if ((mDividerLayout & LAYOUT_LEFT) == LAYOUT_LEFT) {
                itemOffsets[position][l] = mDividerWidth;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][r] = mItemTotalWidth - itemOffsets[position][l];
            }
        } else if ((position + 1) % spanCount == 0) {//最右侧一列
            if ((mDividerLayout & LAYOUT_RIGHT) == LAYOUT_RIGHT) {
                itemOffsets[position][r] = mDividerWidth;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][l] = mItemTotalWidth - itemOffsets[position][r];
            }
        } else {
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][l] = mDividerWidth - itemOffsets[position - 1][r];
                itemOffsets[position][r] = mItemTotalWidth - itemOffsets[position][l];
            }
        }
        //竖向
        if (position < spanCount) {//最顶部一行
            if ((mDividerLayout & LAYOUT_TOP) == LAYOUT_TOP) {
                itemOffsets[position][t] = mDividerHeight;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][b] = mItemTotalHeight - itemOffsets[position][t];
            }
        } else if (rowCount == 1 || position >= (rowCount - 1) * spanCount) {//最下面那行
            if ((mDividerLayout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM) {
                itemOffsets[position][b] = mDividerHeight;
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][t] = mItemTotalHeight - itemOffsets[position][b];
            }
        } else {
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                itemOffsets[position][t] = mDividerHeight - itemOffsets[position - spanCount][b];
                itemOffsets[position][b] = mItemTotalHeight - itemOffsets[position][t];
            }
        }

        if (itemOffsets[position][l] < 0) {
            itemOffsets[position][l] = 0;
        }
        if (itemOffsets[position][t] < 0) {
            itemOffsets[position][t] = 0;
        }
        if (itemOffsets[position][r] < 0) {
            itemOffsets[position][r] = 0;
        }
        if (itemOffsets[position][b] < 0) {
            itemOffsets[position][b] = 0;
        }

        log.e("getItemOffsets:" + position + "===" + itemCount + "===" + spanCount + "========" + itemOffsets[position][l] + "|" + itemOffsets[position][t] + "|" + itemOffsets[position][r] + "|" + itemOffsets[position][b]);
        outRect.set(itemOffsets[position][l], itemOffsets[position][t], itemOffsets[position][r], itemOffsets[position][b]);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int itemCount = parent.getAdapter().getItemCount();
        int childCount = parent.getChildCount();
        for (int position = 0; position < childCount; position++) {
            if (position == childCount) {
                break;
            }
            final View child = parent.getChildAt(position);
            RectF[] rectF = new RectF[4];
            if ((mDividerLayout & LAYOUT_LEFT) == LAYOUT_LEFT) {
                //竖向-首列-左缩进
                if ((position + spanCount) % spanCount == 0) {
                    rectF[0] = new RectF(child.getLeft() - mDividerWidth, child.getTop(), child.getLeft(), child.getBottom());
                }
            }
            if ((mDividerLayout & LAYOUT_TOP) == LAYOUT_TOP) {
                //横向-首行-上缩进
                if (position < spanCount) {
                    rectF[1] = new RectF(child.getLeft(), child.getTop() - mDividerWidth, child.getRight(), child.getTop());
                }
            }
            if ((mDividerLayout & LAYOUT_RIGHT) == LAYOUT_RIGHT) {
                //竖向-尾列-右缩进
                if ((position + spanCount) % spanCount == spanCount - 1) {
                    rectF[2] = new RectF(child.getRight(), child.getTop(), child.getRight() + mDividerWidth, child.getBottom());
                }
            }
            if ((mDividerLayout & LAYOUT_BOTTOM) == LAYOUT_BOTTOM) {
                //横向-尾行-下缩进
                if (itemCount < spanCount
                        || (itemCount / spanCount) * spanCount <= position) {
                    rectF[3] = new RectF(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom() + mDividerWidth);
                }
            }
            if ((mDividerLayout & LAYOUT_CENTER) == LAYOUT_CENTER) {
                //先考虑竖列，除了最右侧一列，其它列全部右边缩进
                if ((position + spanCount) % spanCount != spanCount - 1) {
                    rectF[2] = new RectF(child.getRight(), child.getTop(), child.getRight() + mDividerWidth, child.getBottom());
                }
                //再考虑横列，除了最底部一行，其它行全部下边缩进
                if (itemCount > spanCount
                        && (itemCount / spanCount) * spanCount > position) {
                    rectF[3] = new RectF(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom() + mDividerWidth);
                }
            }
//            log.e(String.format("onDraw:%d===%d========%s|%s|%s|%s", position, itemCount, (rectF[0] != null) + "", (rectF[1] != null) + "", (rectF[2] != null) + "", (rectF[3] != null) + ""), pos + "");
            //绘制
            for (int i = 0; i < 4; i++) {
                if (rectF[i] != null) {
                    c.drawRect(rectF[i], paint);
                }
            }
            //最后补齐周边四个角
            if (rectF[0] != null) {
                //左上
                if (rectF[1] != null) {
                    c.drawRect(new RectF(rectF[0].left, rectF[1].top, rectF[0].right, rectF[1].bottom), paint);
                }
                //左下
                if (rectF[3] != null) {
                    c.drawRect(new RectF(rectF[0].left, rectF[3].top, rectF[0].right, rectF[3].bottom), paint);
                }
            }
            if (rectF[2] != null) {
                //右上
                if (rectF[1] != null) {
                    c.drawRect(new RectF(rectF[2].left, rectF[1].top, rectF[2].right, rectF[1].bottom), paint);
                }
                //右下
                if (rectF[3] != null) {
                    c.drawRect(new RectF(rectF[2].left, rectF[3].top, rectF[2].right, rectF[3].bottom), paint);
                }
            }

        }

    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }
}
