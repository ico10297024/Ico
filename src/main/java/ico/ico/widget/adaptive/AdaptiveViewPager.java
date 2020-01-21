package ico.ico.widget.adaptive;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 在ScrollView嵌套ViewPager时，若不设置具体的高度，则ViewPager不会显示
 * 该视图控件可以在嵌套的环境中根据当前显示内容自适应高度，不过需要在页面切换时手动调用postInvalidate();
 */
public class AdaptiveViewPager extends ViewPager {

    public AdaptiveViewPager(Context context) {
        this(context, null);
    }

    public AdaptiveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int i = getCurrentItem();
        View child = getChildAt(i);
        if (child != null) {
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {
                height = h;
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
